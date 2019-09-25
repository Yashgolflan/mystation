/*
 * 
 */
package com.stayprime.comm.gprs.request;

import com.stayprime.comm.BytePacket;
import com.stayprime.comm.PacketSender;
import com.stayprime.comm.encoder.Packet;
import com.stayprime.comm.encoder.PacketType;
import com.stayprime.device.Time;
import com.stayprime.golf.message.Message;
import com.stayprime.golf.message.Payload;
import java.net.SocketAddress;

/**
 *
 * @author benjamin
 * @param <T> Payload class type
 */
public class Request<T extends Payload> {
    protected Packet<T> packet;

    protected int sendRetries = 3;

    protected int retryInterval = 10000;

    private long lastRetry = 0;

    protected boolean complete = false;

    protected boolean canceled = false;

    protected boolean failed = false;

    protected RequestObserver<T> requestObserver;

    private SocketAddress address;

    private Packet<Message> ack;

    public Request(Packet<T> packet) {
        this(packet, null);
    }

    public Request(Packet<T> packet, SocketAddress address) {
        this.packet = packet;
        this.address = address;
    }

    public void setAddress(SocketAddress address) {
        this.address = address;
    }

    public SocketAddress getAddress() {
        return address;
    }

    public Packet<T> getPacket() {
        return packet;
    }

    public boolean retry(PacketSender packetComm, BytePacket bytePacket) {
        if (complete == false && canceled == false && failed == false) {
            if (sendRetries > 0) {
                long now = Time.milliTime();
                long nextRetry = lastRetry == 0? now : lastRetry + retryInterval;

                if (now >= nextRetry) {
                    lastRetry = now;
                    sendRetries--;
                    boolean sent = send(packetComm, bytePacket);

                    if (sent) {
                        requestObserver.requestSent(this);
                    }
                    return true;
                }
            }
            else {
                fail();
            }
        }

        return false;
    }

    private boolean send(PacketSender packetSender, BytePacket bytePacket) {
        try {
            packet.encode(bytePacket);
            if (address != null) {
                return packetSender.sendPacket(bytePacket, address);
            }
            else {
                return packetSender.sendPacket(bytePacket);
            }
        }
        catch (Exception ex) {
        }

        return false;
    }

    public boolean isComplete() {
        return complete;
    }

    public boolean isCanceled() {
        return canceled;
    }

    public boolean isFailed() {
        return failed;
    }

    public void setObserver(RequestObserver ro) {
        this.requestObserver = ro;
    }

    public boolean processAck(Packet<Message> ackPacket) {
        if (ackPacket.getPacketType() == PacketType.ACK.id) {
            Message ackMsg = ackPacket.getPayload();

            int requestType = packet.getPacketType();
            int msgCounter = packet.getMessageCounter();

            if (ackMsg.getMessageCode() == requestType
                && ackPacket.getMessageCounter() == msgCounter ) {
                complete(ackPacket);
                return true;
            }
        }
        return false;
    }

    protected void complete(Packet<Message> p) {
        if (complete == false) {
            complete = true;
            this.ack = p;
            requestObserver.requestComplete(this, p.getPayload());
        }
    }

    /**
     * Cancel requests with the same packet type, so that only one request
     * of each type can be active at any time.
     * @param p packet to compare to
     * @return true if the request matched and was canceled
     */
    public boolean cancelMatching(Packet p) {
        return cancelMatching(p.getPacketType());
    }

    public boolean cancelMatching(int type) {
        if (type == packet.getPacketType() && complete == false) {
            cancel();
            return true;
        }
        return false;
    }

    protected void cancel() {
        canceled = true;
        requestObserver.requestCanceled(this);
    }

    private void fail() {
        failed = true;
        requestObserver.requestFailed(this);
    }

    @Override
    public String toString() {
        return "Request payload: [" + packet.getPayload()
                + "] sendRetries: " + sendRetries;
    }

}
