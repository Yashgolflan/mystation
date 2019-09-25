/*
 * 
 */

package com.aeben.golfcourse.objects;

import com.stayprime.geo.Coordinates;
import com.aeben.golfcourse.elements.Area;
import java.util.List;

/**
 *
 * @author benjamin
 */
public interface Green extends Area {
    public Hole getHole();

    public Coordinates getBackOfGreen();

    public Coordinates getCenterOfGreen();

    public Coordinates getFrontOfGreen();
    
    public List<Coordinates> getPinLocations();

    public List<Coordinates> getGreenGrid();
}
