/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.aeben.golfcourse.objects;

import com.aeben.golfcourse.elements.CompositeCourseElement;
import com.aeben.golfcourse.elements.CourseElement;
import com.aeben.golfcourse.elements.ObjectType;
import java.util.ArrayList;

/**
 *
 * @author benjamin
 */
public class CourseImpl extends CompositeCourseElement implements Course {
    private ArrayList<Hole> holes;
    
    private int holeCount;

    private GolfClub golfClub;

    public CourseImpl(GolfClub parent, int number, String name, int holeCount) {
	super(parent, number, name);

	golfClub = parent;
	holes = new ArrayList<Hole>(holeCount);
	trimHoleCount(holeCount);
    }

    public void setGolfClub(GolfClub golfClub) {
	this.golfClub = golfClub;
    }
    
    private void trimHoleCount(int count) {
	while(holes.size() > count)
	    holes.remove(holes.size() - 1);
	
	holes.ensureCapacity(count);

	for(int n = holes.size(); n < count; n++) {
	    holes.add(new HoleImpl(this, n));
	}

	this.holeCount = count;

	holes.trimToSize();
    }

    public GolfClub getGolfClub() {
	return golfClub;
    }

    public int getNumber() {
	return getId();
    }

    public void setHoleCount(int holeCount) {
	trimHoleCount(holeCount);
    }

    public int getHoleCount() {
	return holeCount;
    }

    public void setHole(int number, Hole hole) {
	if(hole.getNumber() != number)
	    throw new IllegalArgumentException("Hole number doesn't match index");

	holes.set(number - 1, hole);
    }

    public Hole getHole(int number) {
	return holes.get(number - 1);
    }

    /*
     * Implement CourseElement
     */
    public ObjectType getType() {
	return ObjectTypes.GOLF_COURSE;
    }

    @Override
    public void set(CourseElement e) {
	CourseImpl course = (CourseImpl) e;
	super.set(e);

	setHoleCount(course.getHoleCount());

	for(int n = 1; n <= getHoleCount(); n++)
	    setHole(n, course.getHole(n));
    }

}
