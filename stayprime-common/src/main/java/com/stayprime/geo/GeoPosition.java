/*
 * 
 */
package com.stayprime.geo;

import java.awt.geom.Point2D;

/**
 * Describes the position and speed of an object in a bi-dimensional space.
 * @author benjamin
 */

public interface GeoPosition {
    /**
     * @return Coordinates of this position
     */
    public Point2D getPoint();

    /**
     * Heading of this moving position in degrees, measured from North
     * @return the heading angle
     */
    public float getBearing();
    
    /**
     * Speed of this moving position, measured in km/h
     * @return the speed
     */
    public float getSpeed();

    /**
     * Last time this position was updated.
     * @return Time in milliseconds when this position was last updated.
     */
    public long getLastReportedTime();
    
    public double distanceTo(Point2D point);

    public float bearingTo(Point2D point);
}
