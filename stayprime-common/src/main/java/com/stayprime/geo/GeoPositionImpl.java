/*
 * 
 */
package com.stayprime.geo;

import com.stayprime.geo.GeoPosition;
//import com.stayprime.geo.util.GeoCalculations;
import java.awt.geom.Point2D;

/**
 *
 * @author benjamin
 */
public class GeoPositionImpl implements GeoPosition {
//    private transient GeoCalculations geoCalculations;
    
    private float bearing;
    private float speed;
    private Point2D point;
    private long lastReportedTime;

    public GeoPositionImpl() {
	this(null);
    }
    
    public GeoPositionImpl(Point2D point) {
	this(point, 0, 0);
    }
    
    public GeoPositionImpl(Point2D point, float heading, float speed) {
	this.point = point;
	this.bearing = heading;
	this.speed = speed;
	this.lastReportedTime = 0;
	
    }

    public void setPoint(Point2D point) {
	if(point == null)
	    this.point = null;
	else {
	    if(this.point == null)
		this.point = new Point2D.Double();

	    this.point.setLocation(point);
	}
    }

    @Override
    public Point2D getPoint() {
	return point;
    }

    public void setSpeed(float speed) {
	this.speed = speed;
    }
    
    @Override
    public float getSpeed() {
	return speed;
    }

    public void setBearing(float bearing) {
	this.bearing = bearing;
    }

    @Override
    public float getBearing() {
	return bearing;
    }

    @Override
    public long getLastReportedTime() {
	return lastReportedTime;
    }
    
    @Override
    public double distanceTo(Point2D pos) {
        return EarthGeoCalculations.getInstance().getDistance(getPoint(), pos);
    }

    @Override
    public float bearingTo(Point2D pos) {
	return EarthGeoCalculations.getInstance().getBearing(getPoint(), pos);
    }

    public void set(GeoPosition position) {
	setPoint(position.getPoint());
	setSpeed(position.getSpeed());
	setBearing(position.getBearing());
    }

}
