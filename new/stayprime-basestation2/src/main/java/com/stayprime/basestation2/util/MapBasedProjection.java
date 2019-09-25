/*
 * 
 */
package com.stayprime.basestation2.util;

import com.aeben.elementos.mapview.MapProjection;
import com.stayprime.geo.BasicMapImage;
import com.stayprime.geo.CoordinateCalculations;
import com.stayprime.geo.Coordinates;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 *
 * @author benjamin
 */
public class MapBasedProjection implements MapProjection {

    private BasicMapImage map;
    private Rectangle2D imageRectangle;

    public MapBasedProjection() {
    }

    public void setMap(BasicMapImage map) {
        this.map = map;
    }

    public void setImageRectangle(Rectangle2D imageRectangle) {
        this.imageRectangle = imageRectangle;
    }

    @Override
    public BasicMapImage getMap() {
        return map;
    }

    @Override
    public Point2D project(Coordinates coord) {
        return convertMapPointToView(convertCoordinatesToMapPoint(coord));
    }

    @Override
    public Coordinates projectInverse(Point2D point) {
        return convertMapPointToCoordinates(convertViewPointToMap(point));
    }

    @Override
    public double getAngle() {
        return 0;
    }

    @Override
    public double getScale() {
        return 1;
    }

    private static Point2D convertMapPointToView(Point2D mapPoint, Rectangle2D imageRect) {
        if (mapPoint instanceof Point2D.Float) {
            return new Point2D.Float(
                    (float) (mapPoint.getX() + imageRect.getX()),
                    (float) (mapPoint.getY() + imageRect.getY()));
        }
        else {
            return new Point2D.Double(
                    mapPoint.getX() + imageRect.getX(),
                    mapPoint.getY() + imageRect.getY());
        }

    }

    private Point2D convertMapPointToView(Point2D mapPoint) {
        if (imageRectangle != null)
            return convertMapPointToView(mapPoint, imageRectangle);
        else
            return null;
    }

    private static Point2D convertViewPointToMap(Point2D viewPoint, Rectangle2D imageRect) {
        if (viewPoint instanceof Point2D.Float) {
            return new Point2D.Float(
                    (float) (viewPoint.getX() - imageRect.getX()),
                    (float) (viewPoint.getY() - imageRect.getY()));
        }
        else {
            return new Point2D.Double(
                    viewPoint.getX() - imageRect.getX(),
                    viewPoint.getY() - imageRect.getY());
        }
    }

    private Point2D convertViewPointToMap(Point2D viewPoint) {
        if (imageRectangle != null)
            return convertViewPointToMap(viewPoint, imageRectangle);
        else
            return null;
    }

    private static Coordinates convertMapPointToCoordinates(Point2D mapPoint, BasicMapImage courseMap, Rectangle2D imageRect) {
        Coordinates coord
                = CoordinateCalculations.getCoordinatesFromXYRelativeToMapPixel(mapPoint, courseMap, imageRect);

        return coord;
    }

    private Coordinates convertMapPointToCoordinates(Point2D mapPoint) {
        if (map != null && imageRectangle != null)
            return convertMapPointToCoordinates(mapPoint, map, imageRectangle);
        else
            return null;
    }

    private static Point2D convertCoordinatesToMapPoint(Coordinates coord, BasicMapImage courseMap, Rectangle2D imageRect) {
        Point2D.Double mapPoint = CoordinateCalculations.getXYRelativeToMapPixelCoordinates(
                coord, courseMap, imageRect);

        return mapPoint;
    }

    private Point2D convertCoordinatesToMapPoint(Coordinates coord) {
        if (map != null && imageRectangle != null)
            return convertCoordinatesToMapPoint(coord, map, imageRectangle);
        else
            return null;
    }

}
