/*
 * 
 */

package com.aeben.golfcourse.util;

import com.stayprime.golf.course.GolfCourse;
import com.stayprime.golf.course.GolfHole;

/**
 *
 * @author benjamin
 */
public class GolfCourseUtil {

    public static boolean isNextInSequence(GolfHole hole1, GolfHole hole2) {
        if(hole1 == null || hole2 == null)
            return false;

        int absn1 = hole1.getHoleIndex();
        int absn2 = hole2.getHoleIndex();

        if(absn1 + 1 == absn2)
            return true;

        GolfCourse course1 = hole1.getGolfCourse();
        GolfCourse course2 = hole2.getGolfCourse();

        if(course1 == course2) {
            int n1 = hole1.getHoleIndex();
            int n2 = hole2.getHoleIndex();

            if(n1 == course1.getHoleCount() && n2 == 1)
                return true;
        }

        return false;
    }
    
}
