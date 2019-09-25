/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.aeben.elementos.mapview;

import com.aeben.golfclub.GolfCourseObject;
import com.stayprime.geo.Coordinates;
import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Point2D;

/**
 *
 * @author benjamin
 */
public class BasicDrawableImageObject extends GolfCourseObject implements DrawableImageObject {
    private static final Point2D.Float imageCenter = new Point2D.Float(.5f, .5f);

    public String imagePath;
    public Coordinates coordinates;
    public Float diagonalSizeInMeters;
    public Float minDiagonalPixelSize;
    public Point2D sizeInMeters;
    public Point2D relativeImageCenter = imageCenter;
    public transient Shape lastDrawnShape;

    public BasicDrawableImageObject() {
    }

    public BasicDrawableImageObject(String imagePath, Coordinates coordinates,
            Float diagonalSizeInMeters, Float minDiagonalPixelSize,
            Point2D sizeInMeters, Point2D relativeImageCenter) {
        this.imagePath = imagePath;
        this.coordinates = coordinates;
        this.diagonalSizeInMeters = diagonalSizeInMeters;
        this.minDiagonalPixelSize = minDiagonalPixelSize;
        this.sizeInMeters = sizeInMeters;
        this.relativeImageCenter = relativeImageCenter;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public Float getDiagonalSizeInMeters() {
        return diagonalSizeInMeters;
    }

    public Float getMinDiagonalPixelSize() {
        return minDiagonalPixelSize;
    }

    public Point2D getSizeInMeters() {
        return sizeInMeters;
    }

    public Point2D getRelativeImageCenter() {
        return relativeImageCenter;
    }

    public String getImagePath() {
        return imagePath;
    }

    public Shape getLastDrawnShape() {
        return lastDrawnShape;
    }

    public void setLastDrawnShape(Shape s) {
        lastDrawnShape = s;
    }

    public Color getColor() {
        return null;
    }

    @Override
    public GolfCourseObject clone() {
        return null;
    }

}
