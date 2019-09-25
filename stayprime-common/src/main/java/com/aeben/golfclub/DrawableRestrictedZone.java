/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.aeben.golfclub;

import com.stayprime.geo.Coordinates;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author benjamin
 */
public class DrawableRestrictedZone extends DrawableCourseShape {
    public String message;

    public DrawableRestrictedZone(String name, Integer id, GolfCourseObject parentObject, ObjectType type, List<Coordinates> shapeCoordinates, boolean closedShape, float strokeWidth, Color strokeColor, Color fillColor, String message) {
        super(name, id, parentObject, type, shapeCoordinates, closedShape, strokeWidth, strokeColor, fillColor);
        this.message = message;
    }

    public DrawableRestrictedZone(String name, Integer id, GolfCourseObject parentObject) {
        super(name, id, parentObject);
    }

    @Override
    public DrawableRestrictedZone clone() {
        List<Coordinates> coords = null;
        if(shapeCoordinates != null) {
            coords = new ArrayList<Coordinates>(shapeCoordinates.size());
            for(Coordinates c: shapeCoordinates)
                coords.add(c.clone());
        }
        DrawableRestrictedZone zone = new DrawableRestrictedZone(name, id,
                getParentObject(), type, coords, closedShape,
                strokeWidth, strokeColor, fillColor, message);
        zone.lastDrawnShape = lastDrawnShape;
        return zone;
    }

}
