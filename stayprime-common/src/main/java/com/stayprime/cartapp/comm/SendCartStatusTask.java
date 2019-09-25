/*
 * 
 */
package com.stayprime.cartapp.comm;

import com.stayprime.cartapp.CartStatus;
import com.stayprime.cartapp.comm.ServerResponseMonitor;
import com.stayprime.comm.BytePacket;
import com.stayprime.comm.PacketComm;
import com.stayprime.comm.encoder.GolfCartEncoder;
import com.stayprime.comm.encoder.PacketType;
import com.stayprime.device.Time;
import com.stayprime.geo.Coordinates;
import com.stayprime.geo.EarthGeoCalculations;
import com.stayprime.golf.course.GolfHole;
import com.stayprime.golf.course.Site;
import com.stayprime.golfapp.round.RoundManager;
import com.stayprime.golfapp.round.RoundState;
import com.stayprime.model.golf.CartAppMode;
import com.stayprime.model.golf.GolfCart;
import com.stayprime.model.golf.Position;
import com.stayprime.util.FormatUtils;
import com.stayprime.util.task.Task;
import java.util.Date;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.stayprime.cartapp.CartAppStatus;

/**
 *
 * @author benjamin
 */
public class SendCartStatusTask implements Task {
    private static final Logger log = LoggerFactory.getLogger(SendCartStatusTask.class);

    private final GPRSMobileComm comm;
    private final PacketComm packetComm;
    private final ServerResponseMonitor serverResponseMonitor;

    private int minPacketDelay = 30;
    private int maxPacketDelay = 300;
    private float distanceThreshold = 10;

    private final BytePacket packet;
    private final Position origin;
    private final GolfCart golfCart;

    private int currentHoleIndex;
    private int viewHole;
    private int paceOfPlay;

    private volatile boolean sendImmediately;
    private final Position lastPositionSent;
    private long lastPositionSentTime = 0;
    private byte trackingMessageCounter = 0;

    private String taskStatus = StringUtils.EMPTY;
    //TODO replace RoundManager here with model
    private RoundManager roundManager;

    private CartAppStatus cartStatus;

    public SendCartStatusTask(GPRSMobileComm comm, PacketComm packetComm,
            ServerResponseMonitor serverResponseMonitor) {
        this.comm = comm;
        this.packetComm = packetComm;
        this.serverResponseMonitor = serverResponseMonitor;

        Position position = new Position();
        golfCart = new GolfCart(0, null, 0);
        golfCart.setPosition(position);
        packet = new BytePacket();
        origin = new Position();
        lastPositionSent = new Position();
    }

    public void setMinPacketDelay(int minPacketDelay) {
        this.minPacketDelay = minPacketDelay;
    }

    public void setMaxPacketDelay(int maxPacketDelay) {
        this.maxPacketDelay = maxPacketDelay;
    }

    public void setDistanceThreshold(float distanceThreshold) {
        this.distanceThreshold = distanceThreshold;
    }

    public void setRoundManager(RoundManager roundManager) {
        this.roundManager = roundManager;
    }

    public void setGolfClub(Site golfClub) {
        if (golfClub != null) {
            Coordinates topLeft = golfClub.getMapImage().getTopLeft();
            origin.setLocation(topLeft.longitude, topLeft.latitude);
        }
        else {
            origin.setLocation(0, 0);
        }
        comm.setOrigin(origin);
    }

    public void setCartStatus(CartAppStatus cartStatus) {
        this.cartStatus = cartStatus;
    }

    public void setCartNumber(int id) {
        golfCart.setNumber(id);
    }

    public void setSiteId(int id) {
        golfCart.setSiteId(id);
    }

    /*
     * Scheduled send cart status task
     */

    @Override
    public void startTask() {
    }

    @Override
    public void runTask() {
        serverResponseMonitor.checkLastServerResponse();

        long lastMessageSeconds = getLastMessageSeconds();

        boolean send = sendImmediately
                || lastPositionSentTime == 0
                || lastMessageSeconds >= maxPacketDelay
//                || serverResponseMonitor.isRequestAckTime()
                || (lastMessageSeconds >= minPacketDelay && hasLocationChanged());

        if (send) {
            sendLocation();
        }

        createTaskStatusString();
    }

    /*
     * Send the location and status
     */
    private void sendLocation() {
        //Introduce check for server response to make sure we are still online

        synchronized (packetComm) {
            //Synchronization: Only clear sendImmediately flag when it was true
            //at the beginning, otherwise we may be missing some information.
            boolean sendNow = sendImmediately;
            boolean ready = packetComm.getReady();
            boolean encoded = ready && encodePositionAndStatus();
            boolean ackRequested = encoded &&
                    serverResponseMonitor.checkAndEncodeAckRequest(packet);

            if (encoded) {
                if (log.isDebugEnabled()) {
                    log.debug("Sending: " + packet);
                }

                if(packetComm.sendPacket(packet)) {
                    lastPositionSentTime = Time.milliTime();
                    lastPositionSent.setLocation(golfCart.getPosition());

                    if (ackRequested) {
                        serverResponseMonitor.ackRequestSent();
                    }

                    if (sendNow) {
                        sendImmediately = false;
                    }
                }
            }
        }
    }

    private boolean encodePositionAndStatus() {
        boolean encoded = GolfCartEncoder.encodeGolfCartPosition(packet, trackingMessageCounter, origin, golfCart);
        trackingMessageCounter++;

        if(encoded) {
            int appMode = 0;
            if (cartStatus.getMode() == CartAppMode.GOLF) {
                //Always update pace of play value before sending
                updatePaceOfPlay();
                encoded &= GolfCartEncoder.encodeGameStatus(packet, currentHoleIndex, paceOfPlay);
                encoded &= GolfCartEncoder.encodeCartAheadRequest(packet, viewHole);

                if (cartStatus.isAccessibleMode()) {
                    appMode = PacketType.STATUS_APPMODE_GOLF_HANDICAP;
                }
            }
            else if (cartStatus.getMode() == CartAppMode.RANGER) {
                appMode = PacketType.STATUS_APPMODE_RANGER;
            }
//            else {
//                encoded = false;
//            }
            int status = PacketType.STATUS_ACTIVE;

            if (cartStatus != null) {
                status |= cartStatus.isInRestrictedZone()? PacketType.STATUS_RESTRICTEDZONE : 0;
                status |= cartStatus.isWeatherAlertEnabled()? PacketType.STATUS_WEATHERALERT : 0;
                status |= cartStatus.isCartPathOnlyEnabled()? PacketType.STATUS_CARTPATHONLY : 0;
                status |= cartStatus.isInCartPathViolation()? PacketType.STATUS_OUTOFCARTPATH : 0;
                status |= cartStatus.isAccessibleMode()? PacketType.STATUS_ACCESSIBLE : 0;
            }

            encoded &= GolfCartEncoder.encodeCartStatus(packet, appMode, status);
        }

        return encoded;
    }

    private long getLastMessageSeconds() {
        return (Time.milliTime() - lastPositionSentTime) / 1000;
    }

    private boolean hasLocationChanged() {
        if (ObjectUtils.equals(lastPositionSent, golfCart.getPosition())) {
            return false;
        }
        else {
            EarthGeoCalculations geo = EarthGeoCalculations.getInstance();
            double distance = geo.getDistance(lastPositionSent, golfCart.getPosition());
            return distance > distanceThreshold;
        }
    }

    /**
     * Updates the cart position for tracking report.
     * Called from PositionManager(async)->CartAppComm
     * @param p the current cart position
     */
    public void positionUpdated(Coordinates p, float heading) {
        golfCart.getPosition().setLocation(p.longitude, p.latitude);
        golfCart.setHeading(heading);
    }

    /**
     * Updates the pace of play for tracking report.
     */
    public void updatePaceOfPlay() {
        paceOfPlay = roundManager == null? 0 : roundManager.getCurrentPace();
    }

    /*
     * RoundObserver
     */

    public void viewHoleChanged(GolfHole hole) {
        viewHole = hole == null ? 0 : hole.getHoleIndex();
        sendImmediately = true; //Send location which contains the cart ahead request
    }

    public void currentHoleChanged(GolfHole hole) {
        if (hole != null) {
            currentHoleIndex = hole.getHoleIndex();
            paceOfPlay = roundManager == null ? 0 : roundManager.getCurrentPace();
        }
        else {
            currentHoleIndex = 0;
            paceOfPlay = 0;
        }
    }

    public void roundStateChanged(RoundState round) {
        sendImmediately = true;
    }

    /*
     * Status string generation
     */

    private void createTaskStatusString() {
        Date d = new Date();
        taskStatus = "taskRun=" + FormatUtils.dateTimeFormat.format(d);

        long lastResponse = serverResponseMonitor.getLastServerResponse();
        d.setTime(Time.toLocalTime(lastResponse));
        taskStatus += "; lastResponse=" +
                (lastResponse == 0? "" : FormatUtils.dateTimeFormat.format(d));

        d.setTime(Time.toLocalTime(lastPositionSentTime));
        taskStatus += "; lastPosition=" +
                (lastPositionSentTime == 0? "" : FormatUtils.dateTimeFormat.format(d));
    }

    public String getTaskStatus() {
        return taskStatus;
    }

}
