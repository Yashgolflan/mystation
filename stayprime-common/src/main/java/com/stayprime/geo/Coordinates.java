/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.stayprime.geo;

import java.awt.geom.Point2D;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

/**
 *
 * @author benjamin
 */
public class Coordinates {
    public static final DecimalFormat format = new DecimalFormat("#.######");

    public double latitude;
    public double longitude;

    public Coordinates() {
	this(0, 0);
    }

    public Coordinates(double lat, double lon) {
        this.latitude = lat;
        this.longitude = lon;
    }

    public Coordinates(String coordinates) {
        String coords[] = StringUtils.split(coordinates, ",");
        if (coords != null && coords.length == 2) {
            this.longitude = NumberUtils.toDouble(coords[0]);
            this.latitude = NumberUtils.toDouble(coords[1]);
        }
    }

    public Coordinates(Coordinates coordinates) {
	this(coordinates.latitude, coordinates.longitude);
    }

    public void set(Coordinates coordinates) {
	latitude = coordinates.latitude;
	longitude = coordinates.longitude;
    }

    public void set(double lat, double lon) {
	latitude = lat;
	longitude = lon;
    }

    public boolean isValid() {
        return isValid(latitude, longitude);
    }

    public static boolean isValid(Double lat, Double lon) {
        return isValid(lat) && isValid(lon);
    }

    public static boolean isValid(Double d) {
        return d != null && Double.isInfinite(d) == false && Double.isNaN(d) == false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + (int) (Double.doubleToLongBits(this.latitude) ^ (Double.doubleToLongBits(this.latitude) >>> 32));
        hash = 31 * hash + (int) (Double.doubleToLongBits(this.longitude) ^ (Double.doubleToLongBits(this.longitude) >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof Coordinates) {
            Coordinates coord = (Coordinates) o;
            return coord.latitude==latitude && coord.longitude==longitude;
        }
        return false;
    }

    public Coordinates clone() {
        return new Coordinates(latitude, longitude);
    }

    @Override
    public String toString() {
        return Double.toString(longitude)+","+Double.toString(latitude);
    }

    public String toReadableString() {
        return format.format(Math.abs(latitude)) + (latitude > 0? "N":"S") + ", "
                + format.format(Math.abs(longitude)) + (longitude > 0? "E":"W");
    }

    public double metersTo(Coordinates coordinates) {
	return CoordinateCalculations.distanceInMeters(this, coordinates);
    }

    public double metersTo(double lat, double lon) {
	return CoordinateCalculations.distanceInMeters(latitude, longitude, lat, lon);
    }

    public static Coordinates fromString(String string) {
        String coords[] = StringUtils.split(string, ",");
        if (coords != null && coords.length == 2) {
            return new Coordinates(NumberUtils.toDouble(coords[1]), NumberUtils.toDouble(coords[0]));
        }
        return null;
    }

    public static List<Coordinates> listFromString(String geom) {
        try {
            List<Coordinates> sh = new ArrayList<Coordinates>();
            String[] coords = geom.split(";");
            for (String c: coords) {
                sh.add(new Coordinates(c.trim()));
            }
            return sh;
        }
        catch (Exception ex) {
            throw new IllegalArgumentException("Error parsing coords: " + ex,  ex);
        }
    }

    public static String listToString(List<Coordinates> pinLocations) {
        return StringUtils.join(pinLocations, ";");
    }

}
