/*
 * 
 */

package com.aeben.golfcourse.elements;

import java.util.Collections;
import java.util.List;

/**
 *
 * @author benjamin
 */
public abstract class AbstractCourseElement implements CourseElement {
    private String name;
    private Integer id;
    private CourseElement parent;

    public AbstractCourseElement(CourseElement parent, Integer id, String name) {
	this.name = name;
	this.id = id;
	this.parent = parent;
    }

    
    public void setId(Integer id) {
	this.id = id;
    }

    public void setName(String name) {
	this.name = name;
    }

    public void setParent(CourseElement parent) {
	this.parent = parent;
    }

    public Integer getId() {
	return id;
    }

    public String getName() {
	return name;
    }

    public CourseElement getParent() {
	return parent;
    }

    public List<CourseElement> getChildren() {
	return Collections.EMPTY_LIST;
    }

    public void set(CourseElement e) {
	setParent(e.getParent());
	setId(e.getId());
	setName(e.getName());
    }

}
