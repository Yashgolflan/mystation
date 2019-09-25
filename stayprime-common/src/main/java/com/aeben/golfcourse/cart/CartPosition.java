/*
 * 
 */

package com.aeben.golfcourse.cart;

import com.stayprime.geo.Coordinates;

/**
 *
 * @author benjamin
 */
public interface CartPosition {
    public Integer getUnitId();
    public PositionInfo getPositionInformation();

//    public void addCartPositionObserver(Observer observer);
//    public void removeCartPositionObserver(Observer observer);
//
//    public interface Observer {
//	public void unitIdChanged(CartPosition cartPosition);
//	public void positionChanged(CartPosition cartPosition, Coordinates oldLocation);
//    }
}
