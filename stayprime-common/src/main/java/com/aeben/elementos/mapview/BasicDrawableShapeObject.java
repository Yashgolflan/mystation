/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.aeben.elementos.mapview;

import com.aeben.golfclub.GolfCourseObject;
import com.stayprime.geo.Coordinates;
import java.awt.Color;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author benjamin
 */
public class BasicDrawableShapeObject extends GolfCourseObject implements DrawableShapeObject {
    public List<Coordinates> shapeCoordinates;
    public boolean closedShape = true;
    public float strokeWidth = 2;
    public Color strokeColor;
    public Color fillColor;
    public transient Shape lastDrawnShape;

    public static final Color defaultColor = new Color(0x44FFFFFF, true);

    public BasicDrawableShapeObject() {
    }

    public BasicDrawableShapeObject(List<Coordinates> shapeCoordinates, boolean closedShape, float strokeWidth, Color strokeColor, Color fillColor) {
        this.shapeCoordinates = shapeCoordinates;
        this.closedShape = closedShape;
        this.strokeWidth = strokeWidth;
        this.strokeColor = strokeColor;
        this.fillColor = fillColor;
    }

    public boolean isClosedShape() {
        return closedShape;
    }

    public Color getFillColor() {
        return fillColor;
    }

    public List<Coordinates> getShapeCoordinates() {
        return shapeCoordinates;
    }

    public Color getStrokeColor() {
        return strokeColor;
    }

    public float getStrokeWidth() {
        return strokeWidth;
    }

    public Shape getLastDrawnShape() {
        return lastDrawnShape;
    }

    public void setLastDrawnShape(Shape s) {
        lastDrawnShape = s;
    }

    @Override
    public GolfCourseObject clone() {
        ArrayList<Coordinates> shape = new ArrayList<Coordinates>(shapeCoordinates);
        return new BasicDrawableShapeObject(shape, closedShape, strokeWidth, strokeColor, fillColor);
    }

}
