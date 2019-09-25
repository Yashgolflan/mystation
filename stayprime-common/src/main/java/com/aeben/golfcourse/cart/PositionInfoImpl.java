/*
 * 
 */
package com.aeben.golfcourse.cart;

import com.stayprime.geo.Coordinates;
import org.apache.commons.lang.ObjectUtils;

/**
 *
 * @author benjamin
 */
public class PositionInfoImpl implements PositionInfo {
    private boolean positionValid;
    private Coordinates coordinates;
    private Coordinates oldCoordinates;
    private float heading;
    private float speed;
    private boolean positionGood;

    public PositionInfoImpl() {
    }

    public Coordinates getCoordinates() {
	return coordinates;
    }

    public Coordinates getOldCoordinates() {
	return oldCoordinates;
    }

    public float getHeading() {
	return heading;
    }

    public float getSpeed() {
	return speed;
    }

    public boolean isPositionValid() {
	return positionValid;
    }

    public boolean isPositionGood() {
	return positionGood;
    }

    public void set(PositionInfo p) {
	set(p.getCoordinates(), p.isPositionValid(), p.isPositionGood(), p.getHeading(), p.getSpeed());
    }

    protected void set(Coordinates coordinates, boolean valid, boolean good, float heading, float speed) {
        positionGood = good;
	setPositionValid(valid);
	setCoordinates(coordinates);
	setHeading(heading);
	setSpeed(speed);
    }
    
    private boolean setCoordinates(Coordinates coordinates) {
	if(ObjectUtils.notEqual(this.coordinates, oldCoordinates)) {
	    if(this.coordinates != null) {
		if(oldCoordinates == null)
		    oldCoordinates = new Coordinates(this.coordinates);
		else
		    oldCoordinates.set(this.coordinates);
	    }
	    else
		oldCoordinates = null;
	}

	if(ObjectUtils.notEqual(coordinates, this.coordinates)) {
	    if(coordinates != null) {
		if(this.coordinates == null)
		    this.coordinates = new Coordinates(coordinates);
		else
		    this.coordinates.set(coordinates);
	    }
	    else
		this.coordinates = null;

	    return true;
	}

	return false;
    }

    private boolean setPositionValid(boolean positionValid) {
	if(this.positionValid != positionValid) {
	    this.positionValid = positionValid;
	    return true;
	}

	return false;
    }

    private boolean setHeading(Float heading) {
	if(ObjectUtils.notEqual(this.heading, heading)) {
	    this.heading = heading;
	    return true;
	}

	return false;
    }

    private boolean setSpeed(Float speed) {
	if(ObjectUtils.notEqual(this.speed, speed)) {
	    this.speed = speed;
	    
	    return true;
	}

	return false;
    }

}
