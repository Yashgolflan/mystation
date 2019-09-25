/*
 * 
 */

package com.stayprime.device.wifi;

import com.stayprime.util.FormatUtils;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author benjamin
 */
public class WPASupplicantInfo {
    private static final Logger log = LoggerFactory.getLogger(WPASupplicantInfo.class);
    public static final String DEFAULT_CONF_FILE = "/etc/wpa_supplicant.conf";
    
    private String ssidPrefix = "ssid=";

    private String configFile;
    private String essids[];

    public WPASupplicantInfo() {
	this(DEFAULT_CONF_FILE);
    }

    public WPASupplicantInfo(String confFile) {
	this.configFile = confFile;
	parseConfigFile();
    }

    public String[] getESSIDs() {
	return essids;
    }

    private void parseConfigFile() {
	ArrayList<String> ssids = new ArrayList<String>();
	BufferedReader br = null;

	try {
	    log.debug("Reading wpa_supplicant config file: " + configFile);
	    br = new BufferedReader(new FileReader(configFile));

	    String line;

	    while((line = br.readLine()) != null) {
		if(line.trim().startsWith(ssidPrefix)) {
		    String ssid = FormatUtils.getValue(line, ssidPrefix, null);
		    ssid = StringUtils.stripToNull(StringUtils.strip(ssid, "\""));

		    if(ssid != null) {
			log.debug(ssid);
			ssids.add(ssid);
		    }
		}
	    }
	}
	catch (Exception ex) {
	    log.error("Error parsing config file: " + ex);
	    log.trace("Error parsing config file: ", ex);
	}
	finally {
	    IOUtils.closeQuietly(br);
	}

	ssids.trimToSize();
	this.essids = ssids.toArray(new String[0]);
    }

    
}
