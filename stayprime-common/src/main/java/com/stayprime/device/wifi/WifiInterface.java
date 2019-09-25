/*
 * 
 */
package com.stayprime.device.wifi;

import java.util.List;

/**
 *
 * @author benjamin
 */
public interface WifiInterface extends WifiInfo {
    public String getInterfaceName();
    public String getMacAddress();

    public boolean isEnabled();
    public void setEnabled(boolean enabled);
    public void restart();

    public boolean isConnected();
    public boolean connect();
    public boolean disconnect();
    
    public String getIPAddress();
    public APInfo getAPInfo();
    public Integer getLinkQuality();
    
    public boolean isAutoConnect();
    public void setAutoConnect(boolean auto);
    
    public List<String> getTargetESSIDs();
    public List<? extends APInfo> scanAccessPoints();
    public List<? extends APInfo> getScanResults();
    
    public void refreshStatus();
}
