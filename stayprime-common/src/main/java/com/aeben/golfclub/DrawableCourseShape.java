/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.aeben.golfclub;

import com.aeben.elementos.mapview.BasicDrawableShapeObject;
import com.stayprime.geo.Coordinates;
import com.google.gson.annotations.Expose;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author benjamin
 */
public class DrawableCourseShape extends BasicDrawableShapeObject {
    public String name;
    public Integer id;
    public ObjectType type = ObjectType.UNKNOWN;

    public DrawableCourseShape(String name, Integer id, GolfCourseObject parentObject) {
        this.name = name;
        this.id = id;
        setParent(parentObject);
    }
    
    public DrawableCourseShape(String name, Integer id, GolfCourseObject parentObject,
            ObjectType type, List<Coordinates> shapeCoordinates, boolean closedShape,
            float strokeWidth, Color strokeColor, Color fillColor) {
        super(shapeCoordinates, closedShape, strokeWidth, strokeColor, fillColor);
        this.name = name;
        this.id = id;
        setParent(parentObject);
        this.type = type;
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

    public String toString() {
        return (type == null? "" : type.name + ": ") + name;
    }

    public DrawableCourseShape clone() {
        List<Coordinates> coords = null;
        if(shapeCoordinates != null) {
            coords = new ArrayList<Coordinates>(shapeCoordinates.size());
            for(Coordinates c: shapeCoordinates)
                coords.add(c.clone());
        }
        DrawableCourseShape shape = new DrawableCourseShape(name, id,
                getParentObject(), type, coords, closedShape,
                strokeWidth, strokeColor, fillColor);
        shape.lastDrawnShape = lastDrawnShape;
        return shape;
    }
}
