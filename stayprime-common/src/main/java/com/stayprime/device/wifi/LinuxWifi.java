/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.stayprime.device.wifi;

import com.stayprime.util.FormatUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author benjamin
 */
public class LinuxWifi implements WifiInterface {
    private static final Logger log = LoggerFactory.getLogger(LinuxWifi.class);

    private final String interfaceName;
    private String macAddress;
    private String ipAddress;
    private BasicAPInfo apInfo;
    private Integer linkQuality;
    private boolean autoConnect;
    private boolean enabled;
    private boolean disconnect;
    private List<String> targetEssids;

    //This has to be moved outside of this class
    //the dhcpRetries could be just a parameter to a DHCP request method.
    private int dhcpRetries = 5;
    private int connectRetries = 3;
    private long connectDelay = 5000l;

    private APList<BasicAPInfo> scan;
    private APList<BasicAPInfo> scanResults;
    private List<BasicAPInfo> roScanResults;
    private List<Observer> observers;

    private final String ifconfigCmd;
    private final String iwconfigCmd;
    private String restartCmd;
    private String stopCmd;
    private final String scanCmd;
    private final String reconnectCmd;
    private final String disconnectCmd;
    private final String deassociateCmd;
    private final String iwlistCommand;
    private final String dhcpCommand;

    private static final String macPrefix = "HWaddr ";
    private static final String ipPrefix = "inet addr:";
    private static final String essidPrefix = "ESSID:";
    private static final String modePrefix = "Mode:";
    private static final String modeManaged = "Managed";
    private static final String bssidPrefix = "Access Point: ";
    private static final String notAssociated = "Not-Associated";
    private static final String freqPrefix = "Frequency";
    private static final String iwFreqSuffix = " (";
    private static final String dbmSuffix = " dBm";
    private static final String qualityPrefix = "Quality=";
    private static final String signalPrefix = "Signal level";
    private static final String cellPrefix = "Cell ";
    private static final String addressPrefix = "Address: ";
    private static final String separator = "  ";
    private static final String langEnv[] = {"LANG=C"};

    public LinuxWifi(String interfaceName) {
	log.debug("Creating LinuxWif, interface=" + interfaceName);
	this.interfaceName = interfaceName;

	//Init commands
	ifconfigCmd = "ifconfig " + interfaceName;
	iwconfigCmd = "iwconfig " + interfaceName;
	restartCmd = "/etc/rc.d/init.d/wlan restart";
	stopCmd = "/etc/rc.d/init.d/wlan stop";
        scanCmd = "wpa_cli -i " + interfaceName + " scan";
	reconnectCmd = "wpa_cli -i " + interfaceName + " reconnect";
	disconnectCmd = "wpa_cli -i " + interfaceName + " disconnect";
	deassociateCmd = "iwconfig " + interfaceName + " essid off ap off";
	iwlistCommand = "iwlist " + interfaceName + " scan";
	dhcpCommand = "udhcpc -qn -i " + interfaceName + " -t ";

	scan = new APList<BasicAPInfo>();
	scanResults = new APList<BasicAPInfo>();
	roScanResults = Collections.unmodifiableList(scan);
	observers = new ArrayList<Observer>(1);

    }

    public void setRestartCmd(String restartCmd) {
        this.restartCmd = restartCmd;
    }

    public void setStopCmd(String stopCmd) {
        this.stopCmd = stopCmd;
    }

    public void start() throws Exception {
	log.debug("Entering start()");
	readIfconfig();
	readIwconfig();
    }

    public void stop() {
    }
    
    public void setTargetESSIDs(List<String> essids) {
	if(log.isDebugEnabled() && essids != null)
	    log.debug("targetEssids=" + Arrays.toString(essids.toArray()));

	this.targetEssids = Collections.unmodifiableList(new ArrayList<String>(essids));
    }

    public List<String> getTargetESSIDs() {
	return targetEssids;
    }
    
    /*
     * WiFi control
     */

    public void setEnabled(boolean enabled) {
	setEnabled(false, enabled);
    }

    public void restart() {
	setEnabled(true, true);
    }

    public void setConnectRetries(int connectRetries) {
	this.connectRetries = connectRetries;
    }

    public void setConnectDelay(long connectDelay) {
	this.connectDelay = connectDelay;
    }

    public void setDhcpRetries(int dhcpRetries) {
	this.dhcpRetries = dhcpRetries;
    }

    public boolean connect() {
	disconnect = false;
	runConnectCommand();
	
	try {
	    for(int i = 0; i < connectRetries; i++) {
		log.trace("Trying to connect");
		Thread.sleep(connectDelay);

		//Should only run dhcpCommand if we are associated
		int exit = Runtime.getRuntime().exec(dhcpCommand+dhcpRetries).waitFor();
		if(exit == 0) {
		    log.debug("Connection succeeded");
		    refreshStatus();
		    notifyObservers();
		    return true;
		}
	    }
	}
	catch (Exception ex) {
	    log.warn("Error trying to connect: " + ex);
	}

	log.debug("Connection failed, return false");
	return false;
    }
    
    public boolean disconnect() {
	disconnect = true;
	if(isAutoConnect() == false) {
	    log.trace("Disconnecting: isAutoConnect() == false");
	    if(runDisconnectCommand()) {
		refreshStatus();
		notifyObservers();
		return true;
	    }
	    else return false;
	}
	else {
	    log.trace("Not disconnecting: isAutoConnect() == true");
	    return isConnected() == false;
	}
    }

    public boolean isAutoConnect() {
	return autoConnect;
    }

    public void setAutoConnect(boolean autoConnect) {
	this.autoConnect = autoConnect;

	refreshStatus();
	if(autoConnect)
	    //reconnectCmd won't have any effect if already connected
	    runConnectCommand();
	else if(disconnect)
	    //disconnectCmd only if explicit disconnection was requested
	    runDisconnectCommand();
    }
    
    /*
     * WiFi status
     */
    
    public void refreshStatus() {
	readIfconfig();
	readIwconfig();
    }

    public String getInterfaceName() {
	return interfaceName;
    }

    public boolean isEnabled() {
	return enabled;
    }

    public boolean isConnected() {
	return enabled && apInfo != null && ipAddress != null;
    }

    public String getMacAddress() {
	return macAddress;
    }

    public String getIPAddress() {
	return ipAddress;
    }

    public APInfo getAPInfo() {
	return apInfo;
    }

    public Integer getLinkQuality() {
	return linkQuality;
    }

    public List<BasicAPInfo> getScanResults() {
	return roScanResults;
    }

    public List<BasicAPInfo> scanAccessPoints() {
	readIwlist();
	return getScanResults();
    }

    /*
     * Utility Methods
     */

    private boolean setEnabled(boolean restart, boolean enabled) {
	refreshStatus();

	if(restart || this.enabled != enabled) {
	    BufferedReader br;

	    try {
		String command = restart || enabled? restartCmd : stopCmd;
		log.debug("Running wifi command: " + command);

		Process process = Runtime.getRuntime().exec(command);
		br = new BufferedReader(new InputStreamReader(process.getInputStream()));
		while(br.readLine() != null) continue;
		process.waitFor();
	    }
	    catch(Exception ex) {
		log.error("Error setting wifi enabled: " + ex);
		//throw new RuntimeException("Error setting wifiEnabled=" + enabled + ": " + ex);
	    }
	    finally {
		refreshStatus();
		
		boolean success = this.enabled == enabled;
		if(success == false)
		    log.warn("Setting wifiEnabled=" + enabled + " didn't work");
		return success;
	    }
	}
	
	return true;
    }

    private boolean runConnectCommand() {
	return runCommands(reconnectCmd);
    }

    private boolean runDisconnectCommand() {
	return runCommands(disconnectCmd, deassociateCmd);
    }

    private boolean runCommands(String ... commands) {
	String command = null;
	try {
	    for(String comm: commands) {
		command = comm;
		log.trace(command);
		Runtime.getRuntime().exec(command).waitFor();
	    }
	    return true;
	}
	catch (Exception ex) {
	    log.error("Error running command " + command + ": " + ex);
	    return false;
	}
    }

    private Process execWithLang(String command) throws IOException {
	return Runtime.getRuntime().exec(command, langEnv);
    }

    private boolean readIfconfig() {
	Process process = null;
	BufferedReader br = null;
	try {
	    String command = ifconfigCmd;
	    log.trace("Running ifconfig: " + command);
	    process = execWithLang(command);
	    br = new BufferedReader(new InputStreamReader(process.getInputStream()));

	    String line = br.readLine();
	    enabled = line != null && line.startsWith(interfaceName);

	    if(enabled) {
		String newMac = FormatUtils.getValue(line, macPrefix, separator);
		newMac = StringUtils.stripToNull(newMac);
		newMac = StringUtils.lowerCase(newMac);

		String oldMac = getMacAddress();
		if(oldMac != null && oldMac.equalsIgnoreCase(newMac) == false)
		    log.warn("MAC address changed from " + oldMac + " to " + newMac);
		macAddress = newMac;

		line = br.readLine();

		String ip = FormatUtils.getValue(line, ipPrefix, separator);
		ipAddress = StringUtils.stripToNull(ip);
	    }
	    else {
		ipAddress = null;
	    }

	    while(br.readLine() != null) continue;
	    process.waitFor();
	    return true;
	}
	catch (Exception ex) {
	    log.error("Error reading ifconfig: " + ex);
	    log.trace("Error reading ifconfig: ", ex);
	    return false;
	}
	finally {
	    IOUtils.closeQuietly(br);
	    process.destroy();
	}
    }

    private boolean readIwconfig() {
	Process process = null;
	BufferedReader br = null;
	try {
	    String command = iwconfigCmd;
	    log.trace("Running iwconfig: " + command);
	    process = Runtime.getRuntime().exec(command);
	    br = new BufferedReader(new InputStreamReader(process.getInputStream()));

	    String line = br.readLine();

	    if(enabled && line != null && line.startsWith(interfaceName)) {
		String value;

		//Read ESSID from first line
		value = FormatUtils.getValue(line, essidPrefix, separator);
		value = StringUtils.stripToNull(value);
		String ESSID = StringUtils.strip(value, "\"");

		line = br.readLine();
		//Read mode from second line
		value = FormatUtils.getValue(line, modePrefix, separator);
		value = StringUtils.stripToNull(value);
		String mode = value;
		//Read frequency from second line
		value = FormatUtils.getValue(line, freqPrefix, separator, 1);
		String frequency = StringUtils.stripToNull(value);
		//Read AP BSSID from second line if != "Not Asssociated"
		value = FormatUtils.getValue(line, bssidPrefix, separator);
		value = StringUtils.stripToNull(value);
		String BSSID = null;
		if(ObjectUtils.notEqual(value, notAssociated))
		    BSSID = StringUtils.lowerCase(value);

		//Validate AP
		if(BSSID == null || ESSID == null || !modeManaged.equals(mode))
		    apInfo = null;
		//If connected to a new AP, get it from the search results or create one
		//TODO: change to check BSSID not ESSID for equality
		else if(apInfo == null || !StringUtils.equalsIgnoreCase(apInfo.getESSID(), ESSID)) {
		    BasicAPInfo info = scanResults.getBSSID(BSSID);
		    if(info != null)
			apInfo = info;
		    else
			apInfo = new BasicAPInfo(BSSID);
		}

		if(apInfo != null) {
		    apInfo.setESSID(ESSID);
		    apInfo.setFrequency(frequency);
		}

		//Look for link quality
		linkQuality = null;
		while((line = br.readLine()) != null) {
		    value = StringUtils.stripToNull(FormatUtils.getValue(line, qualityPrefix, separator));

		    if(value != null) {
			String levels[] = value.split("/");
			int level = NumberUtils.toInt(levels[0]);

			if(levels.length == 2) {
			    int max = NumberUtils.toInt(levels[1]);
			    if(max > 0)
				level = level*100/max;
			}

			linkQuality = level;
		    }

		}
	    }
	    else {
		apInfo = null;
		linkQuality = null;
		while((line = br.readLine()) != null) continue;
	    }

	    process.waitFor();
	    return true;
	}
	catch (Exception ex) {
	    log.error("Error reading iwconfig: " + ex);
	    log.trace("Error reading iwconfig: ", ex);
	    return false;
	}
	finally {
	    IOUtils.closeQuietly(br);
	    process.destroy();
	}
    }

    private boolean isESSIDValid(String essid) {
	if(essid == null)
	    return false;

	if(targetEssids == null)
	    return true;

	for(String ess: targetEssids)
	    if(essid.equals(ess))
		return true;

	return false;
    }

    private void readIwlist() {
	scan.clear();
	Process process = null;
	BufferedReader br = null;
	try {
	    String command = iwlistCommand;
	    log.trace("Running iwlist: " + command);
	    process = Runtime.getRuntime().exec(command);
	    br = new BufferedReader(new InputStreamReader(process.getInputStream()));

	    String line = br.readLine();
	    if(line != null && line.startsWith(interfaceName)) {
		line = br.readLine();

		while(line != null) {
		    log.trace(line);
		    if(line.trim().startsWith(cellPrefix)) {
			//Read cell address
			String address = StringUtils.stripToNull(FormatUtils.getValue(line, addressPrefix, null));
			address = StringUtils.lowerCase(address);

			if(address != null) {
			    //Check if it's already in our list to avoid creating a new one
			    BasicAPInfo ap = scanResults.getBSSID(address);

			    if(ap == null)
				ap = new BasicAPInfo(address);

			    line = br.readLine();
			    String signal = null;
			    while(line != null && !line.trim().startsWith(cellPrefix)) {
				String value = FormatUtils.getValue(line, essidPrefix, separator);
				if(value != null) {
				    value = StringUtils.strip(value, "\"");
				    ap.setESSID(StringUtils.stripToNull(value));
				}

				value = FormatUtils.getValue(line, freqPrefix, separator, 1);
				if(value != null) {
				    int i = value.indexOf(iwFreqSuffix);
				    if(i >= 0)
					value = value.substring(0, i);
				}

				if(value != null)
				    ap.setFrequency(StringUtils.stripToNull(value));

				value = StringUtils.stripToNull(FormatUtils.getValue(line, qualityPrefix, separator));
				//If the quality hasn't been found on this iteration or before, use signalPrefix
				if(value == null && signal == null)
				    value = StringUtils.stripToNull(FormatUtils.getValue(line, signalPrefix, separator, 1));

				if(value != null) {
				    signal = value;
				    ap.setSignalLevel(parseSignalLevel(value));
				}

				line = br.readLine();
			    }

			    log.trace("{}", ap);
			    if(isESSIDValid(ap.getESSID())) {
				log.trace("AP is valid, adding to list");
				scan.add(ap);
			    }
			}
		    }
		    else
			line = br.readLine();
		}
	    }

	    while(br.readLine() != null) continue;
	    process.waitFor();
	}
	catch (Exception ex) {
	    log.error("Error reading iwlist: " + ex);
	    log.trace("Error reading iwlist: ", ex);
	}
	finally {
	    IOUtils.closeQuietly(br);
	    process.destroy();
	}

	scan.trimToSize();
	scanResults.clear();
	scanResults.addAll(scan);
	scanResults.trimToSize();
    }

    public static Integer parseSignalLevel(String value) {
	int suffix;

	suffix = value.indexOf('/');
	if(suffix >= 0) {
	    String levels[] = value.split("/");
	    int level = NumberUtils.toInt(levels[0]);

	    if(levels.length == 2) {
		int max = NumberUtils.toInt(levels[1]);
		if(max > 0)
		    return level*100/max;
	    }
	}

	suffix = value.indexOf(dbmSuffix);
	if(suffix >= 0) {
	    int dbm = NumberUtils.toInt(value.substring(0, suffix));
	    if(dbm  < 0) {
		if(dbm < -95)
		    dbm = -95;
		else if(dbm > -35)
		    dbm = -35;
		
		return (dbm + 95) * 100 / 60;
	    }
	}
	
	int level = NumberUtils.toInt(value, -1);

	if(level >= 0)
	    return level;
	else
	    return null;
    }

    public void addWifiInfoObserver(Observer o) {
	observers.add(o);
    }

    public void removeWifiInfoObserver(Observer o) {
	observers.remove(o);
    }

    private void notifyObservers() {
	for(Observer o: observers)
	    o.wifiStatusChanged(this);
    }

}
