/*
 * 
 */
package com.stayprime.basestation2.renderers;

import com.aeben.elementos.mapview.DrawableObject;
import com.aeben.golfcourse.util.DistanceUnits;
import com.stayprime.geo.Coordinates;
import java.awt.Font;
import java.awt.Shape;

/**
 *
 * @author benjamin
 */
public class DrawableDistance implements DrawableObject {
    public final Coordinates point1;
    public final Coordinates point2;
    public final Font font;
    public final DistanceUnits units; // = GolfClub.Units.YARDS;
    public final DistanceRenderer.DistancePosition position;

    public DrawableDistance(Coordinates point1, Coordinates point2, Font font, DistanceUnits units) {
        this(point1, point2, font, units, DistanceRenderer.DistancePosition.CENTER);
    }

    public DrawableDistance(Coordinates point1, Coordinates point2, Font font, DistanceUnits units, DistanceRenderer.DistancePosition position) {
        this.point1 = point1;
        this.point2 = point2;
        this.font = font;
        this.units = units;
        this.position = position;
    }

    public Shape getLastDrawnShape() {
        return null;
    }

    public void setLastDrawnShape(Shape s) {
    }
    
}
