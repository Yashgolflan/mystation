/*
 * 
 */

package com.aeben.golfcourse.elements;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author benjamin
 */
public abstract class CompositeCourseElement extends AbstractCourseElement {
    private List<CourseElement> children;
    private List<CourseElement> unmodifiableChildren;

    public CompositeCourseElement(CourseElement parent, Integer id, String name) {
	super(parent, id, name);
	children = new ArrayList<CourseElement>();
	unmodifiableChildren = Collections.unmodifiableList(children);
    }

    public void accept(CourseElementVisitor visitor) {
	visitor.visitCourseElement(this);
    }

    public void add(CourseElement courseElement) {
	children.add(courseElement);
    }

    public void remove(CourseElement courseElement) {
	children.remove(courseElement);
    }

    public List<CourseElement> getChildren() {
	return unmodifiableChildren;
    }

    @Override
    public void set(CourseElement e) {
	super.set(e);

	children.clear();
	for(CourseElement child: e.getChildren())
	    add(child);
    }

}
