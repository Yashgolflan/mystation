/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.aeben.elementos.mapview;

import com.stayprime.geo.Coordinates;
import java.awt.Color;

/**
 *
 * @author benjamin
 */
public interface DrawablePointObject extends DrawableObject {
    public Coordinates getCoordinates();
    public Color getColor();
}
