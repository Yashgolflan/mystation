/*
 *
 */
package com.stayprime.comm.gprs;

import com.stayprime.comm.PacketReceiver;
import com.stayprime.comm.BytePacketUtils;
import com.stayprime.comm.io.IOStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author benjamin
 */
public class GsmAtModem implements GsmModem {
    protected static final Logger log = LoggerFactory.getLogger(GsmAtModem.class);
    private static final Logger gprsLog = LoggerFactory.getLogger("gprs");

    private final IOStream ioStream;
    private BufferedOutputStream out;
    private AtOutputReader outputReader;

    private boolean gprsReattach = false;

    private String forceOperator, networkSpec;
    private boolean networkSpecSet = false;
    private PacketReceiver packetReceiver;
    private boolean sendEscaped = true;

    private boolean logGprsEvents = false;
    private final GPRSEvent gprsEvent;

    public GsmAtModem(IOStream ioStream) {
	this.ioStream = ioStream;
        this.gprsEvent = new GPRSEvent();
    }

    public void setLogGprsEvents(boolean logGprsEvents) {
        this.logGprsEvents = logGprsEvents;
    }

    public void setSendEscaped(boolean enabled) {
	this.sendEscaped = enabled;
    }

    public boolean isSendEscaped() {
	return sendEscaped;
    }

    public boolean isActive() {
        return outputReader != null && outputReader.isRunning();
    }

    public long getLastReplyNanoTime() {
        return outputReader.getLastReplyNanoTime();
    }

    @Override
    public void setPacketReceiver(PacketReceiver receiver) {
	this.packetReceiver = receiver;
    }

    /**
     * Network spec to pass to the force operator AT command (AT+COPS=1,).
     * @param networkSpec network spec to force network
     */
    public void setNetworkSpec(String networkSpec) {
        log.debug("setNetworkSpec: " + networkSpec);
        this.networkSpec = networkSpec;
    }

    /**
     * Operator name to display whenever we force to a specific network.
     * @param forceOperator operator display name
     */
    public void setForceOperator(String forceOperator) {
        this.forceOperator = forceOperator;
    }

    @Override
    public void setApnConfig(String apn, String user, String pass) {
        log.warn("setApnConfig not implemented");
    }

    @Override
    public void setServerConfig(String server, String port) {
        log.warn("setServerConfig not implemented");
    }

    public void setGprsReattach(boolean gprsReattach) {
	this.gprsReattach = gprsReattach;
    }

    public String getReceiveAddress() {
	return getIPAddress();
    }

    @Override
    public String getIPAddress() {
	return outputReader == null? null : outputReader.getIPAddress();
    }

    @Override
    public GPRState getState() {
	return outputReader == null? GPRState.UNKNOWN : outputReader.getGPRSState();
    }

    @Override
    public String getModemIMEI() {
	return outputReader == null? null : outputReader.getModemIMEI();
    }

    @Override
    public boolean isSimInserted() {
	return outputReader == null? false : outputReader.isSimInserted();
    }

    @Override
    public String getSimIccid() {
	return outputReader == null? null : outputReader.getSimIccid();
    }

    @Override
    public String getSimIMSI() {
	return outputReader == null? null : outputReader.getSimIMSI();
    }

    @Override
    public String getOperator() {
        if(outputReader == null) {
            return null;
        }
        else if(forceOperator != null && networkSpec != null && networkSpec.equals(outputReader.getNetworkSpec())) {
            //If we are using networkSpec and forceOperator, then return the forceOperator name
            return forceOperator;
        }
        else {
            return outputReader.getOperator();
        }
    }

    @Override
    public String getNetworkId() {
	return outputReader == null? null : outputReader.getNetworkId();
    }

    @Override
    public int getSignalQuality() {
        return outputReader == null? null : outputReader.getSignalQuality();
    }

    @Override
    public synchronized boolean start() throws Exception  {
        try {
            log.debug("Starting GPRS modem");
            ioStream.start();
            out = new BufferedOutputStream(ioStream.getOutputStream());

            outputReader = new AtOutputReader(ioStream.getInputStream());
            outputReader.setPacketReceiver(packetReceiver);
            outputReader.start();

            log.trace("GPRS modem started");

            return true;
        }
        catch(Exception ex) {
            log.warn(ex.toString());
            outputReader = null;
            throw ex;
        }
    }

    @Override
    public synchronized boolean stop() {
        if(ioStream.isOpen()) {
            log.debug("Stopping I/O streams");
            ioStream.stop();
        }

        if(outputReader != null) {
            log.debug("Waiting for output reader thread");
            try { outputReader.join(10000); }
            catch (InterruptedException ex) {}
            outputReader = null;
        }

	return true;
    }

    public boolean reset() {
        try {
            sendResetCommand();
            return true;
        }
        catch (IOException ex) {
            log.warn(ex.toString());
            return false;
        }
    }

    private void sendResetCommand() throws IOException {
        log.debug("Modem reset sequence.");
        outputReader.resetState();
        //TODO verify if this conditions is specific to SIM900 modem
        outputReader.setNextState(null, AtConst.CALL_READY);
        writeLine(AtConst.AT_CFUN_RESET);
        outputReader.waitForConfirmation(AtConst.DELAY60);
        networkSpecSet = false;
        logState("reseting modem");
        startGPRSService();
    }

    public void resetState() {
        if (outputReader != null) {
            outputReader.resetState();
            networkSpecSet = false;
        }
    }

    @Override
    public synchronized boolean checkConnection() throws IOException {
	log.warn("checkConnection() not implemented");
        return false;
    }

    @Override
    public synchronized boolean sendData(byte data[], int len) throws IOException {
        log.warn("sendData() not implemented");
	return false;
    }

    public boolean startGPRSService() throws IOException {
	log.trace("Checking GPRS service...");
	//Check functionality first
	if (checkFunctionality() == false) {
            networkSpecSet = false;
	    return false;
        }

        checkSettings();

        if (checkOperator(true) == false) {
	    return false;
        }

	boolean attached = checkAttached();

	if (attached == false && gprsReattach) {
	    // Attach to the GPRS service in case we are not attached
	    // This has been observed to freeze the modem,
	    // thus gprsReattach is false by default
	    writeLine(AtConst.AT_CGATT_WR + "1");
	    outputReader.waitForOk(AtConst.DELAY60);

	    attached = checkAttached();

            logState("GPRS attach: " + attached);
	}

	return attached;
    }

    public synchronized boolean stopGPRSService() throws IOException {
	log.trace("Stopping GPRS service...");
	writeLine(AtConst.AT_CGATT_WR + "0");
	return outputReader.waitForOk(AtConst.DELAY10);
    }

    private boolean checkFunctionality() throws IOException {
        if(isActive() == false) {
            log.debug("Output reader not active");
            return false;
        }

	if(outputReader.isInitialized() && getState().id >= GPRState.FUNCTIONAL.id) {
	    log.trace("Already in FUNCTIONAL state.");
	    return true;
	}

	log.debug("Checking functionality...");
	//If the modem is in binary send mode, we need to exit
        //TODO SIM900 specific, move into Sim900Modem class
	if(getState() == GPRState.UNKNOWN) {
            writeEscapeChars();
	}

        logState("Checking GPRS");

        if (sendInitializeCommands()) {
            outputReader.setInitialized(true);

            outputReader.setNextState(GPRState.FUNCTIONAL, null);
            writeLine(AtConst.AT_CFUN_RD);
            if(outputReader.waitForState(GPRState.FUNCTIONAL, AtConst.DELAY10)
                    && outputReader.waitForOk(AtConst.DELAY1)) {
                if(getState().id >= GPRState.FUNCTIONAL.id)
                    return true;
            }
        }

        logState("not functional");
        //No success, return false
	outputReader.resetState();
	return false;
    }

    protected void checkSettings() throws IOException {
	writeLine(AtConst.AT_CSQ_RD);
        outputReader.waitForOk(AtConst.DELAY1);
    }

    protected boolean sendInitializeCommands() throws IOException {
	boolean ok = true;

	writeLine(AtConst.ATE1);
	ok &= outputReader.waitForOk(AtConst.DELAY1);

	outputReader.setNextState(null, AtConst.AT_GSN_RD);
	writeLine(AtConst.AT_GSN_RD);
	ok &= outputReader.waitForConfirmation(AtConst.DELAY1);
	ok &= outputReader.waitForOk(AtConst.DELAY1);

	writeLine(AtConst.AT_CPIN_RD);
	ok &= outputReader.waitForOk(AtConst.DELAY1);

        outputReader.setNextState(null, AtConst.AT_CIMI_RD);
	writeLine(AtConst.AT_CIMI_RD);
	ok &= outputReader.waitForConfirmation(AtConst.DELAY1);
	ok &= outputReader.waitForOk(AtConst.DELAY1);

	return ok;
    }

    private boolean checkAttached() throws IOException {
	// Check if connected to GPRS service
        outputReader.setNextState(GPRState.GPRS_READY, null);
        writeLine(AtConst.AT_CGATT_RD);
        boolean attached = outputReader.waitForState(GPRState.GPRS_READY, AtConst.DELAY1);
        outputReader.waitForOk(AtConst.DELAY1);

	return attached;
    }

    private boolean checkOperator(boolean set) throws IOException {
	writeLine(AtConst.AT_COPS_RD);
	boolean ok = outputReader.waitForOk(AtConst.DELAY10);

        if (networkSpecSet) {
            //If we already set the network spec (AT+COPS=1,...)
            //let's give the modem the opportunity to try to connect
            return true;
        }

        if (networkSpec == null) {
            //No network spec - don't send AT+COPS=0, to the modem
            //It can cause problems for SIM900, so only do it if explicitly set
	    return true;
        }
        else {
            if (outputReader.getNetworkSpec() != null && outputReader.getNetworkSpec().startsWith(networkSpec)) {
                //Network spec matches, don't set
                return true;
            }
            else if (set) {
                //TODO We could check for all the available operators using AT+COPS=?
                //and select from different options.
                //The AT+COPS=? command may take a long time to return results.
                //For now we will only use the first forceOperator in the list.
                writeLine(AtConst.AT_COPS_WR + networkSpec);

                ok = outputReader.waitForOk(AtConst.DELAY30);
                networkSpecSet = ok;
                //For now, only set network spec once per modem reset cycle, so return true
                return true;
                //return checkOperator(false);
            }
        }

        return false;
    }

    protected void writeLine(String line) throws IOException {
        try {
            log.trace(line);
            outputReader.setEchoLine(line);
            out.write(line.getBytes());
            out.write(AtConst.NL);
            out.flush();
        }
        catch(IOException ex) {
            stop();
            throw ex;
        }
    }

    protected void writeEscaped(byte b) throws IOException {
        try {
            if(sendEscaped)
                BytePacketUtils.writeEscaped(out, b);
            else
                out.write(b);
        }
        catch(IOException ex) {
            stop();
            throw ex;
        }
    }

    protected void writeAndFlush(int ... bytes) throws IOException {
        try {
            for(int b: bytes)
                out.write(b);

            out.flush();
        }
        catch(IOException ex) {
            stop();
            throw ex;
        }

    }

    protected void writeEscapeChars() throws IOException {
        writeAndFlush(AtConst.EOF, AtConst.NL);
        writeAndFlush(AtConst.ESCAPE, AtConst.NL);
    }

    /**
     * @return the outputReader
     */
    protected AtOutputReader getOutputReader() {
        return outputReader;
    }

    public String getStatusLine() {
        StringBuilder status = new StringBuilder("GPRS ");
        status.append(getState()).append("   ");

        if(getState().id >= GPRState.FUNCTIONAL.id) {
            if(!isSimInserted())
                status.append("NO SIM").append("  ");
            else if(getSimIccid() != null)
                status.append("SIM: ").append(StringUtils.right(getSimIccid(), 6)).append("  ");
            if(getOperator() != null)
                status.append(getOperator()).append("  ");
            if(getNetworkId()!= null)
                status.append(getNetworkId()).append("  ");
            if(getSignalQuality() >= 0 && getSignalQuality() < 99)
                status.append("CSQ: ").append(getSignalQuality()).append("  ");
            if(getIPAddress() != null)
                status.append("IP: ").append(getReceiveAddress());
        }

        return status.toString();
    }

    private void logState(String state) {
        gprsEvent.setConnectionState(state);
        gprsEvent.setNetworkName(getOperator());
        gprsEvent.setNetworkCode(getNetworkId());
        gprsEvent.setSignal(Integer.toString(getSignalQuality()));

        if (logGprsEvents) {
            gprsLog.info(gprsEvent.toString());
        }
    }

}
