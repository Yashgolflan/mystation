/*
 * 
 */
package com.stayprime.view.objects;

import com.stayprime.model.golf.Position;
import com.stayprime.view.TransformSource;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

/**
 * Override Point2D applying a transformation from TransformSource.
 * @author benjamin
 */
public class TransformPoint extends Position {
    private final Point2D point = new Point2D.Double();
    private final Point2D trans = new Point2D.Double();
    private final TransformSource transformSource;
    private AffineTransform transform;

    public TransformPoint(TransformSource t) {
        this(t, 0, 0);
    }

    public TransformPoint(TransformSource t, Point2D p) {
        this(t, p.getX(), p.getY());
    }

    public TransformPoint(TransformSource t, double x, double y) {
        super();
        this.transformSource = t;
        point.setLocation(x, y);
        transform(true);
    }

    @Override
    public double getX() {
        transform(false);
        return trans.getX();
    }

    @Override
    public double getY() {
        transform(false);
        return trans.getY();
    }

    @Override
    public void setLocation(Position p) {
        setLocation(p.getX(), p.getY());
    }

    @Override
    public void setLocation(double x, double y) {
        point.setLocation(x, y);
        transform(true);
    }

    private void transform(boolean force) {
        if(force || transform != transformSource.getTransform()) {
            transform = transformSource.getTransform();
            transform.transform(point, trans);
        }
    }

}
