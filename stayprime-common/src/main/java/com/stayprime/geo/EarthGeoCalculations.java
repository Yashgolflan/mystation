/*
 * 
 */
package com.stayprime.geo;

import com.stayprime.model.golf.Position;
import java.awt.geom.Point2D;

/**
 *
 * @author benjamin
 */
public class EarthGeoCalculations {
    private static EarthGeoCalculations instance;
    public static final double R = 6378137.0;
    
    private EarthGeoCalculations() {}
    
    public static EarthGeoCalculations getInstance() {
	if(instance == null)
	    instance = new EarthGeoCalculations();
	
	return instance;
    }

    public double getDistance(Point2D p1, Point2D p2) {
	if (p1 == null || p2 == null) {
	    return Double.NaN;
        }
	
	return getDistance(p1.getX(), p1.getY(), p2.getX(), p2.getY());
    }

    public double getDistance(Position p1, Position p2) {
	if (p1 == null || p2 == null) {
	    return Double.NaN;
        }
	
	return getDistance(p1.getX(), p1.getY(), p2.getX(), p2.getY());
    }

    public double getDistance(double x1, double y1, double x2, double y2) {
	double dLat = (y2 - y1) * (Math.PI / 180);
	double dLon = (x2 - x1) * (Math.PI / 180);
	double a = (Math.sin(dLat / 2) * Math.sin(dLat / 2)) + 
		(Math.cos(y1 * (Math.PI / 180)) * 
		Math.cos(y2 * (Math.PI / 180)) * 
		Math.sin(dLon / 2) * Math.sin(dLon / 2));
	double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
	double d = R * c; // Distance in m
	
	return d;
    }

    public float getBearing(Point2D p1, Point2D p2) {
	if(p1 == null || p2 == null)
	    return Float.NaN;

	double lat1 = Math.toRadians(p1.getY()),
		lon1 = Math.toRadians(p1.getX()),
		lat2 = Math.toRadians(p2.getY()),
		lon2 = Math.toRadians(p2.getX());
	
	double dLon = (lon2-lon1);
	double y = Math.sin(dLon) * Math.cos(lat2);
	double x = Math.cos(lat1) * Math.sin(lat2) 
		- Math.sin(lat1) * Math.cos(lat2)*Math.cos(dLon);
	float bearing = (float) Math.toDegrees(Math.atan2(y, x));

	return bearing;
    }
    
}
