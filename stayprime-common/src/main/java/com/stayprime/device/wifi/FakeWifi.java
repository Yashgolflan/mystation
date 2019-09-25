/*
 * 
 */
package com.stayprime.device.wifi;

import java.util.Collections;
import java.util.List;

/**
 *
 * @author benjamin
 */
public class FakeWifi implements WifiInterface {

    public void setEnabled(boolean enabled) {
    }

    public void start() throws Exception {
    }

    public void stop() {
    }
    
    public void restart() {
    }

    public boolean connect() {
	return true;
    }

    public boolean disconnect() {
	return true;
    }

    public boolean isAutoConnect() {
	return true;
    }

    public void setAutoConnect(boolean autoConnect) {
    }

    public List<String> getTargetESSIDs() {
	return Collections.EMPTY_LIST;
    }

    public List<? extends APInfo> scanAccessPoints() {
	return Collections.EMPTY_LIST;
    }

    public List<? extends APInfo> getScanResults() {
	return Collections.EMPTY_LIST;
    }

    public boolean isEnabled() {
	return true;
    }

    public boolean isConnected() {
	return true;
    }

    public String getInterfaceName() {
	return "null";
    }

    public String getMacAddress() {
	return "00:00:00:00:00:00";
    }

    public String getIPAddress() {
	return "0.0.0.0";
    }

    public APInfo getAPInfo() {
	return null;
    }

    public Integer getLinkQuality() {
	return 100;
    }

    public void refreshStatus() {
    }

    public void addWifiInfoObserver(Observer o) {
    }

    public void removeWifiInfoObserver(Observer o) {
    }

}
