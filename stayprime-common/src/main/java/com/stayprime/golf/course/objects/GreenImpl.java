/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.stayprime.golf.course.objects;

import com.stayprime.geo.Coordinates;
import com.stayprime.golf.objects.BasicArea;
import com.stayprime.golf.course.GolfHole;
import com.stayprime.golf.objects.AbstractFeature;
import com.stayprime.golf.objects.GeomType;
import com.stayprime.util.gson.Exclude;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author benjamin
 */
public class GreenImpl extends BasicArea {
    @Exclude
    private final GolfHole hole;
    private Coordinates front, center, back;
    private List<Coordinates> pins;
    private List<Coordinates> grid;

    public GreenImpl(GolfHole parent, Integer id, String name) {
	super(ObjectType.GREEN, GeomType.POLY, name);
	this.hole = parent;
    }

    public GreenImpl(AbstractFeature f, GolfHole hole) {
	super(ObjectType.GREEN, GeomType.POLY, "Green");
        this.hole = hole;
        setGeometry(f.getGeometry());
        setProperties(f.getProperties());
    }

    public GolfHole getHole() {
	return hole;
    }

    public Coordinates getBackOfGreen() {
	return back;
    }

    public void setBackOfGreen(Coordinates back) {
	this.back = back;
        setPropertyString("back", back);
    }

    public Coordinates getCenterOfGreen() {
	return center;
    }

    public void setCenterOfGreen(Coordinates center) {
	this.center = center;
        setPropertyString("center", center);
    }

    public Coordinates getFrontOfGreen() {
	return front;
    }

    public void setFrontOfGreen(Coordinates front) {
	this.front = front;
        setPropertyString("front", front);
    }

    public List<Coordinates> getPinLocations() {
	return pins;
    }

    public void setPinLocations(List<Coordinates> pinLocations) {
	if(pinLocations == null) {
	    this.pins = null;
            properties.remove("pins");
        }
        else {
	    this.pins = Collections.unmodifiableList(new ArrayList<Coordinates>(pinLocations));
            properties.setProperty("pins", Coordinates.listToString(pinLocations));
        }
    }

    public List<Coordinates> getGreenGrid() {
	return grid;
    }

    public void setGreenGrid(List<Coordinates> greenGrid) {
	if(greenGrid == null) {
	    this.grid = null;
            properties.remove("grid");
        }
        else {
	    this.grid = Collections.unmodifiableList(new ArrayList<Coordinates>(greenGrid));
            properties.setProperty("grid", Coordinates.listToString(greenGrid));
        }
    }

    @Override
    public void setProperties(String props) {
        super.setProperties(props);
        setProperties();
    }

    public void setProperties() {
        front = Coordinates.fromString(properties.getProperty("front"));
        center = Coordinates.fromString(properties.getProperty("center"));
        back = Coordinates.fromString(properties.getProperty("back"));
        pins = getCoordListFromProperties("pins");
        grid = getCoordListFromProperties("grid");
    }

}
