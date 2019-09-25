/*
 * 
 */

package com.aeben.golfcourse.elements;

import com.stayprime.geo.Coordinates;
import java.awt.Color;

/**
 *
 * @author benjamin
 */
public class BasicPoint extends AbstractCourseElement implements Point {
    private Coordinates location;
    private final ObjectType type;
    private Color color = Color.white;

    public BasicPoint(CourseElement parent, ObjectType type, Integer id, String name) {
	super(parent, id, name);
	this.type = type;

	location = null;
    }

    public void setLocation(Coordinates location) {
	this.location = location;
    }

    public void setColor(Color color) {
	this.color = color;
    }

    public Coordinates getLocation() {
	return location;
    }

    public Color getColor() {
	return color;
    }

    public ObjectType getType() {
	return type;
    }

    public void accept(CourseElementVisitor visitor) {
	visitor.visitCourseElement(this);
    }

}
