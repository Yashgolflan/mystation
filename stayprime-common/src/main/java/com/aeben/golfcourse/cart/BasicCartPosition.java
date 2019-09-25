/*
 * 
 */

package com.aeben.golfcourse.cart;

import com.stayprime.geo.Coordinates;
import java.util.ArrayList;
import java.util.List;

/**
 * Provide a separate implementation for a cart number and it's position.
 * TODO: This class is unused and subject to review/merge/deletion.
 * If the PositionInfo is not an external source, there is no way to update it,
 * so it's actually only a placeholder for an ObservablePositionInfo and a cart
 * number.
 *  * @author benjamin
 */
public class BasicCartPosition implements CartPosition {
    private Integer unitId;
    private final PositionInfo positionInformation;
//    private final List<Observer> observers;

    public BasicCartPosition() {
	this(null, new ObservablePositionInfoImpl());
    }

    public BasicCartPosition(Integer unitId, ObservablePositionInfo positionInformation) {
	this.unitId = unitId;
	this.positionInformation = positionInformation;
//	observers = new ArrayList<Observer>();

//	positionInformation.addPositionObserver(new PositionInformationObserver());
    }

    public Integer getUnitId() {
	return unitId;
    }

    public void setUnitId(Integer unitId) {
	this.unitId = unitId;

//	for(CartPosition.Observer o: observers)
//	    o.unitIdChanged(this);
    }

    public PositionInfo getPositionInformation() {
	return positionInformation;
    }

//    public void addCartPositionObserver(Observer observer) {
//	observers.add(observer);
//    }
//
//    public void removeCartPositionObserver(Observer observer) {
//	observers.remove(observer);
//    }
//
//    //Only observes our own position information and reports it to our observers
//    private class PositionInformationObserver implements ObservablePositionInfo.Observer {
//	public void positionUpdated(PositionInfo location, Coordinates oldLocation) {
//	    for (Observer o : observers) {
//		o.positionChanged(BasicCartPosition.this, oldLocation);
//	    }
//	}
//    }

}
