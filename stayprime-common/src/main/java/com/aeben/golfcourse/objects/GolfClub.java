/*
 * 
 */

package com.aeben.golfcourse.objects;

import com.aeben.golfcourse.elements.CourseElement;
import com.aeben.golfcourse.util.DistanceUnits;
import com.stayprime.geo.BasicMapImage;
import java.util.Date;
import java.util.List;

/**
 *
 * @author benjamin
 */
public interface GolfClub extends CourseElement {
    
    public int getSiteId();

    public String getName();
    
    public String getContactInfo();

    public int getCourseCount();

    public Course getCourse(int number);

    //public List<CourseElement> getCourseObjects();

    public List<Course> getCourses();

    public Date getLastUpdated();

    public String getLogoImage();

    public String getWelcomeImage();

    public String getThankyouImage();

    public BasicMapImage getMapImage();

    public Date getPinLocationUpdated();

    public DistanceUnits getDefaultUnits();
    
    public String getTimeZone();

    public boolean isUnitsSelectable();

}
