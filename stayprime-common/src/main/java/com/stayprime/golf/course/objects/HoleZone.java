/*
 * 
 */

package com.stayprime.golf.course.objects;

import com.stayprime.golf.objects.BasicArea;
import com.stayprime.golf.course.GolfHole;
import com.stayprime.golf.objects.AbstractFeature;
import com.stayprime.golf.objects.GeomType;
import com.stayprime.util.gson.Exclude;

/**
 *
 * @author benjamin
 */
public class HoleZone extends BasicArea {
    @Exclude
    private GolfHole hole;
    //TODO This should be a setting in the base station
//    private Integer holeChangeDelay = null;

    public HoleZone(GolfHole parent, Integer id, String name) {
	super(ObjectType.HOLE_ZONE, GeomType.POLY, name);
	this.hole = parent;
    }

    public HoleZone(AbstractFeature f, GolfHole hole) {
	super(ObjectType.HOLE_ZONE, GeomType.POLY, f.getName());
	this.hole = hole;
    }

    public GolfHole getHole() {
	return hole;
    }

//    public Integer getHoleChangeDelay() {
//	return holeChangeDelay;
//    }
//
//    public void setHoleChangeDelay(Integer seconds) {
//	this.holeChangeDelay = seconds;
//    }

}
