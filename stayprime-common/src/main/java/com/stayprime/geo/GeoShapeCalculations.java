/*
 * 
 */
package com.stayprime.geo;

import java.awt.geom.Point2D;
import java.util.List;

/**
 *
 * @author benjamin
 */
public class GeoShapeCalculations {
    public static boolean shapeContains(List<Point2D> shape, Point2D pos) {
	int sides = shape.size();

	boolean oddNodes = false;
	double x = pos.getX(), y = pos.getY();
	int j = sides - 1;

	for (int i = 0; i < sides; i++) {
	    if (shape.get(i).getY() < y && shape.get(j).getY() >= y
             || shape.get(j).getY() < y && shape.get(i).getY() >= y) {
		
		if (shape.get(i).getX() + (y - shape.get(i).getY()) / (shape.get(j).getY() - shape.get(i).getY()) * (shape.get(j).getX() - shape.get(i).getX()) < x) {
		    oddNodes = !oddNodes;
		}
	    }
	    j = i;
	}

	return oddNodes;
    }
}
