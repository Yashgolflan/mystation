/*
 * 
 */
package com.stayprime.model.golf;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author benjamin
 */
public class HoleCartList {
    private int holeNumber;
    private final List<GolfCart> carts;
    private long timestamp;
    private int timeout;

    public HoleCartList() {
	holeNumber = 0;
	carts = new ArrayList<GolfCart>();
	timestamp = 0;
	timeout = 0;
    }

    public void setHoleNumber(int holeNumber) {
	this.holeNumber = holeNumber;
    }

    public int getHoleNumber() {
	return holeNumber;
    }

    public void setTimestamp(long timestamp) {
	this.timestamp = timestamp;
    }

    public long getTimestamp() {
	return timestamp;
    }

    public void setTimeout(int expiry) {
	this.timeout = expiry;
    }

    public int getTimeout() {
	return timeout;
    }

    public void setCarts(List<GolfCart> carts) {
	this.carts.clear();
	this.carts.addAll(carts);
    }

    public List<GolfCart> getCarts() {
	return carts;
    }

    public void set(HoleCartList list) {
	setCarts(list.getCarts());
	setHoleNumber(list.getHoleNumber());
	setTimestamp(list.getTimestamp());
	setTimeout(list.getTimeout());
    }

    @Override
    public String toString() {
	return "Hole " + holeNumber + " {" + StringUtils.join(carts, ",") + "}";
    }

}
