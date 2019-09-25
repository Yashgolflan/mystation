/*
 * 
 */
package com.stayprime.device.gps;

import com.aeben.golfcourse.cart.PositionInfo;

/**
 *
 * @author benjamin
 */
public interface GPSPosition extends PositionInfo {

//    Coordinates getCoordinates();
//
//    boolean isPositionValid();
//
//    float getHeading();
//
//    float getSpeed();

    int getFixQuality();

    float getHDOP();

    float getMeanSNR();

    long getTime();

    int getTotalSVs();

    int getUsedSVs();

    public String getStatusLine();
    
}
