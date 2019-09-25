/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.stayprime.basestation2.renderers;

import com.aeben.golfclub.DrawableCourseShape;
import com.aeben.golfclub.GolfCourseObject;
import com.aeben.golfclub.GolfCourseObject.ObjectType;
import com.stayprime.geo.Coordinates;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author benjamin
 */
public class DrawableAdZone extends DrawableCourseShape {
    private boolean keepVisible;

    public DrawableAdZone(String name, Integer id, GolfCourseObject parentObject, List<Coordinates> shapeCoordinates, float strokeWidth, Color strokeColor, Color fillColor) {
        super(name, id, parentObject, ObjectType.AD_ZONE, shapeCoordinates, true, strokeWidth, strokeColor, fillColor);
    }

    public DrawableAdZone(String name, Integer id, GolfCourseObject parentObject) {
        super(name, id, parentObject);
        type = ObjectType.AD_ZONE;
    }

    public boolean isKeepVisible() {
	return keepVisible;
    }

    public void setKeepVisible(boolean keepVisible) {
	this.keepVisible = keepVisible;
    }

    @Override
    public DrawableAdZone clone() {
        List<Coordinates> coords = null;
        if(shapeCoordinates != null) {
            coords = new ArrayList<Coordinates>(shapeCoordinates.size());
            for(Coordinates c: shapeCoordinates)
                coords.add(c.clone());
        }
        DrawableAdZone adZone = new DrawableAdZone(name, id,
                getParentObject(), coords,
                strokeWidth, strokeColor, fillColor);
        adZone.lastDrawnShape = lastDrawnShape;
        adZone.keepVisible = keepVisible;
        return adZone;
    }

}
