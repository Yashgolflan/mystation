package com.stayprime.golf.course.objects;

import java.awt.Color;

public class CourseTeeBox {
//    private Hole hole;
    private Integer id;
    private int number;
    private String name;
    private Color color;

    public CourseTeeBox() {
    }

    public CourseTeeBox(int number, String name, Color color) {
        this.number = number;
        this.name = name;
        this.color = color;
    }

    /*
     * CourseElement
     */
    public ObjectType getType() {
        return ObjectType.TEE_BOX;
    }

    public void set(CourseTeeBox e) {
	CourseTeeBox tee = (CourseTeeBox) e;
        setId(tee.getId());
        setNumber(tee.getNumber());
	setColor(tee.getColor());
	setName(tee.getName());
    }

    @Override
    protected CourseTeeBox clone() {
        CourseTeeBox t = new CourseTeeBox();
        t.set(this);
        return t;
    }

    /*
     * Accessors
     */

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public Color getColor() {
	return color;
    }

    public void setColor(Color color) {
	this.color = color;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
