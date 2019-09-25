/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.stayprime.golf.course.storage;

import com.stayprime.golf.course.GolfCourse;
import com.stayprime.golf.course.GolfHole;
import com.stayprime.golf.course.Site;
import com.stayprime.util.file.FileLocator;
import java.util.List;

/**
 *
 * @author benjamin
 */
public interface GolfClubLoader {
    public Site loadFullGolfClubDefinition();

    public Site loadGolfClubDefinition();

    public List<GolfCourse> loadCourses(Site club);

    public GolfCourse loadCourseDefinition(Site club, int courseNumber);

    public GolfHole loadHole(Site golfClub, GolfCourse course, int holeNumber);

    public boolean loadPinLocation(Site courseInfo);

    public void setFileLocator(FileLocator fl);

}
