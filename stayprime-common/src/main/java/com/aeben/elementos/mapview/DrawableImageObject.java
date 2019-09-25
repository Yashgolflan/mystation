/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.aeben.elementos.mapview;

import com.stayprime.geo.Coordinates;
import java.awt.geom.Point2D;

/**
 *
 * @author benjamin
 */
public interface DrawableImageObject extends DrawablePointObject {
    public Coordinates getCoordinates();

    public Float getDiagonalSizeInMeters();
    public Float getMinDiagonalPixelSize();
    public Point2D getSizeInMeters();
    public Point2D getRelativeImageCenter();
    public String getImagePath();
}
