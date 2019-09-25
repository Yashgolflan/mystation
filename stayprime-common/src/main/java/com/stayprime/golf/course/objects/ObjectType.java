/*
 * 
 */

package com.stayprime.golf.course.objects;

import com.stayprime.golf.objects.GeomType;
import java.awt.Color;

/**
 *
 * @author benjamin
 */
public enum ObjectType {
    UNKNOWN(0, "Unknown"),
    HAZARD(1, "Hazard"),
    CARTH_PATH(2, "Cart Path", GeomType.POLYLINE, Color.darkGray),
    HOLE_ZONE(3, "Hole Zone", GeomType.POLY, Color.white),
    ACTION_ZONE(4, "Action Zone", GeomType.POLY, Color.orange),
    GOLFCART(6, "Golf Cart"),
    PINFLAG(7, "Pin Flag"),
    CLUBHOUSE_ZONE(8, "Clubhouse Zone", GeomType.POLY, Color.yellow),
    FRONT_GREEN(9, "Front of Green"),
    MIDDLE_GREEN(10, "Middle of Green"),
    BACK_GREEN(11, "Back of Green"),
    GREEN(12, "Green", GeomType.POLY, Color.green),
    GOLF_CLUB(13, "Golf Club"),
    GOLF_COURSE(14, "Golf Course"),
    GOLF_HOLE(15, "Golf Hole"),
    TEE_BOX(16, "Tee Box"),
    GREEN_GRID(17, "Green Grid", GeomType.POLYLINE, Color.white),
    CART_TRACK(18, "Cart Track", GeomType.POLYLINE, Color.blue),
    AD_ZONE(19, "Ad Zone", GeomType.POLY, Color.blue),
    CART_BARN(20, "Cart Barn", GeomType.POLY, Color.yellow.darker()),
    RESTRICTED_ZONE(21, "Restricted Zone", GeomType.POLY, Color.red),
    REFERENCE(22, "Reference Point"),
    PIN_LOCATIONS(23, "Pin Locations", GeomType.POINTLIST, Color.yellow),
    HOLE_OUTLINE(24, "Hole Outline", GeomType.POLY, Color.green),
    APPROACH_LINE(25, "Approach line", GeomType.POLYLINE, Color.white);
    public static final int maxTypeCount = 50;

    private final int key;
    private final String name;
    private final GeomType geomType;
    private final Color defaultColor;

    private ObjectType(int key, String name) {
	this(key, name, GeomType.POINT);
    }

    private ObjectType(int key, String name, GeomType geomType) {
        this(key, name, geomType, Color.white);
    }

    private ObjectType(int key, String name, GeomType geomType, Color c) {
	this.key = key;
	this.name = name;
        this.geomType = geomType;
        this.defaultColor = c;
    }

    public static ObjectType getType(int key) {
	for (ObjectType type : values()) {
	    if (type.key == key) {
		return type;
	    }
	}
	return UNKNOWN;
    }

    public int getId() {
	return key;
    }

    public String getName() {
        return name();
    }

    public String getDescription() {
	return name;
    }

    public GeomType getGeomType() {
        return geomType;
    }

    public Color getDefaultColor() {
        return defaultColor;
    }

    @Override
    public String toString() {
	return name;
    }

    public boolean equals(Integer key) {
	return key == null ? false : key.intValue() == this.key;
    }
}

