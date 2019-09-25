/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.aeben.golfclub;

import com.aeben.elementos.mapview.DrawableObject;
import java.awt.Shape;
import java.util.Set;

/**
 *
 * @author benjamin
 */
public class CartStatus extends CartLocation implements DrawableObject {
    public Set<GolfCourseObject> hittingObjects;
    public int playingHole;
    public int showingHole;
    public GolfCourseObject nearestPath;
    public Double distanceToPath;
    public boolean outOfCartPath = false;
    public boolean onActionZone = false;
    public boolean onClubhouseZone = false;
    //public boolean communicationLost = false;
    //public Date paceOfPlayLimit;
    public Integer paceOfPlaySeconds;
    public Integer mode;

    private Float batteryLevel;

    public transient Shape lastDrawnShape;

    public static final int STATUS_ACTIVE = 0x01;
    public static final int STATUS_ACTIONZONE_HIT = 0x02;
    public static final int STATUS_CLUBZONE_HIT = 0x04;
    public static final int STATUS_ERROR_DETECTED = 0x08;
    public static final int STATUS_OUT_OF_CARTPATH = 0x10;

    public CartStatus() {
        this(null);
    }

    public CartStatus(Integer cartNumber) {
        this.cartNumber = cartNumber;
    }

    @Override
    public CartStatus clone() {
        CartStatus cartStatus = new CartStatus(cartNumber);
        cartStatus.address = address;
        cartStatus.location = location == null? null : location.clone();
        cartStatus.heading = heading;
        cartStatus.speed = speed;
        cartStatus.hittingObjects = hittingObjects;
        cartStatus.playingHole = playingHole;
        cartStatus.showingHole = showingHole;
        cartStatus.nearestPath = nearestPath;
        cartStatus.distanceToPath = distanceToPath;
        cartStatus.outOfCartPath = outOfCartPath;
        cartStatus.onActionZone = onActionZone;
        cartStatus.onClubhouseZone = onClubhouseZone;
        //status.communicationLost = communicationLost;
        //paceOfPlayLimit;
        cartStatus.paceOfPlaySeconds = paceOfPlaySeconds;
        cartStatus.mode = mode;
        cartStatus.statusLastUpdated = statusLastUpdated;
        cartStatus.lastDrawnShape = lastDrawnShape;
        cartStatus.setBatteryLevel(batteryLevel);
        return cartStatus;
    }

    public Shape getLastDrawnShape() {
        return lastDrawnShape;
    }

    public void setLastDrawnShape(Shape s) {
        lastDrawnShape = s;
    }

    public void setBatteryLevel(Float batteryLevel) {
        this.batteryLevel = batteryLevel;
    }

    public Float getBatteryLevel() {
        return batteryLevel;
    }

}