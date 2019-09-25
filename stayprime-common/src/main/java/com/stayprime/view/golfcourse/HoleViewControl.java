/*
 * 
 */
package com.stayprime.view.golfcourse;

import com.stayprime.geo.Coordinates;
import com.stayprime.view.DrawableContainer;
import com.stayprime.golf.course.objects.GreenImpl;
import com.stayprime.golf.course.GolfHole;
import com.stayprime.golf.objects.AbstractFeature;
import com.stayprime.model.golf.Position;
import com.stayprime.view.TransformView;
import com.stayprime.view.control.ViewControl;
import com.stayprime.view.map.MapView;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.List;
import org.apache.commons.collections.CollectionUtils;
import org.jdesktop.animation.timing.Animator;

/**
 *
 * @author benjamin
 */
public class HoleViewControl extends ViewControl {
    private GolfHole hole;
    private final MapView mapView;
    private final Point2D zoomPivot_view;
    private Point2D holePivot_map;

    private final Point2D startPivot;
    private final Point2D finalPivot;
    private final Point2D targetPivot;

    private Animator animator;
    private HoleZoomAnimator holeZoomAnimator;

    private Double referenceAngle;
    private Double targetAngle;
    private int zoomLevel;
    private boolean autoZoom;

    private Rectangle activeArea;

    public HoleViewControl(TransformView transformView, DrawableContainer container, MapView mapView) {
        super(transformView, container, mapView);
        this.mapView = mapView;

        zoomPivot_view = new Point2D.Double();
        startPivot = new Point2D.Double();
        finalPivot = new Point2D.Double();
        targetPivot = new Point2D.Double();
        activeArea = null;
    }

    public void setHole(GolfHole hole) {
        this.hole = hole;

        if(hole != null) {
            if (hole.getPar() < 5)
                setZoomLevels(2);
            else
                setZoomLevels(3);
        }
    }

    public void setCartPosition(double x, double y) {
        if (autoZoom && activeArea != null) {
            //If the cart is above 40% of the height of the active area, zoom in
            double zoomPoint = activeArea.getY() + activeArea.getHeight()*.6;
            if (y < zoomPoint) {
                zoomIn();
            }
        }
    }

    public void setActiveArea(Rectangle activeViewArea) {
        this.activeArea = activeViewArea;
    }

    @Override
    protected void initAnimator() {
        if(animator == null) {
            holeZoomAnimator = new HoleZoomAnimator(this);
            animator = new Animator(animationDuration, holeZoomAnimator);
            animator.setDeceleration(0.6f);
        }

        animator.stop();
    }

    @Override
    public void setup() {
        super.setup();

        zoomLevel = 0;
        holePivot_map = null;
        referenceAngle = null;
        autoZoom = true;

        if (hole != null && hole.getFeatures() != null && mapView.isValidMap()) {
            GreenImpl green = hole.getFeatures().getGreen();
            setControlParameters(green);
        }
    }

    private void setControlParameters(GreenImpl green) {
        if (green != null && green.getCenterOfGreen() != null) {
            holePivot_map = mapView.toPoint(green.getCenterOfGreen());
            startPivot.setLocation(holePivot_map);
            getTransformView().transform(startPivot);

            AbstractFeature approachLine = hole.getFeatures().getApproachLine();
            setReferenceAngle(approachLine, green.getCenterOfGreen());
            setFinalZoomPivot();
        }
    }

    private void setReferenceAngle(AbstractFeature line, Coordinates center) {
        if (line != null) {
            List<Coordinates> shape = line.getShape();
            boolean notEmpty = CollectionUtils.isNotEmpty(shape);
            if (notEmpty && shape.size() > 1) {
                Coordinates approachPoint = shape.get(shape.size() - 2);
                Point2D b = startPivot;
                Point2D a = mapView.toPoint(approachPoint);
                getTransformView().transform(a);
                double angle = Math.atan2(b.getY() - a.getY(), b.getX() - a.getX());

                referenceAngle = (-angle - Math.PI/2);
            }
        }
    }

    private void setFinalZoomPivot() {
        if (container != null && holePivot_map != null) {
            Point2D p = new Point2D.Double(holePivot_map.getX(), holePivot_map.getY());
            getTransformView().transform(p);
//            double x = activeArea.getCenterX();
//            double y = activeArea.getCenterY();
            double x = (250 + container.getBounds().getWidth()) / 2;
            double y = 90 + (p.getY() - 90) * Math.pow(1.5, getZoomLevels());
            finalPivot.setLocation(x, y);
        }
    }

    @Override
    public void zoomIn() {
        if(zoomLevel < getZoomLevels()) {
            zoomLevel++;
            targetAngle = referenceAngle;

            if(zoomLevel == getZoomLevels()) {
                targetPivot.setLocation(finalPivot);
            }
            else {
                double x = startPivot.getX();
                double y = startPivot.getY() + (finalPivot.getY() - startPivot.getY()) * zoomLevel / getZoomLevels();
                targetPivot.setLocation(x, y);
            }

            if (holePivot_map == null) {
                super.zoomIn();
            }
            else {
                zoomPivot_view.setLocation(holePivot_map);
                getTransformView().transform(zoomPivot_view);
                zoomIn(zoomPivot_view);
            }
        }
    }

    @Override
    public void zoomOut() {
        zoomLevel = 0;
        targetAngle = null;
        autoZoom = false;

        if(holePivot_map == null) {
            super.zoomOut();
            super.zoomOut();
            super.zoomOut();
        }
        else {
            targetPivot.setLocation(startPivot);
            zoomPivot_view.setLocation(holePivot_map);
            getTransformView().transform(zoomPivot_view);
            zoom(-getZoomLevels(), zoomPivot_view);
        }
    }

    @Override
    protected void startAnimation() {
	if (animator.isRunning()) {
	    animator.stop();
        }

        holeZoomAnimator.setTargetScale(getTargetScale());
        holeZoomAnimator.setStartPivot(zoomPivot_view);
        holeZoomAnimator.setTargetPivot(targetPivot);
        holeZoomAnimator.setTargetAngle(targetAngle == null? 0 : targetAngle);

        animator.start();
    }

}
