/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.aeben.elementos.mapview;

import com.aeben.golfclub.GolfCourseObject;
import com.stayprime.geo.Coordinates;
import java.awt.Color;
import java.awt.Shape;

/**
 *
 * @author benjamin
 */
public class BasicDrawablePointObject extends GolfCourseObject implements DrawablePointObject {
    public Coordinates coordinates;
    public Color color = Color.white;
    public transient Shape lastDrawnShape;

    public BasicDrawablePointObject() {
    }

    public BasicDrawablePointObject(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public Color getColor() {
        return color;
    }

    public Shape getLastDrawnShape() {
        return lastDrawnShape;
    }

    public void setLastDrawnShape(Shape s) {
        lastDrawnShape = s;
    }

    @Override
    public GolfCourseObject clone() {
        return null;
    }
}
