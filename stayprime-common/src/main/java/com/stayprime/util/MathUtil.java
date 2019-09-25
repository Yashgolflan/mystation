/*
 * 
 */

package com.stayprime.util;

import com.stayprime.geo.Coordinates;
import com.stayprime.util.geometry.Xy;

import java.awt.geom.Point2D;

/**
 *
 * @author benjamin
 */
public class MathUtil {
    public static double normalizeAngle(double angle) {
	if(Double.isInfinite(angle) || Double.isNaN(angle))
	    return angle;
	
	double newAngle = angle;
	while (newAngle < -180)
	    newAngle += 360;
	while (newAngle > 180)
	    newAngle -= 360;
	
	return newAngle;
    }

    //From http://stackoverflow.com/questions/849211/shortest-distance-between-a-point-and-a-line-segment
    public static double sqr(double x) {
	return x * x;
    }

    /**
     * Calculates the squared distance from point v to point w.
     * Takes Point2D objects as parameters.
     * @param v point v
     * @param w point w
     * @return squared distance from v to w
     */
    public static double dist2(Point2D v, Point2D w) {
        return sqr(v.getX() - w.getX()) + sqr(v.getY() - w.getY());
    }

    public static double dist2(Xy v, Xy w) {
        return sqr(v.getX() - w.getX()) + sqr(v.getY() - w.getY());
    }

    /**
     * Calculates the distance from point v to point w.
     * Takes Point2D objects as parameters.
     * @param v point v
     * @param w point w
     * @return distance from v to w
     */
    public static double dist(Point2D v, Point2D w) {
	return Math.sqrt(sqr(v.getX() - w.getX()) + sqr(v.getY() - w.getY()));
    }

    /**
     * Calculates the squared distance from point v to point w.
     * Takes the x and y coordinates of each point as parameters.
     * @param vx point v's x coordinate
     * @param vy point v's y coordinate
     * @param wx point w's x coordinate
     * @param wy point w's y coordinate
     * @return squared distance from v to w
     */
    public static double dist2(double vx, double vy, double wx, double wy) {
	return sqr(vx - wx) + sqr(vy - wy);
    }

    /**
     * Returns the squared distance of a point to a line segment.
     * It saves the use of the square root function, 
     * util for comparing multiple values.
     * @param p the point
     * @param v the first point of the line segment
     * @param w the second point of the line segment
     * @return squared distance of the point to the line segment
     */
    public static double distToSegment2(Point2D p, Point2D v, Point2D w) {
	double l2 = dist2(v, w);
	if (l2 == 0)
	    return dist2(p, v);
	double t = ((p.getX() - v.getX()) * (w.getX() - v.getX()) + (p.getY() - v.getY()) * (w.getY() - v.getY())) / l2;
	if (t < 0)
	    return dist2(p, v);
	if (t > 1)
	    return dist2(p, w);

	return dist2(p.getX(), p.getY(), v.getX() + t * (w.getX() - v.getX()), v.getY() + t * (w.getY() - v.getY()));
    }

    public static double distToSegment2(Xy p, Xy v, Xy w) {
        double l2 = dist2(v, w);
        if (l2 == 0)
            return dist2(p, v);
        double t = ((p.getX() - v.getX()) * (w.getX() - v.getX()) + (p.getY() - v.getY()) * (w.getY() - v.getY())) / l2;
        if (t < 0)
            return dist2(p, v);
        if (t > 1)
            return dist2(p, w);

        return dist2(p.getX(), p.getY(), v.getX() + t * (w.getX() - v.getX()), v.getY() + t * (w.getY() - v.getY()));
    }
    /**
     * Returns the distance of a point to a line segment.
     * @param p the point
     * @param v the first point of the line segment
     * @param w the second point of the line segment
     * @return distance of the point to the line segment
     */
    public static double distToSegment(Point2D p, Point2D v, Point2D w) {
	return Math.sqrt(distToSegment2(p, v, w));
    }

    /**
     * Returns the projection of a point on a line segment.
     * @param p the point
     * @param v the first point of the line segment
     * @param w the second point of the line segment
     * @return the projection of point p on the line segment
     */
    public static Point2D projectPointOnLineSegment(Point2D p, Point2D v, Point2D w) {
        double l2 = dist2(v, w);
        if (l2 == 0)
            return new Point2D.Double(p.getX(), p.getY());

        double t = ((p.getX() - v.getX()) * (w.getX() - v.getX()) + (p.getY() - v.getY()) * (w.getY() - v.getY())) / l2;
        if (t < 0)
            return v;
        if (t > 1)
            return w;

        return new Point2D.Double(v.getX() + t * (w.getX() - v.getX()), v.getY() + t * (w.getY() - v.getY()));
    }

    public static Xy projectPointOnLineSegment(Xy p, Xy v, Xy w) {
        double l2 = dist2(v, w);
        if (l2 == 0)
            return new Xy(p.getX(), p.getY());

        double t = ((p.getX() - v.getX()) * (w.getX() - v.getX()) + (p.getY() - v.getY()) * (w.getY() - v.getY())) / l2;
        if (t < 0)
            return v;
        if (t > 1)
            return w;

        return new Xy(v.getX() + t * (w.getX() - v.getX()), v.getY() + t * (w.getY() - v.getY()));
    }

    public static Point2D.Double getUnitVector(Point2D v, Point2D w) {
	double l = dist(v, w);
        return new Point2D.Double((w.getX() - v.getX()) / l, (w.getY() - v.getY()) / l);
    }

    public static Point2D.Double scale(Point2D v, double scale) {
        return new Point2D.Double(v.getX()*scale, v.getY()*scale);
    }

    public static Point2D.Double add(Point2D v, Point2D w) {
        return new Point2D.Double(v.getX() + w.getX(), v.getY() + w.getY());
    }

    public static Point2D.Double sub(Point2D v, Point2D w) {
        return new Point2D.Double(v.getX() - w.getX(), v.getY() - w.getY());
    }
}
