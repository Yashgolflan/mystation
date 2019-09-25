/*
 *
 */
package com.stayprime.comm.gprs;

import com.stayprime.comm.BytePacket;
import com.stayprime.comm.PacketReceiver;
import com.stayprime.comm.PacketComm;
import com.stayprime.comm.io.IOStream;
import java.io.IOException;
import java.net.SocketAddress;
import java.util.concurrent.TimeUnit;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author benjamin
 */
public class GPRSPacketComm implements PacketComm {
    private static final Logger log = LoggerFactory.getLogger(GPRSPacketComm.class);

    private Sim900Modem gprsModem;
    private final Countdown resetCountdown;

    //Modem IO restart
    private long noReplyRestartDelay_ns = TimeUnit.SECONDS.toNanos(60);
    private long minIoRestartInterval_ns = TimeUnit.SECONDS.toNanos(600);

    private int ioRestartCount = 0;
    private long ioRestartTimeout_ns;

    public GPRSPacketComm(IOStream io) {
	gprsModem = new Sim900Modem(io);
        gprsModem.setSendEscaped(true);
        resetCountdown = new Countdown(10, 100);
    }

    public void config(PropertiesConfiguration config) {
        GPRSPacketSenderConfig.config(this, config);
        GPRSPacketSenderConfig.config(gprsModem, config);
    }

    @Override
    public void updateConfig(PropertiesConfiguration config) {
        GPRSPacketSenderConfig.updateConfig(gprsModem, config);
    }

    public void setModemResetCount(int modemResetCount) {
	resetCountdown.setInterval(modemResetCount);
    }

    public GsmAtModem getGPRSModem() {
	return gprsModem;
    }

    public int getResetCount() {
        return resetCountdown.getActionCounter();
    }

    public int getIoRestartCount() {
        return ioRestartCount;
    }

    @Override
    public void setPacketReceiver(PacketReceiver packetReceiver) {
	gprsModem.setPacketReceiver(packetReceiver);
    }

    @Override
    public void start() throws Exception {
        log.info("Starting gprsModem");
        resetCountdown.start();
        ioRestartTimeout_ns = System.nanoTime() + noReplyRestartDelay_ns;
        gprsModem.start();
    }

    @Override
    public void stop() {
        try { gprsModem.closeConnection(); }
        catch (IOException ex) {}

        gprsModem.stop();
    }

    @Override
    public synchronized boolean getReady() {
        boolean ready = false;

        try {
	    ready = gprsModem.checkConnection();

            if (ready) {
                resetCountdown.clearCountdown();
                ioRestartTimeout_ns = System.nanoTime() + noReplyRestartDelay_ns;
            }
            else {
                ioResetCountdown();
                modemResetCountdown();
            }
	}
	catch (Exception ex) {
	    log.trace(ex.toString(), ex);
            checkModemIORestart();
	}

        return ready;
    }

    public synchronized boolean sendPacket(byte[] b, int len) {
	try {
	    return gprsModem.sendData(b, len);
	}
	catch (Exception ex) {
	    log.error(ex.toString());
	    return false;
	}
    }

    @Override
    public boolean sendPacket(BytePacket p) {
	return sendPacket(p.getPacket(), p.getLength());
    }

    private void ioResetCountdown() throws IOException {
        if(gprsModem.isActive() == false || after(gprsModem.getLastReplyNanoTime() + noReplyRestartDelay_ns)) {
            checkModemIORestart();
        }
    }

    private void modemResetCountdown() {
        if (resetCountdown.countdown()) {
            gprsModem.reset();
        }
    }

    private boolean after(long nanoTime) {
        return System.nanoTime() - nanoTime > 0;
    }

    private void checkModemIORestart() {
        if(after(ioRestartTimeout_ns) == false) {
            long seconds = TimeUnit.NANOSECONDS.toSeconds(ioRestartTimeout_ns - System.nanoTime());
            log.debug("Restarting modem IO in " + seconds);
            return;
        }

        log.debug("Restarting modem IO...");

        try {
            //Ensure we wait for an interval between sucesive IO restarts
            ioRestartTimeout_ns = System.nanoTime() + minIoRestartInterval_ns;
            ioRestartCount++;
            gprsModem.stop();

            try { Thread.sleep(1000); }
            catch (InterruptedException ex) {}

            gprsModem.start();
        }
        catch (Exception ex) {
            log.debug(ex.toString());
            log.trace(ex.toString(), ex);
        }
    }

    @Override
    public String getStatusLine() {
        return gprsModem.getStatusLine();
    }

    @Override
    public String getAddress() {
        return gprsModem.getIPAddress();
    }

    @Override
    public int getPort() {
        return gprsModem.getUdpPort();
    }

    @Override
    public void reconnect() {
        try {
            gprsModem.closeConnection();
        }
        catch (IOException ex) {
            log.error(ex.toString());
        }
    }

    @Override
    public boolean sendPacket(BytePacket packet, SocketAddress address) {
        //Not supported
        return false;
    }

}
