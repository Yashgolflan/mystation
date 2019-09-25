/*
 *
 */
package com.stayprime.comm.test;

import com.stayprime.comm.gprs.GsmAtModem;
import com.stayprime.comm.BytePacket;
import com.stayprime.comm.PacketReceiver;
import com.stayprime.comm.io.CommandIOStream;
import com.stayprime.comm.io.IOStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author benjamin
 */
public class GPRSModemTest {
    private static final Logger log = LoggerFactory.getLogger(GsmAtModem.class);

    public static void main(String args[]) {
	try {
	    IOStream ios = new CommandIOStream("microcom -X -s 115200 /dev/ttymxc0");
	    GsmAtModem modem = new GsmAtModem(ios);
	    log.info("Starting modem");
	    modem.setPacketReceiver(new PacketReceiverImpl());
	    modem.setApnConfig("wlgdsp.com","stay","stay");
	    modem.setServerConfig("64.150.164.90","12346");
	    modem.start();
            modem.checkConnection();

	    String line;
	    BufferedReader r = new BufferedReader(new InputStreamReader(System.in));

	    while(StringUtils.isNotBlank(line = r.readLine())) {
		log.info("Sending data: " + line);
		modem.sendData(line.getBytes(), line.length());
	    }

	    log.info("Stopping modem");
	    modem.stop();
	    log.info("Modem stopped");
	}
	catch (Exception ex) {
	    log.debug(ex.toString());
	}
    }

    private static class PacketReceiverImpl implements PacketReceiver {
	private boolean charEscapeEnabled;

	public PacketReceiverImpl() {
	}

	@Override
	public void packetReceived(BytePacket packet) {
	    log.info(new String(packet.getPacket(), 0, packet.getLength()));
	}
    }
}
