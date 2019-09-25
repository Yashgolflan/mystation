/*
 * 
 */
package com.stayprime.geo;

import java.awt.geom.Point2D;

/**
 *
 * @author benjamin
 */
public interface CoordConverter {
    public Point2D toPoint(Coordinates c);
    public Coordinates toLatLong(Point2D p);
}
