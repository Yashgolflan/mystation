/*
 * 
 */
package com.stayprime.view.objects;

import com.stayprime.view.TransformSource;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import org.jhotdraw.geom.Polygon2D;

/**
 * Override Polygon2D applying a transformation from TransformSource.
 * @author benjamin
 */
public class TransformPolygon extends Polygon2D {
    private Polygon2D.Double source;
    private final Polygon2D.Double transformed;
    private final Point2D.Double tmp = new Point2D.Double();
    private final TransformSource transformSource;
    private AffineTransform transform;

    public TransformPolygon(TransformSource t) {
        this(t, new Polygon2D.Double());
    }

    public TransformPolygon(TransformSource t, Polygon2D.Double p) {
        super();
        this.transformSource = t;
        this.source = p;
        transformed = new Polygon2D.Double(p.xpoints, p.ypoints, p.npoints);
        transform(true);
    }

    private void transform(boolean force) {
        if(force || transform != transformSource.getTransform()) {
            for(int i = 0; i < source.npoints; i++) {
                transform = transformSource.getTransform();
                tmp.setLocation(source.xpoints[i], source.ypoints[i]);
                transform.transform(tmp, tmp);
                transformed.xpoints[i] = tmp.getX();
                transformed.ypoints[i] = tmp.getY();
                transformed.invalidate();
            }
        }
    }

    @Override
    public void invalidate() {
    }

    @Override
    public void translate(double deltaX, double deltaY) {
        source.translate(deltaX, deltaY);
        transform(true);
    }

    @Override
    public void addPoint(double x, double y) {
        source.addPoint(x, y);
        transformed.addPoint(x, y);
        transform(true);
    }

    @Override
    public Rectangle2D getBounds2D() {
        transform(false);
        return transformed.getBounds2D();
    }

    @Override
    public boolean contains(double x, double y) {
        transform(false);
        return transformed.contains(x, y);
    }

    @Override
    public boolean intersects(double x, double y, double w, double h) {
        transform(false);
        return transformed.intersects(x, y, w, h);
    }

    @Override
    public boolean contains(double x, double y, double w, double h) {
        transform(false);
        return transformed.contains(x, y, w, h);
    }

    @Override
    public PathIterator getPathIterator(AffineTransform at) {
        transform(false);
        return transformed.getPathIterator(at);
    }

}
