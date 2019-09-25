package com.aeben.golfcourse.util;

/**
 *
 * @author benjamin
 */
public final class Meters extends DistanceUnits {

    Meters(int key) {
        super(key);
    }

    public String getName() {
	return "Meters";
    }

    public String getShortName() {
	return "m";
    }

    public double convertFrom(Yards units, double distance) {
	return distance * 0.9144;
    }

    public double convertFrom(Meters units, double distance) {
	return distance;
    }

}
