/*
 * 
 */
package com.stayprime.view.control;

import com.stayprime.view.TransformView;
import java.awt.geom.Point2D;
import org.jdesktop.animation.timing.TimingTarget;

/**
 *
 * @author benjamin
 */
public class ZoomAnimator implements TimingTarget {
    protected final ViewControl control;
    protected final TransformView transformView;

    private final Point2D startPivot;
    private final Point2D targetPivot;
    private final Point2D translation;

    private double startScale;
    private double targetScale;

    protected boolean forward;

    public ZoomAnimator(final ViewControl control) {
        this.control = control;
        transformView = control.getTransformView();
        startPivot = new Point2D.Double();
        targetPivot = new Point2D.Double();
        translation = new Point2D.Double();
    }

    public void setTargetScale(double targetScale) {
        this.targetScale = targetScale;
    }

    public double getTargetScale() {
        return targetScale;
    }

    public void setStartPivot(Point2D startPivot) {
        this.startPivot.setLocation(startPivot);
    }

    public void setTargetPivot(Point2D targetPivot) {
        this.targetPivot.setLocation(targetPivot);
    }

    public void begin() {
        startScale = transformView.getScale();
        forward = targetScale > startScale;
        translation.setLocation(startPivot);
    }

    public void timingEvent(float fraction) {
        setScaleAndCenter(fraction);
        control.repaintContainer(true);
    }

    public void end() {
        control.repaintContainer(false);
    }

    public void repeat() {
    }

    protected void setScaleAndCenter(double fraction) {
        Point2D centerBeforeTransform = new Point2D.Double();
        centerBeforeTransform.setLocation(translation);
        transformView.inverseTransform(centerBeforeTransform);

        control.setScale(startScale + (targetScale - startScale) * fraction);
        double x = startPivot.getX() + (targetPivot.getX() - startPivot.getX()) * fraction;
        double y = startPivot.getY() + (targetPivot.getY() - startPivot.getY()) * fraction;
        translation.setLocation(x, y);
        transformView.setRotationCenter(translation);

//        System.out.println(startPivot + " " + centerBeforeTransform + " " + translation + " " + targetPivot);
        transformView.transform(centerBeforeTransform);

        control.movePointTo(centerBeforeTransform, translation);
        control.setTranslationLimits();
    }

}
