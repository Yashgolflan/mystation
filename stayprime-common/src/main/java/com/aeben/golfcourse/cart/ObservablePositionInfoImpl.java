/*
 * 
 */

package com.aeben.golfcourse.cart;

import com.stayprime.geo.Coordinates;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author benjamin
 */
public class ObservablePositionInfoImpl extends PositionInfoImpl implements ObservablePositionInfo {
    private List<Observer> observers;

    public ObservablePositionInfoImpl() {
	observers = new ArrayList<Observer>();
    }

    public void addPositionObserver(Observer observer) {
	observers.add(observer);
    }

    public void removePositionObserver(Observer observer) {
	observers.remove(observer);
    }

    @Override
    protected void set(Coordinates coordinates, boolean valid, boolean good, float heading, float speed) {
	super.set(coordinates, valid, good, heading, speed);
	notifyPositionInformationChanged();
    }

    private void notifyPositionInformationChanged() {
	for(ObservablePositionInfo.Observer o: observers)
	    o.positionUpdated(this, getOldCoordinates());
    }

}