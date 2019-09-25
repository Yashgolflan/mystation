/*
 *
 */
package com.stayprime.comm.gprs;

import com.stayprime.comm.BytePacket;
import com.stayprime.comm.PacketReceiver;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.lang.CharUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author benjamin
 */
/**
 * Output reader and parser thread
 */
class AtOutputReader extends Thread {
    private static final Logger log = LoggerFactory.getLogger(AtOutputReader.class);

    private volatile boolean running = false;

    private BufferedInputStream in;
    private byte[] lineBuffer;
    private static final int maxLineSize = 1024;

    private BytePacket packet;
    private static final int maxPacketLength = 512;
    private PacketReceiver packetReceiver;

    private final Object stateMonitor;
    private final Object okMonitor;
    private final Object confirmMonitor;

    private volatile GPRState state;
    private volatile GPRState nextState;
    private volatile String confirmation;
    private int okCount;

    private String modemIMEI;
    private boolean simInserted;
    private String simIMSI;
    private String simIccid;
    private boolean pinReady;
    private String pinStatus;
    private int udpPort;
    private volatile String networkSpec;
    private volatile String operator, networkId;
    private int signalQuality;
    private String ipAddress;

    private StringBuilder ipPackLen = new StringBuilder(6);
    private int ipHeadLen = Sim900Const.IP_HEAD.length();
    private String echoLine;
    private long lastReplyNanoTime;
    private boolean initialized = false;

    public AtOutputReader(InputStream inputStream) {
	state = GPRState.UNKNOWN;
	stateMonitor = new Object();
	okMonitor = new Object();
	confirmMonitor = new Object();
	in = new BufferedInputStream(inputStream, 2 * maxLineSize);
	lineBuffer = new byte[maxLineSize];
	packet = new BytePacket(new byte[maxPacketLength]);

        lastReplyNanoTime = System.nanoTime();
    }

    public void setPacketReceiver(PacketReceiver receiver) {
	this.packetReceiver = receiver;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    public long getLastReplyNanoTime() {
        return lastReplyNanoTime;
    }

    public void setEchoLine(String echoLine) {
	this.echoLine = echoLine;
    }

    private void setModemIMEI(String modemIMEI) {
	this.modemIMEI = modemIMEI;
    }

    public String getModemIMEI() {
	return modemIMEI;
    }

    private void setSimInserted(boolean simInserted) {
	this.simInserted = simInserted;
    }

    public boolean isSimInserted() {
	return simInserted;
    }

    private void setSimIMSI(String simIMSI) {
	this.simIMSI = simIMSI;
    }

    public String getSimIMSI() {
	return simIMSI;
    }

    private void setSimIccid(String simIccid) {
	if(simIccid != null)
	    setSimInserted(true);

	this.simIccid = simIccid;
    }

    public String getSimIccid() {
	return simIccid;
    }

    private void setUdpPort(int udpPort) {
        this.udpPort = udpPort;
    }

    public int getUdpPort() {
        return udpPort;
    }

    private void setPinStatus(String pinStatus) {
	setPinReady(AtConst.CPIN_READY.equals(pinStatus));
	setSimInserted(!AtConst.CPIN_NOT_INSERTED.equals(pinStatus));

	this.pinStatus = pinStatus;
    }

    public String getPinStatus() {
	return pinStatus;
    }

    private void setPinReady(boolean pinReady) {
	this.pinReady = pinReady;
    }

    public boolean isPinReady() {
	return pinReady;
    }

    private void setNetworkSpec(String networkSpec) {
        log.debug("Network spec: " + networkSpec);
        this.networkSpec = networkSpec;
    }

    public String getNetworkSpec() {
        return networkSpec;
    }

    private void setOperator(String operator) {
	this.operator = operator;
    }

    public String getOperator() {
	return operator;
    }

    private void setNetworkId(String networkId) {
        this.networkId = networkId;
    }

    public String getNetworkId() {
        return networkId;
    }

    private void setSignalQuality(int signalQuality) {
	this.signalQuality = signalQuality;
    }

    public int getSignalQuality() {
	return signalQuality;
    }

    private void setIPAddress(String ip) {
	ipAddress = ip;
    }

    public String getIPAddress() {
	return ipAddress;
    }

    private void setState(GPRState newState) {
        log.debug("{}", newState);
	if (state != newState) {
            state = newState;
	}
        if (state.id < GPRState.GPRS_READY.id) {
            setInitialized(false);
        }
    }

    public void resetState() {
	setState(GPRState.UNKNOWN);
    }

    public GPRState getGPRSState() {
	return state;
    }

    public void setNextState(GPRState state, String confirmation) {
	this.nextState = state;
	this.confirmation = confirmation;
    }

    public boolean waitForState(GPRState newState, long maxWait) {
	return waitForState(newState, maxWait, true);
    }

    public boolean waitForState(GPRState newState, long maxWait, boolean equalsOrGreater) {
	log.debug("waitForState: " + newState);

	synchronized (stateMonitor) {
            log.debug("sync waitForState");
	    if (equalsOrGreater ? state.id < newState.id : state.id != newState.id) {
		try {
		    stateMonitor.wait(maxWait);
		}
		catch (InterruptedException ex) {
		}
	    }
	    if (state.id >= newState.id) {
		log.trace("state ok: " + state);
		return true;
	    }
	    log.debug("no state change");
	    return false;
	}
    }

    public boolean waitForConfirmation(long maxWait) {
	log.debug("waitForConfirmation: " + confirmation);
	synchronized (confirmMonitor) {
            log.debug("sync waitForConfirmation");
	    if (confirmation != null) {
		try {
		    confirmMonitor.wait(maxWait);
		}
		catch (InterruptedException ex) {
		}
	    }
	    if (confirmation == null) {
		return true;
	    }
	    log.debug("no confirmation");
	    return false;
	}
    }

    public boolean waitForOk(long maxWait) {
	log.debug("waitForOk...");
	synchronized (okMonitor) {
            log.debug("sync waitForOk");
	    if (okCount == 0) {
		try {
		    okMonitor.wait(maxWait);
		}
		catch (InterruptedException ex) {
		}
	    }
	    if (okCount > 0) {
		okCount = 0;
		return true;
	    }
	    log.debug("no ok");
	    return false;
	}
    }

    private void notifyNextState() {
	log.trace("notifyNextState");
	nextState = null;
	confirmation = null;
	synchronized (stateMonitor) {
	    stateMonitor.notifyAll();
	}
    }

    private void notifyConfirmation() {
	log.trace("notifyConfirmation");
	confirmation = null;
	synchronized(confirmMonitor) {
	    confirmMonitor.notifyAll();
	}
    }

    private synchronized void setRunning(boolean running) {
        this.running = running;
    }

    public boolean isRunning() {
        return running;
    }

    @Override
    public void run() {
        log.info("Starting output reader thread");
        setRunning(true);

        try {
            readGPRSOutput();
        }
        catch (IOException ex) {
            log.error(ex.toString());
            log.trace(ex.toString(), ex);
        }

        setRunning(false);
    }

    private void readGPRSOutput() throws IOException {
	String line;
        log.debug("Reading GPRS output");
	while(running && (line = readPacketOrLine()) != null) {
            log.trace(line);
	    if (StringUtils.isBlank(line))
		continue;

            lastReplyNanoTime = System.nanoTime();

	    if (line.equals(echoLine)) {
		echoLine = null;
		continue;
	    }

	    if (testConfirmation(line)) continue;
	    if (testReceive(line)) continue;
	    if (testOk(line)) continue;
	    if (testIP(line)) continue;
	    if (testCIPSTATUS(line)) continue;
	    if (testCGATT(line)) continue;
	    if (testCSQ(line)) continue;
	    if (testCOPS(line)) continue;
	    if (testCFUN(line)) continue;
	    if (testNWICCID(line)) continue;
	    if (testCLPORT(line)) continue;
	    if (testURC(line)) continue;
	}

	log.debug("End of stream reached");
    }

    private String readPacketOrLine() throws IOException {
	boolean ipHead = true;
	ipPackLen.setLength(0);
	in.mark(maxLineSize);

	for (int i = 0; i < maxLineSize; i++) {
	    int b = in.read();

	    if (b == -1) { //End of stream reached
		if (i == 0)
		    return null; //Nothing to read, return null to exit
		else
		    return readLine(i); //Return the last bytes of the stream
	    }
	    else if (ipHead) {
		if (i < ipHeadLen) {
		    if (b != Sim900Const.IP_HEAD.charAt(i)) {
			ipHead = false;
		    }
		}
		else {
		    if (i > ipHeadLen && b == ':') {
			//IP Header complete, need to retrieve it
			int ipPacketLen = NumberUtils.toInt(ipPackLen.toString());
			readPacket(ipPacketLen);
			return StringUtils.EMPTY;
		    }
		    else if (b >= '0' && b <= '9') {
			ipPackLen.append((char) b);
		    }
		    else {
			ipHead = false;
		    }
		}
	    }

	    if (ipHead == false) {
		if (b == '\r' || b == '\n') {
		    return readLine(i);
		}
	    }
	}

	return StringUtils.EMPTY;
    }

    private String readLine(int length) throws IOException {
	if (length == 0)
	    return StringUtils.EMPTY;

	in.reset();
	int remaining = length;
	int total = 0;
	while (remaining > 0) {
	    total += in.read(lineBuffer, total, remaining);
	    remaining = length - total;
	}

	return new String(lineBuffer, 0, total);
    }

    private void readPacket(int ipPacketLen) throws IOException {
	log.debug("readPacket, length: " + ipPacketLen);
	if (ipPacketLen > maxPacketLength) {
	    long skip = ipPacketLen;
	    while (skip > 0) {
		skip -= in.skip(skip);
	    }
	}
	else {
	    int remaining = ipPacketLen;
	    int total = 0;
	    while (remaining > 0) {
		total += in.read(packet.getPacket(), total, remaining);
		remaining = ipPacketLen - total;
	    }
	    packet.setLength(total);
	    packetReceived(packet);
	}
    }

    /**
     * Passes the received packet to a PacketReceiver handler.
     * The handler could perform long running operations or call back the modem,
     * which in turn would wait for the AtOutputReader to read modem output.
     *
     * We cannot call the packet receiver in this same thread, because the
     * reading of modem output would be locked until the packetReceiver finishes,
     * which could lead to timeouts for any threads trying to use the modem,
     * included the packetReceiver itself, causing a circular lock.
     *
     * An improvement is to create a list of packets for handling them on
     * a separate handler thread.
     *
     * @param packet
     */
    private void packetReceived(final BytePacket packet) {
	if (packetReceiver != null) {
            new Thread() { //Fix locking the modem while the packet is handled
                @Override public void run() {
                    packetReceiver.packetReceived(packet);
                }
            }.start();
	}
        else {
	    log.warn("packetReceiver == null");
        }
    }

    private boolean testReceive(String line) {
	if (line.startsWith(Sim900Const.IP_HEAD)) {
	    String pack = line.substring(Sim900Const.IP_HEAD.length());
	    int colon = pack.indexOf(':');
	    if (colon > 0) {
		int dataLen = NumberUtils.toInt(pack.substring(0, colon), 0);
	    }
	    return true;
	}
	return false;
    }

    private boolean testCFUN(String line) {
	int cfun = getDigit(line, AtConst.CFUN_RE);
	if (cfun == -2) {
	    return false;
	}
	if (cfun == -1) {
	    log.debug("unknown CFUN report");
	    return true;
	}
	if (cfun == AtConst.CFUN_0) {
	    setState(GPRState.CFUN0);
	}
	else if (cfun == AtConst.CFUN_DISABLERF) {
	    setState(GPRState.RFDISABLED);
	}
	else if (cfun == AtConst.CFUN_1 && state.id < GPRState.FUNCTIONAL.id) {
	    setState(GPRState.FUNCTIONAL);
	    //else should throw IllegalStateException?
	}

        if (nextState == GPRState.FUNCTIONAL) {
	    notifyNextState();
	}
	return true;
    }

    private boolean testCGATT(String line) {
	int cgatt = getDigit(line, AtConst.CGATT_RE);
	if (cgatt == -2) {
	    return false;
	}
	if (cgatt == -1 || cgatt >= 2) {
	    log.debug("unknown CGATT report");
	    return true;
	}
	if (cgatt == 0 && state.id > GPRState.GPRS_NOTREADY.id) {
	    setState(GPRState.GPRS_NOTREADY);
	}
	else if (cgatt == 1 && state.id < GPRState.GPRS_READY.id) {
	    setState(GPRState.GPRS_READY);
	    //else should throw IllegalStateException?
	}
	if (nextState == GPRState.GPRS_READY) {
	    notifyNextState();
	}
	return true;
    }

    private boolean testNWICCID(String line) {
	String iccid = getString(line, AtConst.NWICCID_RE);
        if(iccid == null)
            return false;

        setSimIccid(iccid);
	return true;
    }

    private boolean testCLPORT(String line) {
	String port = getString(line, Sim900Const.CLPORT_RE_UDP);
        if(port == null)
            return false;

        setUdpPort(NumberUtils.toInt(port));
	return true;
    }

    private boolean testCSQ(String line) {
	String csq = getString(line, AtConst.CSQ_RE);
        if(csq == null)
            return false;

        setSignalQuality(NumberUtils.toInt(csq.split(",")[0], 99));
	return true;
    }

    private boolean testIP(String line) {
	// When waiting for an IP, set nextStateConfirmation to null and
	// let parseIpAddress determine if an IP address was received.
	if (parseIpAddress(line)) {
	    if (nextState == GPRState.IP_STATUS) {
		// Only set state if the current status is lower than IP_STATUS
		if (state.id <= GPRState.IP_STATUS.id) {
		    setState(GPRState.IP_STATUS);
		}
		notifyNextState();
	    }

	    return true;
	}
	else {
	    return false;
	}
    }

    private boolean testURC(String line) {
	if(line.startsWith(Sim900Const.PDP_DEACT)) {
	    setState(GPRState.PDP_DEACT);
	    return true;
	}
	else if(line.startsWith(AtConst.CPIN_RE)) {
	    String cpin = getString(line, AtConst.CPIN_RE);
	    setPinStatus(cpin);
	    return true;
	}
	else if(line.startsWith(AtConst.CPIN_RE)) {
	    String cpin = getString(line, AtConst.CPIN_RE);
	    setPinStatus(cpin);
	    return true;
	}
	else if(line.startsWith(AtConst.ERROR)) {
	    setState(GPRState.FUNCTIONAL);
	    notifyNextState();
	    return true;
	}

	return false;
    }

    private boolean testCOPS(String line) {
	String copsLine = getString(line, AtConst.COPS_RE);
	if (copsLine == null) {
	    return false;
	}

        setNetworkSpec(StringUtils.stripToNull(copsLine));

        String[] cops = copsLine.split(",");
	if (cops.length >= 3) {
            if(NumberUtils.toInt(cops[1]) == 2) {//Network ID
                setNetworkId(StringUtils.strip(cops[2], "\""));
                log.debug("networkId: " + networkId);
            }
            else {
                setOperator(StringUtils.strip(cops[2], "\""));
                log.debug("operator: " + operator);
            }
	}
	return true;
    }

    private boolean testCIPSTATUS(String line) {
	String ipStatus = getString(line, Sim900Const.CIPSTATUS_RE);
	if (ipStatus == null) {
	    return false;
	}
	GPRState newState;
	if (ipStatus.equals(Sim900Const.IP_INITIAL)) {
	    newState = GPRState.IP_INITIAL;
	}
	else if (ipStatus.equals(Sim900Const.IP_START)) {
	    newState = GPRState.IP_START;
	}
	else if (ipStatus.equals(Sim900Const.IP_GPRSACT)) {
	    newState = GPRState.IP_GPRSACT;
	}
	else if (ipStatus.equals(Sim900Const.IP_STATUS)) {
	    newState = GPRState.IP_STATUS;
	}
	else if (ipStatus.equals(Sim900Const.CONNECT_OK)) {
	    newState = GPRState.CONNECT_OK;
	}
	else if (ipStatus.equals(Sim900Const.UDP_CLOSED)) {
	    newState = GPRState.UDP_CLOSED;
	}
	else if (ipStatus.equals(Sim900Const.PDP_DEACT)) {
	    newState = GPRState.PDP_DEACT;
	}
	else {
	    return false;
	}
	// If current state is lower than GPRS_READY, we cannot upgrade it
	if (state.id >= GPRState.GPRS_READY.id) {
	    setState(newState);
	    if (nextState != null && newState.id >= nextState.id)
		notifyNextState();
	}
	return true;
    }

    private boolean testConfirmation(String line) {
	if (confirmation == null) {
	    return false;
	}
	else if (nextState != null) {
	    log.debug("testing next state: " + nextState);
	    if (line.equals(confirmation)) {
		setState(nextState);
		notifyNextState();
		return true;
	    }
	}
	else if (Sim900Const.AT_CCID.equals(confirmation)) {
	    if(line.startsWith(Sim900Const.AT_CCID))
		return true;
	    if(!AtConst.ERROR.equals(line))
		setSimIccid(line);
	    notifyConfirmation();
	    return true;
	}
	else if (AtConst.AT_CIMI_RD.equals(confirmation)) {
	    if(line.startsWith(AtConst.AT_CIMI_RD))
		return true;
	    if(!AtConst.ERROR.equals(line))
		setSimIMSI(line);
	    notifyConfirmation();
	    return true;
	}
	else if (AtConst.AT_GSN_RD.equals(confirmation)) {
	    if(line.startsWith(AtConst.AT_GSN_RD))
		return true;
	    if(!AtConst.ERROR.equals(line))
		setModemIMEI(line);
	    notifyConfirmation();
	    return true;
	}
	else if (line.equals(confirmation)) {
	    notifyConfirmation();
	    return true;
	}

	return false;
    }

    private boolean testOk(String line) {
	synchronized (okMonitor) {
	    if (line.equals(AtConst.OK)) {
		okCount++;
		log.trace("ok: " + okCount);
		okMonitor.notifyAll();
		return true;
	    }
	    else {
		return false;
	    }
	}
    }

    private int getDigit(String line, String param) {
	if (line.startsWith(param) == false) {
	    return -2;
	}
	int paramLen = param.length();
	if (line.length() <= paramLen) {
	    return -1;
	}
	int value = CharUtils.toIntValue(line.charAt(paramLen), -1);
	return value;
    }

    private String getString(String line, String param) {
	if (line.startsWith(param)) {
	    return line.substring(param.length());
	}
	else {
	    return null;
	}
    }

    private boolean parseIpAddress(String line) {
	if(line.length() == 0 || line.charAt(0) < '0' || line.charAt(0) > '9')
	    return false;
	String[] split = line.split("\\.");
	if (split.length != 4)
	    return false;

	log.debug("parsing IP");
	int[] ip = new int[4];
	for (int i = 0; i < ip.length; i++) {
	    ip[i] = NumberUtils.toInt(split[i], -1);
	    if (ip[i] < 0 || ip[i] > 255) {
		return false;
	    }
	}

        setIPAddress(StringUtils.trim(line));
	return true;
    }

}
