/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.aeben.golfclub;

import com.stayprime.util.gson.Exclude;

/**
 *
 * @author benjamin
 */
public class GolfCourseObject {
    @Exclude
    private GolfCourseObject parent;

    public GolfCourseObject() {
    }

    public String getName() {
        return null;
    }

    public Integer getId() {
        return null;
    }

    public ObjectType getType() {
        return ObjectType.UNKNOWN;
    }

    public GolfCourseObject getParentObject() {
        return parent;
    }

    @Override
    public GolfCourseObject clone() {
        return null;
    }

    public void setParent(GolfCourseObject parent) {
        this.parent = parent;
    }

    public enum ObjectType {
        UNKNOWN(0, "Unknown", true, true, false),
        HAZARD(1, "Hazard", true, true, false),
        CARTH_PATH(2, "Cart Path", false, true, true),
        HOLE_ZONE(3, "Hole Zone", false, true, false),
        ACTION_ZONE(4, "Action Zone", true, true, false),
        GOLFCART(6, "Golf Cart", true, false, false),
        PINFLAG(7, "Pin Flag", false, false, false),
        CLUBHOUSE_ZONE(8, "Clubhouse Zone", true, true, false),
        FRONT_GREEN(9, "Front of Green", false, true, true),
        MIDDLE_GREEN(10, "Middle of Green", false, true, true),
        BACK_GREEN(11, "Back of Green", false, true, true),
        GREEN(12, "Green", false, true, true),
        GOLF_CLUB(13, "Golf Club", false, false, false),
        GOLF_COURSE(14, "Golf Course", false, false, false),
        GOLF_HOLE(15, "Golf Hole", false, false, false),
        TEE_BOX(16, "Tee Box", false, true, false),
        GREEN_GRID(17, "Green Grid", false, false, false),
        CART_TRACK(18, "Cart Track", false, false, false),
        AD_ZONE(19, "Ad Zone", false, true, false),
        CART_BARN(20, "Cart Barn", true, false, false),
	RESTRICTED_ZONE(21, "Restricted Zone", true, false, false),
        REFERENCE(22, "Reference Point", false, true, true), 
	PIN_LOCATIONS(23, "Pin Locations", false, false, false),
	HOLE_OUTLINE(24, "Hole Outline", false, false, false),
	APPROACH_LINE(25, "Approach line", false, false, false);
        public final int key;
        public final String name;
        public final boolean courseWide, holeWide, holeUnique;

        private ObjectType(int key, String name, boolean courseWide, boolean holeWide, boolean holeUnique) {
            this.key = key;
            this.name = name;
            this.courseWide = courseWide;
            this.holeWide = holeWide;
            this.holeUnique = holeUnique;
        }

        public static ObjectType getType(int key) {
            for (ObjectType type : values()) {
                if (type.key == key) {
                    return type;
                }
            }
            return null;
        }

        @Override
        public String toString() {
            return name;
        }

        public boolean equals(Integer key) {
            return key == null ? false : key.intValue() == this.key;
        }
    };

}
