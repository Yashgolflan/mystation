/*
 *
 */

package com.stayprime.basestation2.ui.modules;

/**
 *
 * @author benjamin
 */
public class CartUpdateStatus implements Comparable<Object> {
    private final String macAddress;
    private String lastUpdate;
    private String inProgressUpdate;
    private String error;
    private Float progress;

    public CartUpdateStatus(String macAddress, String lastUpdate) {
        this.macAddress = macAddress;
	this.lastUpdate = lastUpdate;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public String getLastUpdate() {
	return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
	this.lastUpdate = lastUpdate;
    }

    public String getInProgressUpdate() {
	return inProgressUpdate;
    }

    public void setInProgressUpdate(String inProgressUpdate) {
	this.inProgressUpdate = inProgressUpdate;
    }

    public Float getProgress() {
	return progress;
    }

    public void setProgress(Float progress) {
	this.progress = progress;
    }

    public String getError() {
	return error;
    }

    public void setError(String error) {
	this.error = error;
    }

    @Override
    public String toString() {
	return error != null? error :
		inProgressUpdate != null?
	    inProgressUpdate + ": " + Math.round((progress == null? 0f:progress)*100) + "%" :
	    lastUpdate != null? lastUpdate : "";
    }

    @Override
    public int compareTo(Object o) {
        if (o instanceof CartUpdateStatus) {
            CartUpdateStatus obj = (CartUpdateStatus) o;
            return macAddress.compareTo(obj.getMacAddress());
        }
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CartUpdateStatus) {
            CartUpdateStatus s = (CartUpdateStatus) obj;
            return macAddress.equals(s.getMacAddress());
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 31 * hash + (this.macAddress != null ? this.macAddress.hashCode() : 0);
        return hash;
    }

}
