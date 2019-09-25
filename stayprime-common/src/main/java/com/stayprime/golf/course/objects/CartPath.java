/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.stayprime.golf.course.objects;

import com.stayprime.geo.CoordinateCalculations;
import com.stayprime.geo.Coordinates;
import com.stayprime.golf.objects.AbstractFeature;
import com.stayprime.golf.objects.BasicArea;
import com.stayprime.golf.objects.GeomType;
import java.awt.geom.Line2D;
import java.util.List;
import org.apache.commons.lang.math.NumberUtils;

/**
 *
 * @author benjamin
 */
public class CartPath extends BasicArea {
    public static final float defaultWidth = 8f;
    private float width = 8f;

    public CartPath(Integer id, String name) {
	super(ObjectType.CARTH_PATH, GeomType.POLYLINE, name);
    }

    public CartPath(AbstractFeature f) {
	super(ObjectType.CARTH_PATH, GeomType.POLYLINE, "");
        setProperties(f.getProperties());
    }

    public void setWidth(float meters) {
	this.width = meters;
        properties.setProperty("width", String.valueOf(meters));
    }

    public Float getWidth() {
	return width;
    }

    @Override
    public void setProperties(String props) {
        super.setProperties(props);
        setProperties();
    }

    private void setProperties() {
        String w = properties.getProperty("width");
        setWidth(w == null? defaultWidth : NumberUtils.toFloat(w));
    }

    @Override
    public boolean contains(Coordinates coord) {
	List<Coordinates> shape = getShape();
	double minDistanceSq = Double.MAX_VALUE;
	int mdidx = -1;
	
	for(int i = 0; i < shape.size() - 1; i++) {
	    Coordinates p1 = shape.get(i), p2 = shape.get(i+1);

	    //We want to take into account latitude for quick short distance calculations
	    //http://en.wikipedia.org/wiki/Geographical_distance#Spherical_Earth_projected_to_a_plane
	    double latCorrection = Math.cos(Math.toRadians((p2.latitude - p1.latitude)/2));

	    double distanceSq = Line2D.ptSegDistSq(
		    p1.longitude*latCorrection, p1.latitude,
		    p2.longitude*latCorrection, p2.latitude,
		    coord.longitude*latCorrection, coord.latitude);

	    if(distanceSq < minDistanceSq) {
		minDistanceSq = distanceSq;
		mdidx = i;
	    }
	}

	return mdidx > -1 &&
		Math.sqrt(minDistanceSq)*CoordinateCalculations.R < width/2;

    }

}
