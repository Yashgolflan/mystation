/*
 * 
 */

package com.aeben.golfcourse.objects;

import com.aeben.golfcourse.elements.BasicArea;
import com.aeben.golfcourse.elements.CourseElementVisitor;

/**
 *
 * @author benjamin
 */
public class HoleZone extends BasicArea {
    private Hole hole;
    //TODO This should be a setting in the base station
    private Integer holeChangeDelay = null;

    public HoleZone(Hole parent, Integer id, String name) {
	super(parent, ObjectTypes.HOLE_ZONE, id, name);
	this.hole = parent;
    }

    public void setParent(Hole parent) {
	super.setParent(parent);
	this.hole = parent;
    }

    public Hole getHole() {
	return hole;
    }

    public Integer getHoleChangeDelay() {
	return holeChangeDelay;
    }

    public void setHoleChangeDelay(Integer seconds) {
	this.holeChangeDelay = seconds;
    }

    public void accept(CourseElementVisitor visitor) {
	visitor.visitCourseElement(this);
    }
}
