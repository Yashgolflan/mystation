/*
 * 
 */
package com.stayprime.view;

import com.stayprime.geo.Coordinates;
import com.stayprime.view.map.MapView;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;

/**
 * Wraps a MapView and applies arbitrary translation, rotation and scale.
 *
 * @author benjamin
 */
public class TransformView implements DrawableView, TransformSource {
    private AffineTransform transform;

    private final MapView view;

    private Point2D.Double translation;
    private double scale;
    private Double rotation;
    private Point2D.Double rotationCenterUntransformed;

    public TransformView(MapView view) {
        this.view = view;
        transform = new AffineTransform();
        reset();
    }

    public MapView getMapView() {
        return view;
    }

    public final void reset() {
        rotationCenterUntransformed = new Point2D.Double();
        translation = new Point2D.Double();
        rotation = null;
        scale = 1.0;
    }

    public void setScale(double scale) {
        this.scale = scale;
        setTransform();
    }

    public double getScale() {
        return scale;
    }

    public void setRotation(Double rotation) {
        this.rotation = rotation;
        setTransform();
    }

    public Double getRotation() {
        return rotation;
    }

    public Point2D getRotationCenter() {
        return transform.transform(rotationCenterUntransformed, null);
    }

    public void setRotationCenter(Point2D center) {
        rotationCenterUntransformed.setLocation(center);
        inverseTransform(rotationCenterUntransformed);
        setTransform();
    }

    public Point2D getTranslation() {
        return translation;
    }

    public void setTranslation(Point2D trans) {
        translation.setLocation(trans);
        setTransform();
    }

    private void setTransform() {
        transform = new AffineTransform();
        transform.translate(translation.getX(), translation.getY());
        if (rotation != null) {
            transform.rotate(rotation, rotationCenterUntransformed.getX() * scale, rotationCenterUntransformed.getY() * scale);
        }
        transform.scale(scale, scale);

    }

    /*
     * Implement TransformSource
     */

    @Override
    public AffineTransform getTransform() {
        return transform;
    }

    @Override
    public boolean render(Graphics2D g2d, AffineTransform parentTransform, boolean quick) {
        AffineTransform t = new AffineTransform();
        t.concatenate(parentTransform);
        t.concatenate(transform);
        return view.render(g2d, t, true);
    }

    /*
     * Utility methods to transform Points and Coordinates
     */

    public void transform(Point2D point) {
        transform(point, point);
    }

    public Point2D transform(Point2D src, Point2D dst) {
        return transform.transform(src, dst);
    }

    public void inverseTransform(Point2D point) {
        try {
            transform.inverseTransform(point, point);
        } catch (NoninvertibleTransformException ex) {
        }
    }

    public Point2D inverseTransform(Point2D src, Point2D dst) {
        try {
            return transform.inverseTransform(src, dst);
        } catch (NoninvertibleTransformException ex) {
            return null;
        }
    }

    public Point2D toViewPoint(Coordinates coord) {
        if (view != null && view.isValidMap()) {
            Point2D viewPoint = view.toPoint(coord);
            transform(viewPoint);
            return viewPoint;
        }
        return null;
    }

    public Coordinates fromViewPoint(Point2D point) {
        if (view != null && view.isValidMap()) {
            Point2D p = new Point2D.Double();
            p.setLocation(point);
            inverseTransform(point);
            Coordinates coord = view.toLatLong(point);
            return coord;
        }
        return null;
    }

    public Point2D toMapPoint(Coordinates coord) {
        if (view != null && view.isValidMap()) {
            return view.toPoint(coord);
        }
        return null;
    }

    public Coordinates fromMapPoint(Point2D point) {
        if (view != null && view.isValidMap()) {
            return view.toLatLong(point);
        }
        return null;
    }

}
