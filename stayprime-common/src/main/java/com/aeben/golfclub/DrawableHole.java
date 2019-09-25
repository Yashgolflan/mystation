/*
 * 
 */

package com.aeben.golfclub;

import com.aeben.elementos.mapview.DrawableShapeObject;
import com.stayprime.geo.Coordinates;
import java.awt.Color;
import java.awt.Shape;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author benjamin
 */
public class DrawableHole implements DrawableShapeObject {
    private final HoleDefinition hole;
    private final Coordinates corners[];
    private Shape lastDrawnShape;

    public DrawableHole(HoleDefinition holeDefinition) {
	hole = holeDefinition;
	corners = new Coordinates[4];
    }

    public HoleDefinition getHole() {
	return hole;
    }

    public List<Coordinates> getShapeCoordinates() {
	if(hole.map == null)
	    return Collections.EMPTY_LIST;
	else {
	    corners[0] = hole.map.getTopLeft();
	    corners[1] = hole.map.getTopRight();
	    corners[2] = hole.map.getBottomRight();
	    corners[3] = hole.map.getBottomLeft();
	    return Arrays.asList(corners);
	}
    }

    public boolean isClosedShape() {
	return true;
    }

    public float getStrokeWidth() {
	return 0;
    }

    public Color getStrokeColor() {
	return Color.white;
    }

    public Color getFillColor() {
	return null;
    }

    public Shape getLastDrawnShape() {
	return lastDrawnShape;
    }

    public void setLastDrawnShape(Shape s) {
	lastDrawnShape = s;
    }

}
