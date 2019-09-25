/*
 * 
 */
package com.stayprime.view.objects;

import com.stayprime.model.golf.Position;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 *
 * @author benjamin
 */
public class MeasureObject extends Line2D {
    private String text;
    private Position point1;
    private Position point2;
    private Point2D p1;
    private Point2D p2;

    public MeasureObject(String text, Position point1) {
        this(text, point1, null);
    }

    public MeasureObject(String text, Position point1, Position point2) {
        this.text = text;
        this.point1 = point1;
        this.point2 = point2;
        p1 = setP(point1, p1);
        p2 = setP(point2, p2);
    }

    private Point2D setP(Position point1, Point2D p) {
        if (point1 == null) {
            return null;
        }
        else if (p == null) {
            return new Point2D.Double(point1.getX(), point1.getY());
        }
        else {
            p.setLocation(point1.getX(), point1.getY());
            return p;
        }
    }

    /**
     * @return the text
     */
    public String getText() {
        return text;
    }

    /**
     * @param text the text to set
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * @return the point1
     */
    public Position getPoint1() {
        return point1;
    }

    /**
     * @param point1 the point1 to set
     */
    public void setPoint1(Position point1) {
        this.point1 = point1;
        p1 = setP(point1, p1);
    }

    /**
     * @return the point2
     */
    public Position getPoint2() {
        return point2;
    }

    /**
     * @param point2 the point2 to set
     */
    public void setPoint2(Position point2) {
        this.point2 = point2;
        p2 = setP(point2, p2);
    }

    @Override
    public double getX1() {
        return point1.getX();
    }

    @Override
    public double getY1() {
        return point1.getY();
    }

    @Override
    public Point2D getP1() {
        p1 = setP(point1, p1);
        return p1;
    }

    @Override
    public double getX2() {
        return point2.getX();
    }

    @Override
    public double getY2() {
        return point2.getY();
    }

    @Override
    public Point2D getP2() {
        p2 = setP(point2, p2);
        return p2;
    }

    @Override
    public void setLine(double x1, double y1, double x2, double y2) {
        setPoint1(new Position(x1, y1));
        setPoint2(new Position(x2, y2));
    }

    @Override
    public Rectangle2D getBounds2D() {
        double x1 = getX1(), y1 = getY1(), x2 = getX2(), y2 = getY2();
        double x, y, w, h;
        if (x1 < x2) {
            x = x1;
            w = x2 - x1;
        } else {
            x = x2;
            w = x1 - x2;
        }
        if (y1 < y2) {
            y = y1;
            h = y2 - y1;
        } else {
            y = y2;
            h = y1 - y2;
        }
        return new Rectangle2D.Double(x, y, w, h);
    }

}
