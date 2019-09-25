package com.stayprime.golfapp.cart;

import com.stayprime.device.Time;
import com.stayprime.geo.Coordinates;
import com.stayprime.golf.objects.BasicArea;
import com.stayprime.golf.course.objects.ClubhouseZone;
import com.stayprime.golf.course.objects.GreenImpl;
import com.stayprime.golf.course.objects.HoleZone;
import com.stayprime.golf.course.objects.ObjectType;
import com.stayprime.golf.course.objects.WarningArea;
import com.stayprime.oncourseads.AdZone;
import java.util.HashMap;
import java.util.Map;

public class CourseAreaVisitor {

    private VisitedArea<WarningArea> visitedWarningZone;
    private VisitedArea<WarningArea> visitedRestrictedZone;
    private VisitedArea<ClubhouseZone> visitedClubhouseZone;
    private VisitedArea<HoleZone> visitedHoleZone;
    private VisitedArea<AdZone> visitedAdZone;

    private Coordinates location;
    private Map<BasicArea, VisitedArea> newAreas;
    private Map<BasicArea, VisitedArea> previousAreas;

    public CourseAreaVisitor() {
	previousAreas = new HashMap<BasicArea, VisitedArea>();
	newAreas = new HashMap<BasicArea, VisitedArea>();
    }

    public void start(Coordinates location) {
	this.location = location;
	newAreas.clear();

	visitedHoleZone = null;
	visitedWarningZone = null;
	visitedRestrictedZone = null;
	visitedClubhouseZone = null;
	visitedAdZone = null;
    }

    public void end() {
	//Add all newly visited areas
	previousAreas.putAll(newAreas);
	//Remove previously visited areas that were not visited this time
	previousAreas.keySet().retainAll(newAreas.keySet());
    }

    public boolean isInClubhouseZone() {
	return visitedClubhouseZone != null;
    }

    public boolean isInHoleZone() {
    	return visitedHoleZone != null;
    }

    public boolean isInRestrictedZone() {
	return visitedWarningZone != null || visitedRestrictedZone != null;
    }

    public boolean isInAdZone() {
	return visitedAdZone != null;
    }


    public VisitedArea<WarningArea> getVisitedRestrictedZone() {
        return visitedRestrictedZone;
    }

    public VisitedArea<WarningArea> getVisitedWarningZone() {
        return visitedWarningZone;
    }

    public VisitedArea<ClubhouseZone> getVisitedClubhouseZone() {
	return visitedClubhouseZone;
    }

    public VisitedArea<HoleZone> getVisitedHoleZone() {
	return visitedHoleZone;
    }

    public VisitedArea<AdZone> getVisitedAdZone() {
	return visitedAdZone;
    }

    /**
     * Checks if an area was previously visited. If it wasn't, add it to the
     * previously visited areas and set it's date to the date parameter
     * @param zone The visited area to check
     * @param date The visit date to be recorded in the map
     * @return The date recorded in the map, which represents the date this zone was entered
     */
    private VisitedArea addToAreasMap(BasicArea zone, long milliTime) {
	if (previousAreas.containsKey(zone)) {
	    VisitedArea old = previousAreas.get(zone);
	    //Don't do this, we need to retain the oldest hit time
	    //old.setLastVisited(date.getTime());
	    newAreas.put(zone, old);
	    return old;
	}
	else {
	    VisitedArea visitedArea = new VisitedArea(zone, milliTime);
	    newAreas.put(zone, visitedArea);
	    return visitedArea;
	}
    }

    /*
     * Visitor pattern
     */

    public void visitCourseElement(WarningArea zone) {
	if (zone.contains(location)) {
            if (zone.getType() == ObjectType.RESTRICTED_ZONE) {
                visitedRestrictedZone = addToAreasMap(zone, Time.milliTime());
            }
            else {
                visitedWarningZone = addToAreasMap(zone, Time.milliTime());
            }
	}
    }

    public void visitCourseElement(HoleZone zone) {
	if (zone.contains(location)) {
	    visitedHoleZone = addToAreasMap(zone, Time.milliTime());
	}
    }

    public void visitCourseElement(ClubhouseZone zone) {
	if (zone.contains(location)) {
	    visitedClubhouseZone = addToAreasMap(zone, Time.milliTime());
	}
    }

    public void visitCourseElement(AdZone zone) {
	if (zone.contains(location)) {
	    visitedAdZone = addToAreasMap(zone, Time.milliTime());
	}
    }

    public void visitCourseElement(GreenImpl green) {
    }

    public void visitCourseElement(BasicArea area) {
    }

}
