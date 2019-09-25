/*
 * 
 */

package com.aeben.golfcourse.elements;

import com.stayprime.geo.Coordinates;
import java.awt.Color;
import java.util.List;

/**
 *
 * @author benjamin
 */
public interface Path extends CourseElement {
    public boolean contains(Coordinates coord);
    public List<Coordinates> getShape();
    public Color getColor();
    public Float getWidth();
}
