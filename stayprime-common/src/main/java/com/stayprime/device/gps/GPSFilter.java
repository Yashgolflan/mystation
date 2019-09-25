/*
 * 
 */
package com.stayprime.device.gps;

import com.stayprime.geo.Coordinates;
import com.aeben.golfcourse.cart.PositionInfo;

/**
 * A GPS Filter that tries to give the best fix from a series of fixes.
 * It prefers newer fixes, has a distance threshold to keep a previous fix.
 * @author benjamin
 */
public class GPSFilter implements PositionInfo {
    private final ObservableGPSPosition gps;
    private final GPSPosition positions[];
    private GPSPosition filteredPosition;

    private int stackIndex = -1;
    private int index = -1;

    private float smallDistance = 1f;
    private float bigDistance = 10f;
    private float svsZero = 6f, svsFactor = 1f;
    private float hdopZero = 2f, hdopFactor = -2f;
    private float snrZero = 20f, snrFactor = 1f;
    private float bestFix = 10f;

    public GPSFilter(ObservableGPSPosition gps, int filterSize) {
	this.gps = gps;
	positions = new GPSPosition[filterSize];
	filteredPosition = new GPSPositionImpl();
	gps.addPositionObserver(new PositionObserver());
    }

    public Coordinates getCoordinates() {
	return filteredPosition.getCoordinates();
    }

    public boolean isPositionValid() {
	return filteredPosition.isPositionValid();
    }

    public boolean isPositionGood() {
        return gps.isPositionGood();
    }

    public float getHeading() {
	return filteredPosition.getHeading();
    }

    public float getSpeed() {
	return filteredPosition.getSpeed();
    }

    private class PositionObserver implements ObservableGPSPosition.Observer {
	public void positionUpdated(PositionInfo location, Coordinates oldLocation) {
	    GPSPositionImpl newPos = new GPSPositionImpl();
	    newPos.set(gps);

	    int lastStackIndex = stackIndex;
	    stackIndex = (stackIndex + 1) % positions.length;
	    positions[stackIndex] = newPos;

	    if(index == -1) //Initial position, set to current stackIndex
		index = stackIndex;
	    else if(isBestFix(positions[stackIndex]))
		index = stackIndex;
	    else if(isBetterFix(positions[stackIndex], positions[lastStackIndex]))
		index = stackIndex;
	    else if(!isSmallDistance(positions[lastStackIndex], positions[stackIndex]))
		index = stackIndex;
	    else {
		if(index == stackIndex) //Index was the last element, move fwd
		    index = (index + 1) % positions.length;

		for(int i = index, next_i, s = stackIndex; i != s;) {
		    next_i = (i+1) % positions.length;

		    //Absolute distance should be smaller than bigDistanceLimit
		    if(isBigDistance(positions[i], positions[stackIndex]))
			i = index = next_i;
		    //If the next fix is better, move index towards s
		    else if(isBetterFix(positions[next_i], positions[i]))
			i = index = next_i;
		    //While s is better, move index towards s
		    else if(isBetterFix(positions[s], positions[i]))
			i = index = next_i;
		    else
			i = next_i;
		}
	    }

	    filteredPosition = positions[index];
	}

	private float getFixQuality(GPSPosition p) {
	    return (p.getUsedSVs() - svsZero) * svsFactor + 
		    (p.getHDOP() - hdopZero) * hdopFactor + 
		    (p.getMeanSNR() - snrZero) * snrFactor;
	}

	private boolean isBestFix(GPSPosition p1) {
	    return p1.isPositionValid() && getFixQuality(p1) >= bestFix;
	}

	private boolean isSmallDistance(GPSPosition p0, GPSPosition p1) {
	    if(p0 == null)
		return false;
	    else if(p1 == null)
		return true;

	    if(p0.isPositionValid() == false)
		return false;
	    else if(p0.isPositionValid() == false)
		return true;

	    return p0.getCoordinates().metersTo(p1.getCoordinates()) < smallDistance;
	}

	private boolean isBigDistance(GPSPosition p0, GPSPosition p1) {
	    if(p0 == null)
		return true;
	    else if(p1 == null)
		return false;

	    if(p0.isPositionValid() == false)
		return true;
	    else if(p0.isPositionValid() == false)
		return false;

	    return p0.getCoordinates().metersTo(p1.getCoordinates()) > bigDistance;
	}

	private boolean isBetterFix(GPSPosition p1, GPSPosition p0) {
	    return isBetterFix(p1, p0, 0f);
	}

	private boolean isBetterFix(GPSPosition p1, GPSPosition p0, float threshold) {
	    if(p0 == null) //p0 is null: p1 is equal or better
		return true;
	    if(p1 == null) //p0 not null, p1 null: p0 is better
		return false;

	    if(!p0.isPositionValid()) //p0 not valid: p1 is equal or better
		return true;
	    if(!p1.isPositionValid()) //p0 is valid, p1 not valid: p0 is better
		return false;

	    //Both positions are valid
	    if(p1.getFixQuality() != p0.getFixQuality())
		return p1.getFixQuality() > p0.getFixQuality();

	    return getFixQuality(p1) > getFixQuality(p0) + threshold;
	}
    }

}
