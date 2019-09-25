package com.stayprime.golf.course;

import com.stayprime.golf.course.objects.ObjectType;
import com.stayprime.geo.Coordinates;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;

@Embeddable
public class HoleTeeBox {
    private int number;

    private int par;

    private int strokeIndex;

    private int distance;

    @Transient //Mapped by accessor method below
    private Coordinates position;

    @Transient //Mapped by accessor method below
    private Color color;

    private String name;

    public HoleTeeBox() {
        this(0);
    }

    public HoleTeeBox(int number) {
        this.number = number;
    }

    /*
     * CourseElement
     */
    public ObjectType getType() {
        return ObjectType.TEE_BOX;
    }

    public void set(HoleTeeBox e) {
	HoleTeeBox tee = (HoleTeeBox) e;
        setNumber(e.getNumber());
	setDistance(tee.getDistance());
        setPar(e.getPar());
        setStrokeIndex(e.getStrokeIndex());
        setPosition(e.getPosition());
        setColor(e.getColor());
        setName(e.getName());
    }

    @Override
    protected HoleTeeBox clone() {
        HoleTeeBox t = new HoleTeeBox();
        t.set(this);
        return t;
    }

    /*
     * Accessors
     */

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getPar() {
        return par;
    }

    public void setPar(int par) {
        this.par = par;
    }

    public int getStrokeIndex() {
        return strokeIndex;
    }

    public void setStrokeIndex(int strokeIndex) {
        this.strokeIndex = strokeIndex;
    }

    public Coordinates getPosition() {
        return position;
    }

    public void setPosition(Coordinates position) {
        this.position = position;
    }

    @Column(name="position") @Access(AccessType.PROPERTY)
    public String getPositionString() {
        return position == null? null : position.toString();
    }

    public void setPositionString(String pos) {
        this.position = pos == null? null : new Coordinates(pos);
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    @Column(name="color") @Access(AccessType.PROPERTY)
    public Integer getColorRGB() {
        return color == null? null : color.getRGB();
    }

    public void setColorRGB(Integer rgb) {
        this.color = rgb == null? null : new Color(rgb);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static List<HoleTeeBox> cloneList(List<HoleTeeBox> teeBoxes) {
        List<HoleTeeBox> list = new ArrayList<HoleTeeBox>(teeBoxes.size());
        for (HoleTeeBox t: teeBoxes) {
            list.add(t.clone());
        }
        return list;
    }

}
