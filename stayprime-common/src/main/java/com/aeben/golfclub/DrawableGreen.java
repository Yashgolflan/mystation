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
public class DrawableGreen extends DrawableCourseShape {
    public Coordinates front, middle, back;

    /**
     * Defines this green grid system.
     * The first point is the center of the grid, the second point
     * is the direction, the next points are center of grids 1, 2...
     */
    public List<Coordinates> gridPoints;
    public List<Coordinates> gridLines;


    public DrawableGreen(String name, Integer id, GolfCourseObject parentObject, List<Coordinates> shapeCoordinates, boolean closedShape, float strokeWidth, Color strokeColor, Color fillColor) {
        super(name, id, parentObject, ObjectType.GREEN, shapeCoordinates, closedShape, strokeWidth, strokeColor, fillColor);
    }

    public DrawableGreen(String name, Integer id, GolfCourseObject parentObject) {
        super(name, id, parentObject);
        type = ObjectType.GREEN;
    }

    @Override
    public DrawableGreen clone() {
        List<Coordinates> coords = null;
        if(shapeCoordinates != null) {
            coords = new ArrayList<Coordinates>(shapeCoordinates.size());
            for(Coordinates c: shapeCoordinates)
                coords.add(c.clone());
        }
        DrawableGreen green = new DrawableGreen(name, id,
                getParentObject(), coords, closedShape,
                strokeWidth, strokeColor, fillColor);
        green.lastDrawnShape = lastDrawnShape;
        green.front = front;
        green.middle = middle;
        green.back = back;
        green.gridPoints = gridPoints == null? null
                : new ArrayList<Coordinates>(gridPoints);
        green.gridLines = gridLines == null? null
                : new ArrayList<Coordinates>(gridLines);
        return green;
    }

}
