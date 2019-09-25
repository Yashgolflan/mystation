/*
 *
 */
package com.stayprime.comm.test;

import com.stayprime.comm.BytePacket;
import com.stayprime.comm.PacketReceiver;
import com.stayprime.comm.encoder.GolfCartEncoder;
import com.stayprime.comm.socket.SocketPacketSender;
import com.stayprime.model.golf.GolfCart;
import com.stayprime.model.golf.Position;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author benjamin
 */
public class GolfCartCommServerTest {
    private static final Logger log = LoggerFactory.getLogger(GolfCartCommServerTest.class);

    public static void main(String args[]) {
	try {
	    int port = args.length >= 1? NumberUtils.toInt(args[0], 12345) : 12345;
	    log.info("Port: " + port);

	    SocketPacketSender udpComm = new SocketPacketSender(port);
	    PacketReceiverImpl packetReceiver = new PacketReceiverImpl();
//	    packetReceiver.setSendEscaped(false);
	    
	    if(args.length >= 3)
		packetReceiver.setOrigin(new Position(
			NumberUtils.toDouble(args[1], 0),
			NumberUtils.toDouble(args[2], 0)));
	    else
		packetReceiver.setOrigin(new Position());

	    udpComm.setPacketReceiver(packetReceiver);
	    udpComm.start();
	}
	catch (Exception ex) {
	    log.error(ex.toString(), ex);
	}
    }

    private static class PacketReceiverImpl implements PacketReceiver {
//	private boolean charEscapeEnabled;
        private final Position origin;

	public PacketReceiverImpl() {
            origin = new Position();
	}

	private void setOrigin(Position origin) {
	    log.info("Origin: " + origin);
            this.origin.setLocation(origin);
	}

//	@Override
//	public void setSendEscaped(boolean enabled) {
//	    this.charEscapeEnabled = enabled;
//	}
//
//	@Override
//	public boolean isCharEscapeEnabled() {
//	    return charEscapeEnabled;
//	}

	@Override
	public void packetReceived(BytePacket packet) {
	    log.info("Packet received: " + ArrayUtils.toString(packet.getPacket()));
            GolfCart cart = GolfCartEncoder.decodeGolfCartPosition(packet, origin, null);

	    if (cart != null) {
		log.info("Position decoded: " + cart.getPosition());
            }
	}

    }
}
