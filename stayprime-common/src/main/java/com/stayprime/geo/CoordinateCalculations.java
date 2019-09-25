/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stayprime.geo;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;


/**
 *
 * @author benjamin
 */
public class CoordinateCalculations {

    public static final double R = 6378137.0;

    public static Point2D getOrthographicProjectionInMeters(Coordinates coord, Coordinates centerPoint) {
        double lat0 = Math.PI / 180 * centerPoint.latitude,
                lon0 = Math.PI / 180 * centerPoint.longitude,
                lat = Math.PI / 180 * coord.latitude,
                lon = Math.PI / 180 * coord.longitude,
                x = R * Math.cos(lat) * Math.sin(lon - lon0),
                y = R * (Math.cos(lat0) * Math.sin(lat) - Math.sin(lat0) * Math.cos(lat) * Math.cos(lon - lon0));

        return new Point2D.Double(x, y);
    }

    public static Point2D.Double getMercatorProjectionInMeters(Coordinates coord, Coordinates falseCenter) {
        return getMercatorProjectionInMeters(coord.latitude, coord.longitude, falseCenter);
    }

    public static Point2D.Double getMercatorProjectionInMeters(double latitude, double longitude, Coordinates falseCenter) {
        double toRad = Math.PI / 180, toDeg = 180 / Math.PI;

        double lat0 = falseCenter.latitude * toRad,
                //lon0 = falseCenter.longitude * toRad,
                scale0 = (Math.cos(lat0) * 2 * Math.PI * R) / (360),
                x0 = falseCenter.longitude,
                y0 = Math.log(Math.tan(lat0) + 1/Math.cos(lat0)) * toDeg;

        double lat = latitude * toRad,
                //lon = coord.longitude * toRad,
                //scale = (Math.cos(lat) * 2 * Math.PI * R) / (360),
                x = longitude,
                y = Math.log(Math.tan(lat) + 1/Math.cos(lat)) * toDeg;

        Point2D.Double pos = new Point2D.Double((x - x0)*scale0, (y - y0)*scale0);
        return pos;
    }


    public static Point2D.Double getXYRelativeToMapPixelCoordinates(
	    Coordinates coord, BasicMapImage map, Rectangle2D rect) {
	return getXY(coord, map, new Rectangle2D.Double(0, 0, rect.getWidth(), rect.getHeight()));
    }

    public static Point2D.Double getXY(Coordinates coord, BasicMapImage map, Rectangle2D rect) {
        return getXY(coord.latitude, coord.longitude, map, rect);
    }

    public static Point2D.Double getXY(double lat, double lon, BasicMapImage map, Rectangle2D rect) {
	double pixelWidth = rect.getWidth();
	double pixelHeight = rect.getHeight();

        Coordinates center = new Coordinates(
		(map.getTopLeft().latitude+map.getBottomRight().latitude)/2,
		(map.getTopLeft().longitude+map.getBottomRight().longitude)/2);

	Point2D topLeft = getMercatorProjectionInMeters(map.getTopLeft(), center);
	Point2D topRight = getMercatorProjectionInMeters(map.getTopRight(), center);
        Point2D bottomLeft = getMercatorProjectionInMeters(map.getBottomLeft(), center);
        Point2D bottomRight = getMercatorProjectionInMeters(map.getBottomRight(), center);
        Point2D point = getMercatorProjectionInMeters(lat, lon, center);
	
	point.setLocation(point.getX() - topLeft.getX(), point.getY() - topLeft.getY());
	topRight.setLocation(topRight.getX() - topLeft.getX(), topRight.getY() - topLeft.getY());
	bottomLeft.setLocation(bottomLeft.getX() - topLeft.getX(), bottomLeft.getY() - topLeft.getY());
	bottomRight.setLocation(bottomRight.getX() - topLeft.getX(), bottomRight.getY() - topLeft.getY());
	topLeft.setLocation(0, 0);

	double mapWidth = topLeft.distance(topRight);
	double mapHeight = topLeft.distance(bottomLeft);

	double x = (point.getX()*topRight.getX() + point.getY()*topRight.getY())/mapWidth;
	double y = (point.getX()*bottomLeft.getX() + point.getY()*bottomLeft.getY())/mapHeight;

	return new Point2D.Double(
		x/mapWidth*pixelWidth + rect.getX(),
		y/mapHeight*pixelHeight + rect.getY());
    }

    public static Coordinates getCoordinatesFromXYRelativeToMapPixel(
            Point2D pointPix, BasicMapImage map, Rectangle2D rect) {

	Coordinates topLeftCoord = map.getTopLeft();
	Coordinates bottomRightCoord = map.getBottomRight();
	double pixelWidth = rect.getWidth();
	double pixelHeight = rect.getHeight();

        Coordinates center = new Coordinates(
		(topLeftCoord.latitude+bottomRightCoord.latitude)/2,
		(topLeftCoord.longitude+bottomRightCoord.longitude)/2);

	Point2D topLeft = getMercatorProjectionInMeters(topLeftCoord, center);
	Point2D topRight = getMercatorProjectionInMeters(map.getTopRight(), center);
        Point2D bottomLeft = getMercatorProjectionInMeters(map.getBottomLeft(), center);

	topRight.setLocation(topRight.getX() - topLeft.getX(), topRight.getY() - topLeft.getY());
	bottomLeft.setLocation(bottomLeft.getX() - topLeft.getX(), bottomLeft.getY() - topLeft.getY());

	double w = pointPix.getX()/rect.getWidth();
	double h = pointPix.getY()/rect.getHeight();
	double x = w * topRight.getX() + h * bottomLeft.getX();
	double y = w * topRight.getY() + h * bottomLeft.getY();

	Point2D point = new Point2D.Double(topLeft.getX() + x, topLeft.getY() + y);
        //Coordinates coord = getInverseOrtographicProjectionFromMeters(point, topLeftCoordinate);
        Coordinates coord = getInverseMercatorProjectionFromMeters(point, center);

        return coord;

    }

    public static float getTopEdgeAngle(
            Coordinates topLeftCoordinate, Coordinates bottomRightCoordinate,
            double pixelWidth, double pixelHeight) {

        Coordinates center = new Coordinates((topLeftCoordinate.latitude+bottomRightCoordinate.latitude)/2, (topLeftCoordinate.longitude+bottomRightCoordinate.longitude)/2);

        //Point2D bottomRight = getOrthographicProjectionInMeters(bottomRightCoordinate, topLeftCoordinate);
        Point2D bottomRight = getMercatorProjectionInMeters(bottomRightCoordinate, center),
                topLeft = getMercatorProjectionInMeters(topLeftCoordinate, center);

        double topEdgeToMainDiagonalAngle = Math.atan2(pixelHeight, pixelWidth),
                mainDiagonalAngle = Math.atan2(bottomRight.getY()-topLeft.getY(), bottomRight.getX()-topLeft.getX()),
                topEdgeAngle = topEdgeToMainDiagonalAngle + mainDiagonalAngle;
        return (float) Math.toDegrees(topEdgeAngle);
    }

    /**
     * Calculates the angle in degrees of a line formed by two points.
     * The points in geographic coordinates are converted to Mercator projection
     * and the angle is calculated with atan2.
     * @param origin the fist point.
     * @param point the second point.
     * @return the angle in degrees that the two points form.
     */
    public static float getAngle(Coordinates origin, Coordinates point) {
        return (float) Math.toDegrees(getAngleRad(origin, point));
    }

    /**
     * Calculates the angle in radians of a line formed by two points.
     * The points in geographic coordinates are converted to Mercator projection
     * and the angle is calculated with atan2.
     * @param origin the fist point.
     * @param point the second point.
     * @return the angle in degrees that the two points form.
     */
    public static double getAngleRad(Coordinates origin, Coordinates point) {
        Point2D bottomRight = getMercatorProjectionInMeters(point, origin);
        return Math.atan2(bottomRight.getY(), bottomRight.getX());
    }

        public static double pitagoras(double x, double y) {
        return Math.sqrt(x * x + y * y);
    }

    public static double distanceInMeters(Coordinates coord1, Coordinates coord2) {
        return distanceInMeters(coord1.latitude, coord1.longitude, coord2.latitude, coord2.longitude);
    }

    public static double distanceInMeters(double lat1, double lon1, double lat2, double lon2) {
        double dLat = (lat2 - lat1) * (Math.PI / 180),
                dLon = (lon2 - lon1) * (Math.PI / 180),
                a = (Math.sin(dLat / 2) * Math.sin(dLat / 2))
                + (Math.cos(lat1 * (Math.PI / 180)) * Math.cos(lat2 * (Math.PI / 180)) * Math.sin(dLon / 2) * Math.sin(dLon / 2)),
                c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a)),
                d = R * c; // Distance in m

        return (d);
        /*Point2D p1 = getMercatorProjectionInMeters(coord1, coord1),
                p2 = getMercatorProjectionInMeters(coord2, coord1);
        return pitagoras(p1.getX() - p2.getX(), p1.getY() - p2.getY());*/
    }

    public static double distanceInMeters(Coordinates coord1, Coordinates coord2, Coordinates center) {
        Point2D p1 = getMercatorProjectionInMeters(coord1, center),
                p2 = getMercatorProjectionInMeters(coord2, center);
        return pitagoras(p1.getX() - p2.getX(), p1.getY() - p2.getY());
    }

    public static double fastDistanceInMeters(Coordinates p1, Coordinates p2) {
	return fastDistanceInMeters(p1.longitude, p1.latitude, p2.longitude, p2.latitude);
    }
    
    public static double fastDistanceInMeters(double p1lon, double p1lat, double p2lon, double p2lat) {
	double latCorrection = getLatitudeCorrection(p1lat, p2lat);

	return Math.toRadians(pitagoras((p2lon - p1lon)*latCorrection, p2lat - p1lat))*R;

    }

    public static Point2D fastProjection(Coordinates origin, Coordinates coord) {
	double latCorrection = getLatitudeCorrection(origin.latitude);
	return new Point2D.Double(Math.toRadians(coord.longitude - origin.longitude) * R,
		Math.toRadians(coord.latitude - origin.latitude) * R * latCorrection);
	//return Math.toRadians(pitagoras((p2.longitude - p1.longitude)*latCorrection, p2.latitude - p1.latitude))*R;

    }

    public static double getLatitudeCorrection(double p1lat, double p2lat) {
	return getLatitudeCorrection((p2lat - p1lat)/2);
    }
    
    public static double getLatitudeCorrection(Coordinates p1, Coordinates p2) {
	return getLatitudeCorrection((p2.latitude - p1.latitude)/2);
    }

    public static double getLatitudeCorrection(double centerLatitude) {
	return Math.cos(Math.toRadians(centerLatitude));
    }

    /*private static Coordinates getInverseOrtographicProjectionFromMeters(Point2D point, Coordinates centerPoint) {
        double lat0 = Math.PI / 180 * centerPoint.latitude,
                lon0 = Math.PI / 180 * centerPoint.longitude,
                p = pitagoras(point.getX(), point.getY()),
                c = Math.asin(p/R),
                lat = (Math.asin(Math.cos(c)*Math.sin(lat0)
                    + point.getY()*Math.sin(c)*Math.cos(lat0)/p)) * 180/Math.PI,
                lon = (lon0 + Math.atan2(point.getX()*Math.sin(c),
                        p*Math.cos(lat0)*Math.cos(c)
                        - point.getY()*Math.sin(lat0)*Math.sin(c))) * 180/Math.PI;
        return new Coordinates(lat, lon);

    }*/

    public static Coordinates getInverseMercatorProjectionFromMeters(Point2D point, Coordinates falseCenter) {
        double toRad = Math.PI / 180, toDeg = 180 / Math.PI;

        double lat0 = falseCenter.latitude * toRad,
                //lon0 = falseCenter.longitude * toRad,
                scale0 = (Math.cos(lat0) * 2 * Math.PI * R) / (360),
                x0 = falseCenter.longitude,
                y0 = Math.log(Math.tan(lat0) + 1/Math.cos(lat0)) * toDeg;

        double x = (point.getX()/scale0 + x0) * toRad,
                y = (point.getY()/scale0 + y0) * toRad,
                lon = x * toDeg,
                lat = Math.atan(Math.sinh(y))*toDeg;

        return new Coordinates(lat, lon);

    }

    public static Float getBearing(Coordinates orig, Coordinates dest) {
	    double lat1 = Math.toRadians(orig.latitude),
			    lon1 = Math.toRadians(orig.longitude),
			    lat2 = Math.toRadians(dest.latitude),
			    lon2 = Math.toRadians(dest.longitude);
	    double //dLat = (lat2-lat1),
			    dLon = (lon2-lon1);
	    double y = Math.sin(dLon) * Math.cos(lat2),
			    x = Math.cos(lat1)*Math.sin(lat2) - Math.sin(lat1)*Math.cos(lat2)*Math.cos(dLon);
	    float bearing = (float) Math.toDegrees(Math.atan2(y, x));
	    return bearing;
    }

    public static Float getFinalBearing(Coordinates orig, Coordinates dest) {
	    return (getBearing(dest, orig) + 180) % 360;
    }

    public static boolean shapeContains(List<Coordinates> s, Coordinates coord) {
        return shapeContains(s, coord.latitude, coord.longitude);
    }

    public static boolean shapeContains(List<Coordinates> s, double lat, double lon) {
    	int polySides = s.size();
    	int j = polySides-1;
    	boolean oddNodes = false;

    	for(int i = 0; i < polySides; i++) {
    		if (s.get(i).latitude<lat && s.get(j).latitude>=lat
    				||  s.get(j).latitude<lat && s.get(i).latitude>=lat) {
    			if (s.get(i).longitude+(lat-s.get(i).latitude)/(s.get(j).latitude-s.get(i).latitude)*(s.get(j).longitude-s.get(i).longitude)<lon) {
    				oddNodes=!oddNodes;
    			}
    		}
    		j=i;
    	}

    	return oddNodes;
    }

    public static boolean shapeContains(List<Point2D> s, Point point) {
    	int polySides = s.size();
    	int j = polySides-1;
    	boolean oddNodes = false;
        double x = point.getX();
        double y = point.getY();

    	for(int i = 0; i < polySides; i++) {
    		if (s.get(i).getY()<y && s.get(j).getY()>=y
    				||  s.get(j).getY()<y && s.get(i).getY()>=y) {
    			if (s.get(i).getX()+(y-s.get(i).getY())/(s.get(j).getY()-s.get(i).getY())*(s.get(j).getX()-s.get(i).getX())<x) {
    				oddNodes=!oddNodes;
    			}
    		}
    		j=i;
    	}

    	return oddNodes;
    }

}
