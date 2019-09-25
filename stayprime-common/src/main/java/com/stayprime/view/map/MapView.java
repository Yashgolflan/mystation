/*
 * 
 */
package com.stayprime.view.map;

import com.stayprime.geo.BasicMapImage;
import com.stayprime.geo.CoordConverter;
import com.stayprime.geo.CoordinateCalculations;
import com.stayprime.geo.Coordinates;
import com.stayprime.view.ImageView;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

/**
 *
 * @author benjamin
 */
public class MapView extends ImageView implements CoordConverter {
    private BasicMapImage map;
    private Point2D mapScale;
    private double angle;
    private boolean validMap;

    public MapView() {
    }

    @Override
    public void setImage(BufferedImage image) {
        throw new RuntimeException("Please use setMapImage()");
    }

    public void setMap(BasicMapImage map, BufferedImage image) {
	this.map = map;
        super.setImage(image);
        setMapScale();
    }

    public BasicMapImage getMap() {
        return map;
    }

    public boolean isValidMap() {
        return validMap;
    }

    public BufferedImage getImage() {
        return super.getImage(); //To change body of generated methods, choose Tools | Templates.
    }

    private void setMapScale() {
        Rectangle2D imageBounds = getImageBounds();
        if(imageBounds != null && map != null) {
            validMap = true;
            double xScale = imageBounds.getWidth() / map.getTopLeft().metersTo(map.getTopRight());
            double yScale = imageBounds.getHeight() / map.getTopLeft().metersTo(map.getBottomLeft());
            mapScale = new Point2D.Double(xScale, yScale);
            angle = CoordinateCalculations.getAngleRad(map.getTopLeft(), map.getTopRight());
        }
        else {
            validMap = false;
            mapScale = new Point2D.Double(1, 1);
        }
    }

    /**
     * Map image scale in pixels/meters.
     * @return Point2D with the image x and y scale.
     */
    public Point2D getMapScale() {
	return mapScale;
    }

    /**
     * The inclination of the map relative to the x axis in Mercator projection.
     * @return the angle of the map in radians.
     */
    public double getMapAngle() {
        return angle;
    }

    /*
     * Implement CoordConverter
     */
    @Override
    public Point2D toPoint(Coordinates latLong) {
        if (validMap) {
            return CoordinateCalculations.getXY(latLong, map, getImageBounds());
        }
        return null;
    }

    public Point2D toPoint(double lat, double lon) {
        if (validMap) {
            return CoordinateCalculations.getXY(lat, lon, map, getImageBounds());
        }
        return null;
    }

    @Override
    public Coordinates toLatLong(Point2D point) {
        if (validMap) {
            return CoordinateCalculations.getCoordinatesFromXYRelativeToMapPixel(point, map, getImageBounds());
        }
        return null;
    }

}
