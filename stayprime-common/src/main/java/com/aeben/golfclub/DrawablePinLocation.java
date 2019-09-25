/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.aeben.golfclub;

import com.stayprime.geo.Coordinates;
import java.util.Date;

/**
 *
 * @author benjamin
 */
public class DrawablePinLocation extends DrawableCoursePoint {
    public Date lastUpdated;

    public DrawablePinLocation(String name, int id, GolfCourseObject parentObject, Coordinates coordinates) {
        super(name, id, parentObject, ObjectType.PINFLAG, coordinates);
    }

    public DrawablePinLocation(String name, int id, GolfCourseObject parentObject) {
        super(name, id, parentObject, ObjectType.PINFLAG, null);
    }

    @Override
    public DrawablePinLocation clone() {
        DrawablePinLocation pin = new DrawablePinLocation(name, id, getParentObject(),
                coordinates == null? null : coordinates.clone());
        pin.id = id;
        pin.type = type;
        pin.lastDrawnShape = lastDrawnShape;
        pin.lastUpdated = lastUpdated;
        return pin;
    }

}
