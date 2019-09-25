/*
 * 
 */

package com.aeben.elementos.mapview;

import com.stayprime.geo.Coordinates;
import java.awt.geom.Point2D;

/**
 *
 * @author benjamin
 */
public interface Projection {
    public Point2D project(Coordinates coord);
    public Coordinates projectInverse(Point2D point);
    public double getAngle();
    public double getScale();
}
