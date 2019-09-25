/*
 * 
 */

package com.aeben.golfcourse.objects;

import com.aeben.golfcourse.elements.CourseElement;

/**
 *
 * @author benjamin
 */
public interface Course extends CourseElement {

    public GolfClub getGolfClub();

    public String getName();
    
    public int getNumber();

    public Hole getHole(int number);

    public int getHoleCount();

}
