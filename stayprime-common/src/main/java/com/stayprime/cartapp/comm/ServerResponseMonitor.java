/*
 * 
 */
package com.stayprime.cartapp.comm;

import com.stayprime.cartapp.CartAppConst;
import com.stayprime.comm.BytePacket;
import com.stayprime.comm.PacketComm;
import com.stayprime.comm.encoder.GolfCartEncoder;
import com.stayprime.comm.encoder.PacketType;
import com.stayprime.device.Time;
import java.util.concurrent.TimeUnit;
import org.apache.commons.configuration.PropertiesConfiguration;

/**
 *
 * @author benjamin
 */
public class ServerResponseMonitor {
    //Keep track of the last server response time to try to recover connection
    private boolean restartConnection = false;
    private long lastResponse = 0;
    private long timerStart = 0;
    private long requestAckTimeout = TimeUnit.MINUTES.toMillis(10);
    private long noReplyTimeout = TimeUnit.MINUTES.toMillis(12);
    private boolean ackRequestSent = false;

    private Runnable action;

    public ServerResponseMonitor(Runnable action) {
        this.action = action;
    }

    public void updateConfig(PropertiesConfiguration config) {
        this.restartConnection = config.getBoolean(CartAppConst.GPRS_RECONNECT, true);
   }

    public boolean checkAndEncodeAckRequest(BytePacket packet) {
        long timeLimit = timerStart + requestAckTimeout;
        if (Time.milliTime() > timeLimit) {
            //Encode the request for ack if there hasn't been any server response
            return GolfCartEncoder.encodePreMsg(packet, PacketType.REQUEST_ACK);
        }
        return false;
    }

    public void ackRequestSent() {
        ackRequestSent = true;
    }

    public boolean isRequestAckTime() {
        return Time.milliTime() >= timerStart + requestAckTimeout;
    }

    public long getLastServerResponse() {
        return lastResponse;
    }

    public void checkLastServerResponse() {
        long serverResponseTimeout = timerStart + noReplyTimeout;
        if (ackRequestSent && Time.milliTime() > serverResponseTimeout) {
            GPRSMobileComm.log.debug("No server response");
            timerStart = Time.milliTime();
            ackRequestSent = false;

            if (restartConnection) {
                action.run();
            }
        }
    }

    public void packetReceived() {
        lastResponse = Time.milliTime();
        timerStart = lastResponse;
    }

}
