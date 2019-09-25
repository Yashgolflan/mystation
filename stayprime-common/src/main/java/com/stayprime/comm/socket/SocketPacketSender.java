/*
 *
 */
package com.stayprime.comm.socket;

import com.stayprime.util.NetUtil;
import com.stayprime.comm.BytePacket;
import com.stayprime.comm.BytePacketUtils;
import com.stayprime.comm.PacketComm;
import com.stayprime.comm.PacketReceiver;
import com.stayprime.comm.gprs.GPRSPacketSenderConfig;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Enumeration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author benjamin
 */
public class SocketPacketSender implements PacketComm {
    private static final Logger log = LoggerFactory.getLogger(SocketPacketSender.class);
    private DatagramSocket socket;

    private final DatagramPacket sendPacket;
    private SocketAddress sendAddr;

    private DatagramPacketReceiver datagramPacketReceiver;
    private PacketReceiver packetReceiver;
    private PacketServer packetServer;
    private int receivePort = -1;
    private NetworkInterface networkInterface;

    private boolean sendEscaped = false;
    private boolean receiveEscaped = false;
    private BytePacket sendEscPacket = null;
    private transient boolean started;

    /**
     * Create SocketPacketSender on default interface and no receivePort.
     */
    public SocketPacketSender() {
	this(0);
    }

    /**
     * Create SocketPacketSender on default interface listening on receivePort.
     * @param receivePort the port to listen for incoming packets.
     */
    public SocketPacketSender(int receivePort) {
        log.debug("Creating SocketPacketSender with receivePort " + receivePort);

        this.receivePort = receivePort;
	sendPacket = new DatagramPacket(new byte[0], 0);
    }

    /**
     * Create SocketPacketSender on specific interface listening on receivePort.
     * If the interface doesn't exist, throws a SocketException.
     * @param receivePort the port to listen for incoming packets.
     * @param interfaceName the interface to bind for receiving packets.
     * @throws SocketException
     */
    public SocketPacketSender(int receivePort, String interfaceName) throws SocketException {
        log.debug("Creating SocketPacketSender with interface " + interfaceName + ", receivePort " + receivePort);

        if(log.isTraceEnabled()) {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while(interfaces.hasMoreElements()) {
                NetworkInterface ia = interfaces.nextElement();
                log.trace("NetworkInterface: " + ia.getName() + " " + Arrays.toString(ia.getHardwareAddress()));
            }
        }

        this.receivePort = receivePort;
	sendPacket = new DatagramPacket(new byte[0], 0);
        networkInterface = NetworkInterface.getByName(interfaceName);

        if(networkInterface == null) {
            throw new SocketException("Invalid network interface: " + interfaceName);
        }

        log.debug("Created " + networkInterface.getName());
    }

    @Override
    public void updateConfig(PropertiesConfiguration config) {
        String serverSettings = config.getString(GPRSPacketSenderConfig.SERVER_SETTINGS);
        String[] srv = StringUtils.split(serverSettings, ',');

        if (srv != null && srv.length == 2) {
            setSendAddress(new InetSocketAddress(srv[0], Integer.parseInt(srv[1])));
        }
    }

    public void setSendAddress(SocketAddress address) {
        log.debug("Entering setSendAddress: " + address);
	this.sendAddr = address;
    }

    public void setPacketReceiver(DatagramPacketReceiver packetReceiver) {
	this.datagramPacketReceiver = packetReceiver;
    }

    @Override
    public void setPacketReceiver(PacketReceiver packetReceiver) {
	this.packetReceiver = packetReceiver;
    }

    public void setSendEscaped(boolean enabled) {
        sendEscaped = enabled;
        if (sendEscaped && sendEscPacket == null) {
            sendEscPacket = new BytePacket();
        }
    }

    public void setReceiveEscaped(boolean enabled) {
        receiveEscaped = enabled;
    }

    public boolean isStarted() {
        return started;
    }

    @Override
    public boolean getReady() {
	return socket != null;
    }

    @Override
    public synchronized void start() throws Exception {
        log.debug("Entering start()");

        if(started) {
            throw new IllegalArgumentException("UDPPacketSender already started");
        }
        started = true;

	if(receivePort > 0) {
            log.debug("Creating listening DatagramSocket");

            if (networkInterface != null) {
                socket = new DatagramSocket(receivePort, getInet4Address());
            }
            else {
                socket = new DatagramSocket(receivePort);
            }
        }
        else {
	    socket = new DatagramSocket();
        }

	if(packetReceiver != null || datagramPacketReceiver != null) {
            log.debug("Creating PacketServer to receive UDP packets");
            packetServer = new PacketServer(socket);
            packetServer.setReceiveEscaped(receiveEscaped);
            packetServer.setDatagramPacketReceiver(datagramPacketReceiver);
            packetServer.setPacketReceiver(packetReceiver);
	    packetServer.start();
	}
    }

    @Override
    public synchronized void stop() {
        log.debug("Entering stop()");

        //packetServer.stopServer();
        IOUtils.closeQuietly(socket);
        socket = null;
        synchronized (this) {
            started = false;
        }
    }

    @Override
    public boolean sendPacket(BytePacket packet) {
        return sendAddr != null && sendPacket(packet.getPacket(), packet.getLength(), sendAddr);
    }

    @Override
    public boolean sendPacket(BytePacket packet, SocketAddress address) {
        return address != null && sendPacket(packet.getPacket(), packet.getLength(), address);
    }

    public boolean sendPacket(byte[] data, int len, SocketAddress address) {
        log.debug("Entering sendPacket() " + address);
	try {
            synchronized (sendPacket) {
                sendPacket.setSocketAddress(address);

                if (sendEscaped) {
                    int length = BytePacketUtils.escapePacket(data, sendEscPacket.getPacket(), len);
                    sendEscPacket.setLength(length);
                    sendPacket.setData(sendEscPacket.getPacket(), 0, length);
                }
                else {
                    sendPacket.setData(data, 0, len);
                }

                socket.send(sendPacket);
            }
            return true;
	}
	catch (IOException ex) {
	    log.error(ex.toString());
	    log.debug(ex.toString(), ex);
            stop();
        }
	catch (Exception ex) {
	    log.error(ex.toString());
	    log.debug(ex.toString(), ex);
	}
        return false;
    }

    public InetAddress getInet4Address() {
        if(networkInterface != null) {
            if(log.isTraceEnabled()) {
                Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
                while(inetAddresses.hasMoreElements()) {
                    InetAddress ia = inetAddresses.nextElement();
                    log.trace("InetAddress: " + ia.getClass().getSimpleName() + ia);
                }
            }
            return NetUtil.getInet4Address(networkInterface);
        }

        return null;
    }

    @Override
    public String getAddress() {
        InetAddress inet4Address = getInet4Address();
        return inet4Address == null? null : inet4Address.getHostAddress();
    }

    @Override
    public int getPort() {
        return receivePort;
    }

    @Override
    public synchronized void reconnect() throws Exception {
        if (started) {
            stop();
            start();
        }
        else {
            throw new IllegalStateException("Can't reconnect: not started");
        }
    }

    private static class PacketServer extends Thread {
        private DatagramSocket socket;
	private final DatagramPacket receivePacket;
	private final BytePacket bytePacket;
        private final int bufferSize = 2048;

        private boolean receiveEscaped = false;
        private DatagramPacketReceiver datagramPacketReceiver;
        private PacketReceiver packetReceiver;

	public PacketServer(DatagramSocket socket) {
            this.socket = socket;
	    bytePacket = new BytePacket(new byte[bufferSize]);
	    receivePacket = new DatagramPacket(bytePacket.getPacket(), bufferSize);
	}

        public void setReceiveEscaped(boolean receiveEscaped) {
            this.receiveEscaped = receiveEscaped;
        }

        public void setDatagramPacketReceiver(DatagramPacketReceiver datagramPacketReceiver) {
            this.datagramPacketReceiver = datagramPacketReceiver;
        }

        public void setPacketReceiver(PacketReceiver packetReceiver) {
            this.packetReceiver = packetReceiver;
        }

        public void stopServer() {
            socket = null;
        }

	@Override
	public void run() {
            while (true && socket != null) {
                try {
                    receivePacket.setLength(bufferSize);
		    socket.receive(receivePacket);

                    if (receiveEscaped) {
                        byte[] data = receivePacket.getData();
                        int offset = receivePacket.getOffset();
                        int length = receivePacket.getLength();
                        length = BytePacketUtils.unescapePacket(data, offset, length);
                        receivePacket.setLength(length);
                    }

                    bytePacket.setLength(receivePacket.getLength());

		    log.debug("{}", receivePacket.getSocketAddress());

		    if(datagramPacketReceiver != null)
			datagramPacketReceiver.packetReceived(receivePacket);

		    if(packetReceiver != null)
			packetReceiver.packetReceived(bytePacket);
                }
                catch (IOException ex) {
                    log.error(ex.toString());
                    socket = null;
                }
                catch (Exception ex) {
                    log.error(ex.toString());
                }
            }
	}
    }

    public String getStatusLine() {
        return "Socket " + (socket == null? "NULL" : getInet4Address());
    }

}
