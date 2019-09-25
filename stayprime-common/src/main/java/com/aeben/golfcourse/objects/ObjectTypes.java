/*
 * 
 */

package com.aeben.golfcourse.objects;

import com.aeben.golfcourse.elements.ObjectType;

/**
 *
 * @author benjamin
 */
public enum ObjectTypes implements ObjectType {
    UNKNOWN(0, "Unknown"),
    HAZARD(1, "Hazard"),
    CARTH_PATH(2, "Cart Path"),
    HOLE_ZONE(3, "Hole Zone"),
    ACTION_ZONE(4, "Action Zone"),
    GOLFCART(6, "Golf Cart"),
    PINFLAG(7, "Pin Flag"),
    CLUBHOUSE_ZONE(8, "Clubhouse Zone"),
    FRONT_GREEN(9, "Front of Green"),
    MIDDLE_GREEN(10, "Middle of Green"),
    BACK_GREEN(11, "Back of Green"),
    GREEN(12, "Green"),
    GOLF_CLUB(13, "Golf Club"),
    GOLF_COURSE(14, "Golf Course"),
    GOLF_HOLE(15, "Golf Hole"),
    TEE_BOX(16, "Tee Box"),
    GREEN_GRID(17, "Green Grid"),
    CART_TRACK(18, "Cart Track"),
    AD_ZONE(19, "Ad Zone"),
    CART_BARN(20, "Cart Barn"),
    RESTRICTED_ZONE(21, "Restricted Zone"),
    REFERENCE(22, "Reference Point"),
    PIN_LOCATIONS(23, "Pin Locations"),
    HOLE_OUTLINE(24, "Hole Outline"),
    APPROACH_LINE(25, "Approach line");

    private final int key;
    private final String name;

    private ObjectTypes(int key, String name) {
	this.key = key;
	this.name = name;
    }

    public static ObjectTypes getType(int key) {
	for (ObjectTypes type : values()) {
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
	return name;
    }

    @Override
    public String toString() {
	return name;
    }

    public boolean equals(Integer key) {
	return key == null ? false : key.intValue() == this.key;
    }
}

