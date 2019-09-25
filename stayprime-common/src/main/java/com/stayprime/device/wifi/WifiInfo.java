/*
 * 
 */

package com.stayprime.device.wifi;

/**
 *
 * @author benjamin
 */
public interface WifiInfo {
    //From deleted interface NetInfo
//    public String getMacAddress();
//    public String getIPAddress();
//    public boolean isConnected();
//    public void refreshStatus();

    public String getInterfaceName();
    public String getMacAddress();

    public boolean isEnabled();
    
    public boolean isConnected();

    public String getIPAddress();
    public APInfo getAPInfo();
    public Integer getLinkQuality();

    public void refreshStatus();
    
    public void addWifiInfoObserver(Observer o);
    public void removeWifiInfoObserver(Observer o);
    
    public interface Observer {
	public void wifiStatusChanged(WifiInfo wifiInfo);
    }
}
