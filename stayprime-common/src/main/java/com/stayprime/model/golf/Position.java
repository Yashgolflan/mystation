/*
 * 
 */
package com.stayprime.model.golf;

import java.text.DecimalFormat;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Transient;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

/**
 *
 * @author benjamin
 */
@Embeddable
public class Position {
    public static final DecimalFormat format = new DecimalFormat("#.######");

    @Transient
    private double x;

    @Transient
    private double y;

    public Position() {
    }

    public Position(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Position(String position) {
        setPosition(position);
    }

    @Column(name = "position")
    @Access(AccessType.PROPERTY)
    String getPosition() {
        return toString();
    }

    void setPosition(String position) {
        String coords[] = StringUtils.split(position, ",");
        if (coords != null && coords.length == 2) {
            this.x = NumberUtils.toDouble(coords[0]);
            this.y = NumberUtils.toDouble(coords[1]);
        }
    }

    public void setLocation(Position p) {
        this.x = p.getX();
        this.y = p.getY();
    }

    public void setLocation(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

//    public void setLon(double longitude) {
//        this.x = longitude;
//    }
    public double getY() {
        return y;
    }

    public boolean isValid() {
        return Double.isNaN(x) == false && Double.isInfinite(x) == false
                && Double.isNaN(y) == false && Double.isInfinite(y) == false;
    }

//    public void setLat(double latitude) {
//        this.y = latitude;
//    }
    @Override
    public String toString() {
        return Double.toString(x) + "," + Double.toString(y);
    }

    public static Position get(Double lon, Double lat) {
        boolean valid = lon != null && lat != null;
        if (valid) {
            return new Position(lat, lon);
        }
        else {
            return null;
        }
    }

}
