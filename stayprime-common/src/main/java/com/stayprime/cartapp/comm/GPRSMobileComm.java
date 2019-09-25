/*
 *
 */
package com.stayprime.cartapp.comm;

import com.stayprime.comm.BytePacket;
import com.stayprime.comm.PacketComm;
import com.stayprime.comm.PacketReceiver;
import com.stayprime.comm.encoder.GolfCartListEncoder;
import com.stayprime.comm.encoder.MessageEncoder;
import com.stayprime.comm.encoder.Packet;
import com.stayprime.comm.encoder.PacketType;
import com.stayprime.comm.encoder.PinLocationListEncoder;
import com.stayprime.comm.encoder.TournamentEncoder;
import com.stayprime.comm.gprs.request.MessageRequest;
import com.stayprime.comm.gprs.request.Request;
import com.stayprime.comm.gprs.request.RequestHandler;
import com.stayprime.comm.gprs.request.RequestObserver;
import com.stayprime.golf.course.CoursePinLocationsInfoDTO;
import com.stayprime.golf.message.FnbOrderItem;
import com.stayprime.golf.message.FnbOrderPayload;
import com.stayprime.golf.message.GolfCartListMessage;
import com.stayprime.golf.message.Message;
import com.stayprime.model.golf.Position;
import com.stayprime.model.golf.ScorecardModel;
import com.stayprime.tournament.comm.CartRoundInfo;
import com.stayprime.tournament.comm.PlayerScoresList;
import com.stayprime.tournament.comm.TournamentInfo;
import com.stayprime.tournament.model.PlayerScores;

import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author benjamin
 */
public class GPRSMobileComm implements GolfCartMobileComm {
    static final Logger log = LoggerFactory.getLogger(GPRSMobileComm.class);
    public static final int MAX_RETRIES = 3;
    public static final int ACK_MAXDELAY = 5;

    private final PacketComm packetComm;
    private final ServerResponseMonitor serverResponseMonitor;

    private final RequestHandler requestHandler;
    private final PacketDecoder packetDecoder;

    private byte messageCounter = 0;
    private int siteId;
    private int cartNumber;

    private final Position origin;

    public GPRSMobileComm(PacketComm comm, ServerResponseMonitor serverResponseMonitor, MobileCommReceiver receiver) {
        this.packetComm = comm;
        this.serverResponseMonitor = serverResponseMonitor;

        packetDecoder = new PacketDecoder(receiver);
        packetComm.setPacketReceiver(packetDecoder);
        origin = new Position();
        requestHandler = new RequestHandler(packetComm);
    }

    /*
     * Configuration and properties setting
     */

    public void setCartNumber(int id) {
        this.cartNumber = id;
    }

    public void setSiteId(int id) {
        this.siteId = id;
    }

    public void start() {
        requestHandler.start();
    }

    /*
     * Public methods.
     */

    @Override
    public boolean sendAssistanceRequest(int type, RequestObserver ro) {
        Packet<Message> packet = new Packet<Message>(siteId, cartNumber, messageCounter++);
        packet.setPayload(new Message(PacketType.PRE_MSG, type));

        Request request = new MessageRequest(packet);
        request.setObserver(ro);

        requestHandler.addRequest(request);
        return true;
    }

    private boolean sendACK(int messageType, int ackMessageCounter, int messageCode) {
        BytePacket msgPacket = new BytePacket();
        MessageEncoder.encodeAck(msgPacket, siteId, cartNumber, ackMessageCounter, messageType, null, messageCode);

        if (log.isDebugEnabled()) {
            log.debug("Sending: " + msgPacket);
        }

        return packetComm.getReady() && packetComm.sendPacket(msgPacket);
    }
    
    private boolean sendACK(int messageType, int ackMessageCounter, int messageCode, String data) {
        BytePacket msgPacket = new BytePacket();
        MessageEncoder.encodeAck(msgPacket, siteId, cartNumber, ackMessageCounter, messageType, data, messageCode);

        if (log.isDebugEnabled()) {
            log.debug("Sending: " + msgPacket);
        }

        return packetComm.getReady() && packetComm.sendPacket(msgPacket);
    }

    public boolean sendFBOrder(List<FnbOrderItem> orderItems, int currentHole, int hutNumber, RequestObserver ro) {
        log.debug("entering sendFBOrder()");
        Packet<FnbOrderPayload> packet = new Packet<FnbOrderPayload>(siteId, cartNumber, messageCounter++);
        packet.setPayload(new FnbOrderPayload(currentHole, hutNumber, orderItems));

        Request<FnbOrderPayload> request = new Request<FnbOrderPayload>(packet);
        request.setObserver(ro);
        requestHandler.addRequest(request);
        return true;
    }

     /**
     * Encode and send scoreCard.
     * @param players
     * @param emailAddress
     * @param ro
     * @return
     */
    public boolean sendScorecardRequest(List<PlayerScores> players, String emailAddress, RequestObserver ro) {
        Packet<ScorecardModel> p = new Packet<ScorecardModel>(siteId, cartNumber, messageCounter++);
        p.setPayload(new ScorecardModel(players, emailAddress));

        Request<ScorecardModel> request = new Request<ScorecardModel>(p);
        request.setObserver(ro);
        requestHandler.addRequest(request);
        return true;
    }

    public boolean sendTournamentScores(String tmtExtId, int round, List<PlayerScores> players, RequestObserver ro) {
        log.info("Sending live scores for players: " + StringUtils.join(players, ", "));

        Packet<PlayerScoresList> packet = new Packet<PlayerScoresList>(siteId, cartNumber, messageCounter++);
        packet.setPayload(new PlayerScoresList(tmtExtId, round, players));

        Request<PlayerScoresList> request = new Request<PlayerScoresList>(packet);
        request.setObserver(ro);
        requestHandler.addRequest(request);
        return true;
    }

    public boolean sendTournamentInfoRequest(RequestObserver ro) {
        Packet<TournamentInfo> packet = new Packet<TournamentInfo>(siteId, cartNumber, messageCounter++);
        packet.setPayload(new TournamentInfo());

        Request request = new Request(packet);
        request.setObserver(ro);

        requestHandler.addRequest(request);
        return true;
    }

    public void sendLeaderboardRequest(int siteId, String extId, RequestObserver ro) {
        Packet<Message> packet = new Packet<Message>(siteId, cartNumber, messageCounter++);
        Message msg = new Message(PacketType.PRE_MSG, PacketType.TMT_REQUEST_MSG, null);
        msg.setDetails(PacketType.TMT_REQUEST_LEADERBOARD);
        packet.setPayload(msg);

        Request request = new Request(packet);
        request.setObserver(ro);

        requestHandler.addRequest(request);
    }

    /*
     * Status
     */

    public void setOrigin(Position origin) {
        this.origin.setLocation(origin);
    }

    public void cancelRequests(int type) {
        requestHandler.cancelMatchingRequests(type);
    }

    /*
     * Internal util methods
     */

    /*
     * Implement PacketReceiver
     */

    private class PacketDecoder implements PacketReceiver {
        private final MobileCommReceiver receiver;

        public PacketDecoder(MobileCommReceiver receiver) {
            this.receiver = receiver;
        }

        @Override
        public void packetReceived(BytePacket receivedPacket) {
            log.debug("Packet received");
            if (serverResponseMonitor != null) {
                serverResponseMonitor.packetReceived();
            }

            byte[] pack = receivedPacket.getPacket();
            int type = pack[1];

            if (PacketType.CART_AHEAD_LIST.test(type)) {
                GolfCartListMessage message = GolfCartListEncoder.decodeCartList(receivedPacket, origin);
                if (message != null) {
                    receiver.cartsAheadReceived(message.getGolfCartList());
                }
            }
            else if (PacketType.CART_LIST.test(type)) {
                GolfCartListMessage message = GolfCartListEncoder.decodeCartList(receivedPacket, origin);
                if (message != null) {
                    log.debug("Packet received  GolfCartListEncoder: decoding packet :" + message.getGolfCartList().toString());
                    receiver.cartsAheadReceived(message.getGolfCartList());
                }
            }
            else if (PacketType.TMT_ROUND_PLAYERS.test(type)) {
                Packet<CartRoundInfo> roundInfo = TournamentEncoder.decodeRoundAndPlayers(receivedPacket);
                receiver.roundInfoReceived(roundInfo);
                sendACK(PacketType.TMT_ROUND_PLAYERS.id, roundInfo.getMessageCounter(), 0);
            }
            else if (PacketType.TEXT_MESSAGE.test(type)) {
                Packet<Message> p = MessageEncoder.decodeTextMessage(receivedPacket);
                handleTextMessageReceived(p);
            }
            else if (PacketType.PRE_MSG.test(type)) {
                Packet<Message> p = MessageEncoder.decodePresetMessage(receivedPacket);
                handlePreMessageReceived(p);
            }
            else if (PacketType.ACK.test(type)) {
                Packet<Message> ack = MessageEncoder.decodeAck(receivedPacket);
                Message message = ack.getPayload();

                //REQUEST_ACK just means the server responded
                if (message.getMessageCode() != PacketType.REQUEST_ACK) {
                    requestHandler.ackReceived(ack);
//                        receiver.ackReceived(message.getText());
//                        this.ackHandler(message.getMessageCode());
                }
            }
            else if (PacketType.COMMAND.test(type)) {
                Message message = MessageEncoder.decodeMessage(receivedPacket, PacketType.COMMAND.id).getPayload();
                receiver.preMessageReceived(message.getMessageCode(), 0);
                //not setting the acknowledge at this stage, may be later
                //this.ackHandler(decoder.getMessageCode());
            }
            else if (PacketType.PIN_LOCATIONS.test(type)) {
                // create the coursePinLocationsInfo object
               
                Packet<CoursePinLocationsInfoDTO> packet = PinLocationListEncoder.decodePinLocationsPacket(receivedPacket, origin);
                // add code in mobileComm reviever to handle the packet
                receiver.pinLocationListRecieved(packet.getPayload());
                sendACK(PacketType.PIN_LOCATIONS.id, packet.getMessageCounter(), 0, packet.getPayload().getLastUpdateTime());
            }
        }

        private void handlePreMessageReceived(Packet<Message> packet) {
            Message message = packet.getPayload();
            receiver.preMessageReceived(message.getMessageCode(), 0);
            sendACK(PacketType.PRE_MSG.id, packet.getMessageCounter(), message.getMessageCode());
        }

        private void handleTextMessageReceived(Packet<Message> packet) {
            Message message = packet.getPayload();
            receiver.textMessageReceived(message.getText(), 0);
            sendACK(PacketType.TEXT_MESSAGE.id, packet.getMessageCounter(), 0);
        }
    }

}
