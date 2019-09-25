/*
 * 
 */

package com.aeben.golfcourse.elements;

import com.stayprime.geo.Coordinates;

/**
 *
 * @author benjamin
 */
public interface ActiveZone extends Area {
    public boolean isEnabled();
    public void test(Coordinates coord);
    public CourseAction getAction();
}
