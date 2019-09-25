/*
 * 
 */

package com.stayprime.comm.gprs;

import com.stayprime.comm.io.IOStream;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author benjamin
 */
public class Sim900Modem extends GsmAtModem {
    private static final Logger log = LoggerFactory.getLogger(Sim900Modem.class);

    private String apn, user, pass;
    private String serverIP, serverPort;

    private boolean udpExtendedMode = true;
    private int defaultUdpPort = 1000;

    public Sim900Modem(IOStream ioStream) {
        super(ioStream);
    }

    public int getUdpPort() {
        return isActive()? getOutputReader().getUdpPort() : 0;
    }

    @Override
    public String getReceiveAddress() {
        return getIPAddress() + ':' + getUdpPort();
    }

    @Override
    public void setApnConfig(String apn, String user, String pass) {
	this.apn = apn;
	this.user = user;
	this.pass = pass;
    }

    @Override
    public void setServerConfig(String server, String port) {
	serverIP = server;
	serverPort = port;
    }

    @Override
    public synchronized boolean checkConnection() throws IOException {
	log.trace("Checking connection status...");
	boolean ok = startGPRSService() && startIPService();

	if(ok && getState() != GPRState.CONNECT_OK)
	    startUDPConnection();

	return getState() == GPRState.CONNECT_OK;
    }

    public synchronized boolean startIPService() throws IOException {
	if(getState().id < GPRState.GPRS_READY.id)
	    return false;

	checkIPStatus(GPRState.IP_INITIAL, false);

	if(getState().id >= GPRState.PDP_DEACT.id && stopIPService() == false)
	    return false;

        AtOutputReader outputReader = getOutputReader();

        if(getState().id < GPRState.IP_START.id) {
	    //Start task and write APN parameters
	    outputReader.setNextState(GPRState.IP_START, AtConst.OK);
	    writeLine(getParamLine(Sim900Const.AT_CSTT_WR, apn, user, pass));
	    if(!outputReader.waitForState(GPRState.IP_START, AtConst.DELAY1))
		return false;
	}

	if(getState().id < GPRState.IP_GPRSACT.id) {
	    outputReader.setNextState(GPRState.IP_GPRSACT, AtConst.OK);
	    writeLine(Sim900Const.AT_CIICR);
	    if(!outputReader.waitForState(GPRState.IP_GPRSACT, AtConst.DELAY60))
		return false;
	}

	if(getState().id < GPRState.IP_STATUS.id) {
	    outputReader.setNextState(GPRState.IP_STATUS, null);
	    writeLine(Sim900Const.AT_CIFSR);
	    if(!outputReader.waitForState(GPRState.IP_STATUS, AtConst.DELAY30))
		return false;
	}

	return true;
    }

    private boolean checkIPStatus(GPRState ipState, boolean equalsOrGreater) throws IOException {
	// Check if modem is in IP STATUS or higher
        AtOutputReader outputReader = getOutputReader();
	outputReader.setNextState(ipState, null);
	writeLine(Sim900Const.AT_CIPSTATUS);
	return outputReader.waitForOk(AtConst.DELAY1)
		&& outputReader.waitForState(ipState, AtConst.DELAY1, equalsOrGreater);
    }

    private boolean stopIPService() throws IOException {
        // Set the status to GPRS_READY after shutting PDP context off to
        // always have a change to a higher state when checking IP status
        AtOutputReader outputReader = getOutputReader();

        outputReader.setNextState(GPRState.GPRS_READY, Sim900Const.SHUT_OK);
	writeLine(Sim900Const.AT_CIPSHUT);
	if(!outputReader.waitForConfirmation(AtConst.DELAY10))
	    return false;

	return checkIPStatus(GPRState.IP_INITIAL, true);
    }

    private boolean startUDPConnection() throws IOException {
	if(serverIP == null || serverPort == null)
	    return false;

	if(getState() == GPRState.CONNECT_OK)
	    closeConnection();

        AtOutputReader outputReader = getOutputReader();

	if(getState() == GPRState.IP_STATUS || getState() == GPRState.UDP_CLOSED) {
	    outputReader.setNextState(GPRState.CONNECT_OK, Sim900Const.CONNECT_OK);
	    writeLine(getParamLine(Sim900Const.AT_CIPSTART_WR, Sim900Const.UDP, serverIP, serverPort));

	    return outputReader.waitForOk(AtConst.DELAY10)
		    && outputReader.waitForState(GPRState.CONNECT_OK, AtConst.DELAY10);
	}

	return false;
    }

    public synchronized boolean closeConnection() throws IOException {
        AtOutputReader outputReader = getOutputReader();

        if(getState() == GPRState.CONNECT_OK) {
	    outputReader.setNextState(null, Sim900Const.CLOSE_OK);
	    writeLine(Sim900Const.AT_CIPCLOSE);

	    if(outputReader.waitForConfirmation(AtConst.DELAY5))
		return checkIPStatus(GPRState.UDP_CLOSED, true);
	}

	return false;
    }

    @Override
    public synchronized boolean sendData(byte data[], int len) throws IOException {
        AtOutputReader outputReader = getOutputReader();
        int offset = 0;

        if(getState() == GPRState.CONNECT_OK) {
	    outputReader.setNextState(null, Sim900Const.SEND_OK);
	    writeLine(Sim900Const.AT_CIPSEND);

	    //TODO: Improve wait for prompt!
	    try {Thread.sleep(100);}
	    catch (InterruptedException ex) {}

	    for(int i = offset; i < len; i++) {
		writeEscaped(data[i]);
	    }

            writeAndFlush(AtConst.EOF);

	    if(outputReader.waitForConfirmation(AtConst.DELAY10))
		return true;
	}

	return false;
    }

    @Override
    protected void checkSettings() throws IOException {
        super.checkSettings();
	writeLine(Sim900Const.AT_CLPORT_RD);
        getOutputReader().waitForOk(AtConst.DELAY1);
    }

    @Override
    protected boolean sendInitializeCommands() throws IOException {
        AtOutputReader outputReader = getOutputReader();

        boolean ok = super.sendInitializeCommands();

        //Sim900 specifics:
        //Read SIM ICCID
        outputReader.setNextState(null, Sim900Const.AT_CCID);
        writeLine(Sim900Const.AT_CCID);
        ok &= outputReader.waitForConfirmation(AtConst.DELAY1);
        ok &= outputReader.waitForOk(AtConst.DELAY1);

        //Shut down IP mode
	outputReader.setNextState(null, Sim900Const.SHUT_OK);
	writeLine(Sim900Const.AT_CIPSHUT);
	ok &= outputReader.waitForConfirmation(AtConst.DELAY10);

        //Send initial commands
	StringBuilder builder = new StringBuilder(AtConst.AT);
	builder.append(Sim900Const.CIPHEAD_SET1).append(';');
	builder.append(Sim900Const.CLPORT_SET_UDP).append(defaultUdpPort).append(';');
	builder.append(Sim900Const.CIPUDPMODE_WR).append(udpExtendedMode? '1' : '0').append(';');
	writeLine(builder.toString());
	ok &= outputReader.waitForOk(AtConst.DELAY1);

	return ok;
    }

    private String getParamLine(String prefix, String ... params) throws IOException {
	StringBuilder line = new StringBuilder(prefix);
	int last = params.length - 1;

	for(int i = 0; i <= last; i++) {
	    line.append('"').append(params[i]).append('"');
	    if(i < last) line.append(',');
	}

	return line.toString();
    }

}
