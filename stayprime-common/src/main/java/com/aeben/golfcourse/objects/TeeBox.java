package com.aeben.golfcourse.objects;

import com.aeben.golfcourse.elements.AbstractCourseElement;
import com.aeben.golfcourse.elements.CourseElement;
import com.aeben.golfcourse.elements.CourseElementVisitor;
import com.aeben.golfcourse.elements.ObjectType;
import java.awt.Color;

public class TeeBox extends AbstractCourseElement {

    private Integer distance;
    private Integer teePar;
    private Integer teeSI;
    private Color color;
    private Hole hole;

    public TeeBox(Hole hole, String name, Integer id) {
	super(hole, id, name);
        this.hole = hole;
    }

    /*
     * CourseElement
     */
    public ObjectType getType() {
        return ObjectTypes.TEE_BOX;
    }

    @Override
    public void set(CourseElement e) {
	TeeBox tee = (TeeBox) e;
	super.set(e);

	setColor(tee.getColor());
	setDistance(tee.getDistance());
	setTeePar(tee.getTeePar());
	setTeeSI(tee.getTeeSI());
    }

    /*
     * Accessors
     */
    public Color getColor() {
	return color;
    }

    public void setColor(Color color) {
	this.color = color;
    }

    public Integer getTeePar() {
	return teePar;
    }

    public void setTeePar(Integer teePar) {
	this.teePar = teePar;
    }

    public Integer getTeeSI() {
	return teeSI;
    }

    public void setTeeSI(Integer teeSI) {
	this.teeSI = teeSI;
    }

    public Integer getDistance() {
	return distance;
    }

    public void setDistance(Integer teeDistance) {
	this.distance = teeDistance;
    }

    public Hole getHole() {
	return hole;
    }

    public void setHole(HoleImpl hole) {
	this.hole = hole;
    }

    public void accept(CourseElementVisitor visitor) {
	visitor.visitCourseElement(this);
    }
}
