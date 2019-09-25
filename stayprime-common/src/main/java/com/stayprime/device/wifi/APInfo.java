/*
 * 
 */

package com.stayprime.device.wifi;

/**
 *
 * @author benjamin
 */
public interface APInfo {
    public String getBSSID();
    public String getESSID();
    public String getFrequency();
    public Integer getSignalLevel();
    public Integer getNoiseLevel();
    public boolean equals(APInfo info);
}
