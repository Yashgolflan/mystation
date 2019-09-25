/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.stayprime.golf.course.storage;

import com.stayprime.geo.Coordinates;
import com.stayprime.golf.course.GolfCourse;
import com.stayprime.golf.course.GolfHole;
import com.stayprime.golf.course.Site;

/**
 *
 * @author benjamin
 */
public interface GolfClubWriter {
    public void writeFullGolfClubDefinition(Site golfClub);

    public void writeGolfClubDefinition(Site golfClub);

    public void createCourse(Site club, GolfCourse course);

    public void updateCourse(Site club, int courseNumber, GolfCourse course);

    public void deleteCourse(Site club, int courseNumber);

    public void createHole(GolfCourse course, GolfHole hole);

    public void updateHole(GolfCourse course, int holeNumber, GolfHole hole);

    public void deleteHole(GolfCourse course, int holeNumber);

    public void writePinLocation(GolfHole hole, Coordinates location);

//    public void createObject(CompositeCourseElement parent, CourseElement object);
//
//    public void updateObject(CompositeCourseElement parent, int objectId, CourseElement object);
//
//    public void deleteObject(CompositeCourseElement parent, int objectId);
}