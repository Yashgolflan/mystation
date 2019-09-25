/*
 * 
 */
package com.stayprime.geo;

import java.awt.geom.Point2D;
import java.util.List;

/**
 *
 * @author benjamin
 */
public interface GeoPath {
    public List<Point2D> getPoints();
    public boolean isClosed();
    public Float getWidth();
    public boolean contains(Point2D p);
}
