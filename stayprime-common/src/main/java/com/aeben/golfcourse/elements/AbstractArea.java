/*
 * 
 */

package com.aeben.golfcourse.elements;

import com.stayprime.geo.CoordinateCalculations;
import com.stayprime.geo.Coordinates;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author benjamin
 */
public abstract class AbstractArea extends AbstractCourseElement implements Area {
    private List<Coordinates> shape;
    private final ObjectType type;
    private Color color;

    public AbstractArea(CourseElement parent, ObjectType type, Integer id, String name) {
	super(parent, id, name);
	this.type = type;
	
	shape = Collections.EMPTY_LIST;
	color = Color.white;
    }

    public boolean contains(Coordinates coord) {
	if(shape == null)
	    return false;
	else
	    return CoordinateCalculations.shapeContains(shape, coord);
    }

    public void setShape(List<Coordinates> shape) {
	this.shape = shape;
    }

    public List<Coordinates> getShape() {
	return shape;
    }

    public void setColor(Color color) {
	this.color = color;
    }

    public Color getColor() {
	return color;
    }

    @Override
    public void set(CourseElement e) {
	Area area = (Area) e;
	super.set(e);

	setShape(new ArrayList(area.getShape()));
    }

    public ObjectType getType() {
	return type;
    }

//    public void accept(CourseElementVisitor visitor) {
//	visitor.visitCourseElement(this);
//    }
}
