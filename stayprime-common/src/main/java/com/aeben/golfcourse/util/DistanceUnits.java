/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.aeben.golfcourse.util;

/**
 *
 * @author benjamin
 */
public abstract class DistanceUnits {
    public int key;
    public static final Yards Yards = new Yards(0);
    public static final Meters Meters = new Meters(1);

    public DistanceUnits(int key) {
        this.key = key;
    }

    public abstract String getName();
    public abstract String getShortName();
    public abstract double convertFrom(Meters units, double distance);
    public abstract double convertFrom(Yards units, double distance);

    public static DistanceUnits get(int key) {
        if(key == Yards.key)
            return Yards;
        else if(key == Meters.key)
            return Meters;
        else
            throw new IllegalArgumentException("Invalid Units key");
    }

    public static DistanceUnits get(String name) {
        if("Yards".equalsIgnoreCase(name) || "yd".equalsIgnoreCase(name))
            return Yards;
        else if("Meters".equalsIgnoreCase(name) || "m".equalsIgnoreCase(name))
            return Meters;
        else
            throw new IllegalArgumentException("Invalid Units name");
    }

}
