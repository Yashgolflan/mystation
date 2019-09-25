/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.aeben.golfclub;

import com.stayprime.geo.Coordinates;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author benjamin
 */
public class CartTrack extends DrawableCourseShape {
    public List<CartLocation> trackPoints;
    public boolean directionForward = true;

    public CartTrack() {
        this(null, true);
    }

    public CartTrack(List<CartLocation> trackPoints, boolean directionForward) {
	this(null, null, null, trackPoints, directionForward);
        this.trackPoints = trackPoints;
	this.directionForward = directionForward;
    }

    public CartTrack(String name, Integer id, GolfCourseObject parentObject, float strokeWidth, Color strokeColor, Color fillColor, List<CartLocation> trackPoints, boolean directionForward) {
	super(name, id, parentObject, ObjectType.CART_TRACK, null, false, strokeWidth, strokeColor, fillColor);
	setTrackPoints(trackPoints);
	this.directionForward = directionForward;
    }

    public CartTrack(String name, Integer id, GolfCourseObject parentObject, List<CartLocation> trackPoints, boolean directionForward) {
	this(name, id, parentObject, 2f, Color.blue, Color.white, trackPoints, directionForward);
	this.trackPoints = trackPoints;
	this.directionForward = directionForward;
    }

    public List<CartLocation> getTrackPoints() {
        return trackPoints;
    }

    public final void setTrackPoints(List<CartLocation> trackPoints) {
	this.trackPoints = trackPoints;

	List<Coordinates> coords = new ArrayList<Coordinates>(trackPoints.size());
	for(CartLocation l: trackPoints) {
	    coords.add(l.location);
	}
	this.shapeCoordinates = coords;
    }

    @Override
    public DrawableCourseShape clone() {
        List<CartLocation> points = null;
        if(trackPoints != null) {
	    points = new ArrayList<CartLocation>(trackPoints);
//            coords = new ArrayList<CartLocation>(trackPoints.size());
//            for(Coordinates c: shapeCoordinates)
//                coords.add(c.clone());
        }
        CartTrack shape = new CartTrack(name, id, getParentObject(), strokeWidth,
		strokeColor, fillColor, points, directionForward);
        shape.lastDrawnShape = lastDrawnShape;
        return shape;
    }

}
