package com.aeben.golfcourse.util;

/**
 *
 * @author benjamin
 */
public final class Yards extends DistanceUnits {

    Yards(int key) {
        super(key);
    }

    public int getKey() {
        return key;
    }

    public String getName() {
	return "Yards";
    }

    public String getShortName() {
	return "yd";
    }

    public double convertFrom(Yards units, double distance) {
	return distance;
    }

    public double convertFrom(Meters units, double distance) {
	return distance / 0.9144;
    }

}
