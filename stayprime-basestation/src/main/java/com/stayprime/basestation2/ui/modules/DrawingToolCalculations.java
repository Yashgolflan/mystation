/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.stayprime.basestation2.ui.modules;

import com.stayprime.util.geometry.UnitVector;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.List;
import org.jhotdraw.geom.Polygon2D;

/**
 *
 * @author benjamin
 */
public class DrawingToolCalculations {
    public static Line2D.Double projectLineInPolygon(Point2D p, double angle, Shape shape) {
        if(p != null && shape instanceof Polygon2D.Double) {
            Polygon2D.Double path = (Polygon2D.Double) shape;

            Point2D p0 = null, p1, line1 = null, line2 = null;
            Point2D firstPoint = null;
            for(int i = 0; i <= path.npoints; i++) {

                if(i == path.npoints && firstPoint != null)
                    p1 = firstPoint;
                else
                    p1 = new Point2D.Double(path.xpoints[i], path.ypoints[i]);

                if(firstPoint == null)
                    firstPoint = p1;

                if(p0 != null) {
                    UnitVector v1 = new UnitVector(p, angle);
                    Point2D itx = getPolarLineAndSegmentIntersection(v1, p0, p1);
                    if(itx != null) {
                        UnitVector v2 = new UnitVector(p, 
				Math.atan2(itx.getY() - p.getY(), itx.getX()-p.getX()));

			if(dotProduct(v1, v2) > 0) {
                            if(line1 == null)
                                line1 = itx;
                            else if(itx.distance(p) > line1.distance(p))
                                line1 = itx;
                        }
                        else {
                            if(line2 == null)
                                line2 = itx;
                            else if(itx.distance(p) > line2.distance(p))
                                line2 = itx;
                        }
                    }
                }
                p0 = p1;
            }

            if(line1 == null && line2 == null)
                return null;
            else if(line1 == null)
                line1 = p;
            else if(line2 == null)
                line2 = p;

            return new Line2D.Double(line1, line2);
        }

        return null;
    }

    /**
     * Finds the intersection of the directional polar line with a poligonal
     * shape.
     * @param v Unit vector with start point and direction
     * @param path Path to find intersection with
     * @param result the point to store the intersection if found
     * @return the index of the point where the segment that intersects the ray starts, or -1 if no intersection was found
     */
    public static int findIntersectionDirectional(UnitVector v, List<Point2D> path, Point2D result) {
        int index = -1;

        Point2D p0 = null, p1, intersection = null;
        Point2D firstPoint = null;

        for(int i = 0; i <= path.size(); i++) {

            if(i == path.size() && firstPoint != null)
                p1 = firstPoint;
            else
                p1 = path.get(i);

            if(firstPoint == null)
                firstPoint = p1;

            if(p0 != null) {
                Point2D itx = getPolarLineAndSegmentIntersection(v, p0, p1);
                if(itx != null) {
                    UnitVector v2 = new UnitVector(v.point, 
			    Math.atan2(itx.getY() - v.point.getY(), itx.getX() - v.point.getX()));

		    if(dotProduct(v, v2) > 0) { // Same direction
                        if(intersection == null || itx.distance(v.point) > intersection.distance(v.point)) {
                            intersection = itx;
                            index = i-1;
                        }
                    }
                }
            }
            p0 = p1;
        }

        if(intersection != null && result != null) {
            result.setLocation(intersection);
        }

        return index;
    }

    public static double dotProduct(UnitVector v1, UnitVector v2) {
        return Math.cos(v1.angle - v2.angle);
    }

    public static Point2D getPolarLineAndSegmentIntersection(UnitVector vector, Point2D p1, Point2D p2) {
        //Evitar singularidades en -PI/2 y +PI/2
        double m = Math.tan(vector.angle);
        return getLineAndSegmentIntersection(vector.point, m, p1, p2);
    }

    public static Point2D getLineAndSegmentIntersection(Point2D p, double m, Point2D p1, Point2D p2) {
        double m2 = (p2.getY()-p1.getY())/(p2.getX()-p1.getX());
        Point2D intx = getLinesIntersection(p, m, p1, m2);
        if(intx.getX() > Math.min(p1.getX(), p2.getX())
		&& intx.getX() < Math.max(p1.getX(), p2.getX())) {
            return intx;
        }
        else
            return null;
    }

    public static Point2D getPolarLinesIntersection(UnitVector v1, UnitVector v2) {
        double m1 = Math.tan(v1.angle);
        double m2 = Math.tan(v2.angle);
        return getLinesIntersection(v1.point, m1, v2.point, m2);
    }

    static float MAX_SLOPE = 1000f;
    public static Point2D getLinesIntersection(Point2D p1, double m1, Point2D p2, double m2) {
        //m*(x - p.getX()) + p.getY() = m2*(x - p0.getX()) + p0.getY();
        //m*x - m*p.getX() + p.getY() = m2*x - m2*p0.getX() + p0.getY();
        //x*(m - m2) = m*p.getX() - p.getY() - m2*p0.getX() + p0.getY();
        if(m1 > MAX_SLOPE)
            m1 = MAX_SLOPE;
        else if(m1 < -MAX_SLOPE)
            m1 = -MAX_SLOPE;

        if(m2 > MAX_SLOPE)
            m2 = MAX_SLOPE;
        else if(m2 < -MAX_SLOPE)
            m2 = -MAX_SLOPE;

        double x = (m1*p1.getX() - p1.getY() - m2*p2.getX() + p2.getY()) / (m1 - m2);
        double y = m1*(x - p1.getX()) + p1.getY();

        return new Point2D.Double(x, y);
    }
}
