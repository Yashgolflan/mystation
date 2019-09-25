/*
 * 
 */
package com.aeben.golfcourse.cart;

import com.stayprime.geo.Coordinates;

/**
 *
 * @author benjamin
 */
public interface PositionInfo {

    Coordinates getCoordinates();

    boolean isPositionValid();

    boolean isPositionGood();

    float getHeading();

    float getSpeed();

}
