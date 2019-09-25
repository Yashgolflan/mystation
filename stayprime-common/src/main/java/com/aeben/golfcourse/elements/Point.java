/*
 * 
 */

package com.aeben.golfcourse.elements;

import com.stayprime.geo.Coordinates;
import java.awt.Color;

/**
 *
 * @author benjamin
 */
public interface Point extends CourseElement {
    public Coordinates getLocation();

    public Color getColor();
}
