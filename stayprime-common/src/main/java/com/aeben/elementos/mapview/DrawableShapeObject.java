/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.aeben.elementos.mapview;

import com.stayprime.geo.Coordinates;
import java.awt.Color;
import java.util.List;

/**
 *
 * @author benjamin
 */
public interface DrawableShapeObject extends DrawableObject {
    public List<Coordinates> getShapeCoordinates();
    public boolean isClosedShape();
    public float getStrokeWidth();
    public Color getStrokeColor();
    public Color getFillColor();
}
