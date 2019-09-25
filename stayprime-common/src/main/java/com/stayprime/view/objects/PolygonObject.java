/*
 * 
 */
package com.stayprime.view.objects;

import java.awt.Color;
import org.jhotdraw.geom.Polygon2D;

/**
 *
 * @author benjamin
 */
public class PolygonObject {
    private Polygon2D polygon;
    private Color fillColor;
    private Color lineColor;

    public PolygonObject() {
    }

    public PolygonObject(Polygon2D polygon, Color fillColor, Color lineColor) {
        this.polygon = polygon;
        this.fillColor = fillColor;
        this.lineColor = lineColor;
    }

    /**
     * @return the polygon
     */
    public Polygon2D getPolygon() {
        return polygon;
    }

    /**
     * @param polygon the polygon to set
     */
    public void setPolygon(Polygon2D polygon) {
        this.polygon = polygon;
    }

    /**
     * @return the fillColor
     */
    public Color getFillColor() {
        return fillColor;
    }

    /**
     * @param fillColor the fillColor to set
     */
    public void setFillColor(Color fillColor) {
        this.fillColor = fillColor;
    }

    /**
     * @return the lineColor
     */
    public Color getLineColor() {
        return lineColor;
    }

    /**
     * @param lineColor the lineColor to set
     */
    public void setLineColor(Color lineColor) {
        this.lineColor = lineColor;
    }
}
