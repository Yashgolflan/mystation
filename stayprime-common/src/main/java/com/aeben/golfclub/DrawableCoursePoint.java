/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.aeben.golfclub;

import com.aeben.golfclub.GolfCourseObject.ObjectType;
import com.aeben.elementos.mapview.BasicDrawablePointObject;
import com.stayprime.geo.Coordinates;

/**
 *
 * @author benjamin
 */
public class DrawableCoursePoint extends BasicDrawablePointObject {
    public String name;
    public Integer id;

    public ObjectType type = ObjectType.UNKNOWN;

    public DrawableCoursePoint(String name, Integer id, GolfCourseObject parentObject) {
        this(name, id, parentObject, ObjectType.UNKNOWN, null);
    }

    public DrawableCoursePoint(String name, Integer id, GolfCourseObject parentObject,
            ObjectType type, Coordinates coordinates) {
        super(coordinates);
        this.name = name;
        this.id = id;
        this.type = type;
        this.coordinates = coordinates;
        setParent(parentObject);
    }

    public String getName() {
        return name;
    }

    public Integer getId() {
        return id;
    }

    public ObjectType getType() {
        return type;
    }

    @Override
    public String toString() {
        return (type == null? "" : type.name + ": ") + name;
    }

    @Override
    public DrawableCoursePoint clone() {
        DrawableCoursePoint point = new DrawableCoursePoint(name, id, getParentObject(), type,
                coordinates == null? null : coordinates.clone());
        point.lastDrawnShape = lastDrawnShape;
        return point;
    }
}
