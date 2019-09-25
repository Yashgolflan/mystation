/*
 * 
 */

package com.aeben.golfcourse.objects;

import com.aeben.golfcourse.elements.Path;
import com.stayprime.geo.Coordinates;
import com.aeben.golfcourse.elements.BasicArea;
import com.aeben.golfcourse.elements.CourseElement;
import com.stayprime.geo.BasicMapImage;
import java.util.List;

/**
 *
 * @author benjamin
 */
public interface Hole extends CourseElement {

    public Course getCourse();

    public int getNumber();

    public int getAbsoluteNumber();

    public BasicMapImage getMapImage();

    public String getFlyover();

    public String getProTips();

    public int getPaceOfPlay();

    public int getPar();

    public int getStrokeIndex();
    
    public Coordinates getPinLocation();

    public Coordinates getReferencePoint();

    public Green getGreen();

    public BasicArea getHoleOutline();

    public Path getCartPath();

    public BasicArea getApproachLine();

    public boolean isCartPathOnly();

    public List<TeeBox> getTeeBoxes();

    public List<HoleZone> getHoleZones();

}
