/*
 * 
 */

package com.aeben.golfcourse.dashboard;

import com.aeben.elementos.mapview.MapProjection;
import com.stayprime.geo.BasicMapImage;
import com.stayprime.geo.CoordinateCalculations;
import com.stayprime.geo.Coordinates;
import java.awt.geom.Point2D;

/**
 *
 * @author benjamin
 */
public class BasicMercatorProjection implements MapProjection {
    private BasicMapImage map;
    private Coordinates center;

    public BasicMercatorProjection() {
	this(null);
    }

    public BasicMercatorProjection(BasicMapImage mapImage) {
	this.map = mapImage;
	setMap();
    }

    public void setMap(BasicMapImage map) {
	this.map = map;
	setMap();
    }

    private void setMap() {
	if(map != null) {
	    Coordinates topLeft = map.getTopLeft();
	    Coordinates bottomRight = map.getBottomRight();

	    center = new Coordinates(
		    (topLeft.latitude + bottomRight.latitude)/2,
		    (topLeft.longitude + bottomRight.longitude)/2);
	}
    }

    public Point2D project(Coordinates coord) {
	return CoordinateCalculations.getMercatorProjectionInMeters(coord, center);
    }

    public Coordinates projectInverse(Point2D point) {
	return CoordinateCalculations.getInverseMercatorProjectionFromMeters(point, center);
    }

    public double getScale() {
	return 1;
    }

    public double getAngle() {
	return 0;
    }

    public BasicMapImage getMap() {
	return map;
    }

}
