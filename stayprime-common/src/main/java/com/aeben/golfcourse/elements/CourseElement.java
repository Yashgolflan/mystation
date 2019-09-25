/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.aeben.golfcourse.elements;

import java.util.List;

/**
 *
 * @author benjamin
 */
public interface CourseElement {
    public Integer getId();
    public String getName();
    public ObjectType getType();

    //TODO check leaf implementation
    public void set(CourseElement e);

    public void accept(CourseElementVisitor visitor);

    public CourseElement getParent();
    public List<CourseElement> getChildren();

}
