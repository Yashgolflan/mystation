/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.stayprime.util.geometry;

import java.awt.geom.Point2D;

/**
 *
 * @author benjamin
 */
public class UnitVector {
    public final Point2D point;
    public final double angle;

    public UnitVector(Point2D point, double angle) {
        this.point = point;
        this.angle = normaliseAngle(angle);
    }

    public UnitVector invert() {
        return new UnitVector(point, angle + Math.PI);
    }

    public static double normaliseAngle(double angle) {
        return angle - (2.0 * Math.PI * Math.floor(angle / (2.0 * Math.PI)));
    }
}
