/*
 *
 */
package com.stayprime.comm.gprs.request;

import com.stayprime.comm.BytePacket;
import com.stayprime.comm.PacketSender;
import com.stayprime.comm.encoder.Packet;
import com.stayprime.golf.message.Message;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Synchronizes requests, doing the actual sending and retrying on a single thread.
 * 1. This RequestHandler thread waits on a blocking queue for any requests.
 * 2. When a request is added (from any thread), we push it to the blocking queue.
 * 3. The RequestHandler gets the request from the queue and tries to send it.
 * 4. If the request is completed immediately (does not require ack), it's discarded.
 * 5. Otherwise the request gets added to a list to be retried or processed later.
 * @author benjamin
 */
public class RequestHandler extends Thread {
    static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private volatile PacketSender packetSender;

    //Blocking queue to pass new requests to the request handler
    private final LinkedBlockingQueue<Request> queue;

    //Synchronized list of pending requests
    private final List<Request> pendingRequests;

    //Stats of successful and failed requests
    private final RequestStats requestStats;

    //Send buffer to avoid creating one buffer per pending request
    private final BytePacket bytePacket;

    private volatile boolean running = false;

    public RequestHandler(PacketSender packetSender) {
        this.packetSender = packetSender;
        this.queue = new LinkedBlockingQueue<Request>();
        this.pendingRequests = Collections.synchronizedList(new ArrayList<Request>());
        this.bytePacket = new BytePacket();

        //Keep here for now, needs to be kept higher in the hierarchy
        requestStats = new RequestStats();
    }

    public void setPacketComm(PacketSender packetComm) {
        this.packetSender = packetComm;
    }

    public void cancelMatchingRequests(Packet packet) {
        synchronized (pendingRequests) {
            for (Request request : pendingRequests) {
                request.cancelMatching(packet);
            }
        }
    }

    public void cancelMatchingRequests(int type) {
        synchronized (pendingRequests) {
            for (Request request : pendingRequests) {
                request.cancelMatching(type);
            }
        }
    }

    public boolean ackReceived(Packet<Message> packet) {
        synchronized (pendingRequests) {
            for (Request request : pendingRequests) {
                return request.processAck(packet);
            }
        }
        return false;
    }

    public void addRequest(Request request) {
        try {
            cancelMatchingRequests(request.getPacket());
            queue.offer(request);
            //need to have callback mechanism to report status to caller,
            //can be done either through a request listener, or by passing
            //the callback into this method.
        }
        catch (Exception ex) {
            //throw app exception
        }
    }

    public void stopRequestHandler() {
        running = false;
    }

    @Override
    public void run() {
        running = true;
        while (running) {
            //1. Wait for new requests, or continue after one second
            Request newRequest = null;
            try {
                newRequest = queue.poll(1, TimeUnit.SECONDS);
            }
            catch (InterruptedException ex) {
            }

            //2. Process new request immediately, if any
            if (newRequest != null) {
                retryRequest(newRequest);

                if (newRequest.isComplete() == false) {
                    pendingRequests.add(newRequest);
                }
            }

            //3. Process list of pending requests, remove the finished ones
            for (int i = pendingRequests.size() - 1; i >= 0; i--) {
                Request request = pendingRequests.get(i);
                retryRequest(request);

                if (request.isComplete()) {
                    log.info("Request completed: " + request);
                    requestStats.requestCompleted(request);
                    pendingRequests.remove(i);
                }
                else if (request.isCanceled()) {
                    log.info("Request canceled: " + request);
                    requestStats.requestCanceled(request);
                    pendingRequests.remove(i);
                }
                else if (request.isFailed()) {
                    log.info("Request failed: " + request);
                    requestStats.requestFailed(request);
                    //Remove for now, but for Scorecard request, we need to keep
                    //trying later, and save it for sending through WiFi later
                    pendingRequests.remove(i);
                }
            }
        }
    }

    private void retryRequest(Request request) {
        try {
            PacketSender comm = packetSender;
            if (comm != null) {
                boolean retried = request.retry(packetSender, bytePacket);
                if (retried) {
                    log.info("Retried request: " + request);
                }
            }
        }
        catch (Exception ex) {
            log.error("Exception trying to retry request [" + request + "]: " + ex);
        }
    }

}
