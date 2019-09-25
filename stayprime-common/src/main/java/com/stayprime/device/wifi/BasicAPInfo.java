/*
 * 
 */

package com.stayprime.device.wifi;

import com.stayprime.device.wifi.APInfo;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author benjamin
 */
public class BasicAPInfo implements APInfo {
    private String BSSID;
    private String ESSID;
    private String frequency;
    private Integer signalLevel;
    private Integer noiseLevel;

    public BasicAPInfo(String BSSID) {
	this(BSSID, null, null, null, null);
    }

    public BasicAPInfo(String BSSID, String ESSID, String frequency, Integer signalLevel, Integer noiseLevel) {
	this.BSSID = BSSID;
	this.ESSID = ESSID;
	this.frequency = frequency;
	this.signalLevel = signalLevel;
	this.noiseLevel = noiseLevel;
    }

    public String getBSSID() {
	return BSSID;
    }

    public void setESSID(String ESSID) {
	this.ESSID = ESSID;
    }

    public String getESSID() {
	return ESSID;
    }

    public void setFrequency(String frequency) {
	this.frequency = frequency;
    }

    public String getFrequency() {
	return frequency;
    }

    public void setSignalLevel(Integer signalLevel) {
	this.signalLevel = signalLevel;
    }

    public Integer getSignalLevel() {
	return signalLevel;
    }

    public void setNoiseLevel(Integer noiseLevel) {
	this.noiseLevel = noiseLevel;
    }

    public Integer getNoiseLevel() {
	return noiseLevel;
    }

    public boolean equals(APInfo info) {
	return info != null && StringUtils.equals(BSSID, info.getBSSID());
    }

    @Override
    public String toString() {
	StringBuilder s = new StringBuilder();
	s.append("AP: ").append(BSSID);
	s.append("  ESSID: ").append(ESSID);
	s.append("  Frequency: ").append(frequency);
	if(signalLevel != null)
	    s.append("  Signal: ").append(signalLevel);
	if(noiseLevel != null)
	    s.append("  Noise: ").append(noiseLevel);

	return s.toString();
    }

}
