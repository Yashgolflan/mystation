/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.aeben.golfclub;

import com.aeben.golfclub.GolfCourseObject.ObjectType;
import com.stayprime.geo.BasicMapImage;
import com.stayprime.util.gson.Exclude;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author benjamin
 */
public class HoleDefinition extends GolfCourseObject {

    public int number;
    public int par;
    public int strokeIndex;
    public BasicMapImage map;
    public DrawablePinLocation pinLocation;
    public boolean cartPathOnly;
    public DrawableCourseShape cartPath;
    public DrawableGreen green;
    private List<DrawableCourseShape> shapes;
    private List<DrawableCoursePoint> points;
    public List<TeeBox> teeBoxes;
    public int paceSeconds;
    public String proTips = null;
    public String flyover = null;
    public Date lastUpdated;
    public Date imageUpdated;
    public Date flyoverUpdated;

    @Exclude
    public CourseDefinition course;
    
    public static final GolfCourseObject.ObjectType type = GolfCourseObject.ObjectType.GOLF_HOLE;

    private DrawableCourseShape holeOutline;
    private DrawableCourseShape approachLine;

    public HoleDefinition(CourseDefinition course, int number) {
        this.course = course;
        this.number = number;

        shapes = new ArrayList<DrawableCourseShape>();
        points = new ArrayList<DrawableCoursePoint>();
        teeBoxes = new ArrayList<TeeBox>();

	if(course != null && course.getTeeBoxes() != null) {
	    for(TeeBox tee: course.getTeeBoxes()) {
		TeeBox holeTee = tee.clone();
		holeTee.distanceToHole = "";
		teeBoxes.add(holeTee);
	    }
	}
    }

    public void setHoleOutline(DrawableCourseShape holeOutline) {
	this.holeOutline = holeOutline;
    }

    public DrawableCourseShape getHoleOutline() {
	return holeOutline;
    }

    public void setApproachLine(DrawableCourseShape approachLine) {
        this.approachLine = approachLine;
    }

    public DrawableCourseShape getApproachLine() {
        return approachLine;
    }

    public List<DrawableCourseShape> getShapes() {
        return shapes;
    }

    public List<DrawableCoursePoint> getPoints() {
        return points;
    }

    @Override
    public String toString() {
        return Integer.toString(number);
    }

    public String getName() {
        return (course == null? "" : course.toString() + ", ")
                + "Hole " + number;
    }

    public Integer getId() {
        return number > 0? number : null;
    }

    public GolfCourseObject getParentObject() {
        return course;
    }

    public ObjectType getType() {
        return type;
    }

    @Override
    public HoleDefinition clone() {
        HoleDefinition hole = new HoleDefinition(course, number);
        hole.number = number;
        hole.par = par;
        hole.strokeIndex = strokeIndex;
        hole.map = map;
        hole.pinLocation = pinLocation;
        hole.cartPathOnly = cartPathOnly;
        hole.cartPath = cartPath;
        hole.shapes.addAll(shapes);
        hole.points.addAll(points);
        hole.teeBoxes.clear();
        hole.teeBoxes.addAll(teeBoxes);
        hole.paceSeconds = paceSeconds;
        hole.proTips = proTips;
        hole.flyover = flyover;
        hole.lastUpdated = lastUpdated;
        hole.imageUpdated = imageUpdated;
        hole.flyoverUpdated = flyoverUpdated;
        hole.course = course;
        hole.holeOutline = holeOutline;
        hole.approachLine = approachLine;
        return hole;
    }

    void setParents(CourseDefinition c) {
        this.course = c;
        for (GolfCourseObject o : shapes) {
            o.setParent(this);
        }
        for (GolfCourseObject o : points) {
            o.setParent(this);
        }
        for (GolfCourseObject o : teeBoxes) {
            o.setParent(this);
        }
        if (pinLocation != null) {
            pinLocation.setParent(this);
        }
        if (cartPath != null) {
            cartPath.setParent(this);
        }
        if (green != null) {
            green.setParent(this);
        }
    }

}
