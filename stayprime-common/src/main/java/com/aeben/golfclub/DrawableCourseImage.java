/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.aeben.golfclub;

import com.aeben.golfclub.GolfCourseObject.ObjectType;
import com.aeben.elementos.mapview.BasicDrawableImageObject;
import com.stayprime.geo.Coordinates;
import com.google.gson.annotations.Expose;
import java.awt.geom.Point2D;

/**
 *
 * @author benjamin
 */
public class DrawableCourseImage extends BasicDrawableImageObject {
    public String name;
    public Integer id;
    public ObjectType type = ObjectType.UNKNOWN;

    public DrawableCourseImage(String name, Integer id, GolfCourseObject parentObject) {
        super();
        this.name = name;
        this.id = id;
        setParent(parentObject);
    }

    public DrawableCourseImage(String name, Integer id, GolfCourseObject parentObject,
            ObjectType type, String imagePath, Coordinates coordinates, Float diagonalSizeInMeters,
            Float minDiagonalPixelSize, Point2D sizeInMeters, Point2D relativeImageCenter) {
        super(imagePath, coordinates, diagonalSizeInMeters, minDiagonalPixelSize, sizeInMeters, relativeImageCenter);
        this.name = name;
        this.id = id;
        this.type = type;
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
    public DrawableCourseImage clone() {
        DrawableCourseImage image = new DrawableCourseImage(name, id, getParentObject(), type, imagePath,
                coordinates.clone(), diagonalSizeInMeters, minDiagonalPixelSize,
                sizeInMeters, relativeImageCenter);
        image.lastDrawnShape = lastDrawnShape;
        return image;
    }
}
