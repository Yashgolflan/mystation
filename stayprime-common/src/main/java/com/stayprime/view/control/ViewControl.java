/*
 *
 */

package com.stayprime.view.control;

import com.stayprime.view.DrawableContainer;
import com.stayprime.geo.GeoShapeCalculations;
import com.stayprime.view.TransformView;
import com.stayprime.view.map.MapView;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import org.jdesktop.animation.timing.Animator;

/**
 *
 * @author benjamin
 */
public class ViewControl {
    protected final DrawableContainer container;
    protected final TransformView transformView;
    protected final MapView mapView;
    protected final int animationDuration = 800;

    private double minScale;
    private double maxScale;
    private double zoomFactor = 1.5;
    private int zoomLevels = 3;

    private Point2D startPivot_view;
    private Point2D targetPivot_view;

    private Animator animator;
    private ZoomAnimator zoomAnimator;

    private final Point2D imageCorners[];
    private final int sortedX[] = new int[4];
    private final int sortedY[] = new int[4];
    private Rectangle2D containerBounds;
    private double targetScale;

    public ViewControl(TransformView transformView, DrawableContainer container,
	    MapView imageView) {
	this.transformView = transformView;
	this.container = container;
        this.mapView = imageView;

        imageCorners = new Point2D[4];
        startPivot_view = new Point2D.Double();
        targetPivot_view = new Point2D.Double();
    }

    protected void initAnimator() {
        if(zoomAnimator == null) {
            zoomAnimator = new ZoomAnimator(this);
            animator = new Animator(animationDuration, zoomAnimator);
            animator.setDeceleration(0.6f);
        }

        animator.stop();
    }

    public void setup() {        
        initAnimator();

	containerBounds = null;
        resetBounds();

        targetScale = minScale;
        startPivot_view.setLocation(containerBounds.getCenterX(), containerBounds.getCenterY());

        setScale(minScale);
        setRotation(null);
        setRotationCenter(startPivot_view);        

        //Move image center to view center
        Rectangle2D ib = mapView.getImageBounds();
        if(ib != null) {
            Point2D mapCenter = new Point2D.Double(ib.getCenterX(), ib.getCenterY());
            transformView.transform(mapCenter);
            movePointTo(mapCenter, startPivot_view);
            setTranslationLimits();
        }
        repaintContainer(false);
    }

    public void resetBounds() {
        Rectangle2D imageBounds = mapView.getImageBounds();
        Rectangle2D oldBounds = containerBounds;
        containerBounds = container.getBounds();

        if(imageBounds != null && containerBounds != null) {
            minScale = Math.max(
                    containerBounds.getWidth() / imageBounds.getWidth(),
                    containerBounds.getHeight() / imageBounds.getHeight());
            maxScale = minScale * Math.pow(zoomFactor, zoomLevels);

            if (oldBounds != null) {
                //Move old center to new center
                Point2D oldCenter = new Point2D.Double(oldBounds.getCenterX(), oldBounds.getCenterY());
                Point2D newCenter = new Point2D.Double(containerBounds.getCenterX(), containerBounds.getCenterY());
                movePointTo(oldCenter, newCenter);
                setTranslationLimits();
                zoom(0, startPivot_view);
            }
        }

        repaintContainer(false);
    }

    public TransformView getTransformView() {
        return transformView;
    }

    public MapView getMapView() {
        return mapView;
    }

    public void setZoomLevels(int zoomLevels) {
        this.zoomLevels = zoomLevels;
    }

    public int getZoomLevels() {
        return zoomLevels;
    }

    public double getZoomFactor() {
        return zoomFactor;
    }

    public void repaintContainer(boolean quick) {
        container.setDirty();
        container.repaint(quick);
    }

    public void setRotationCenter(Point2D centerPoint) {
        transformView.setRotationCenter(centerPoint);
        setImageCorners();
    }

    public Point2D getRotationCenter() {
        return transformView.getRotationCenter();
    }

    public void setScale(double scale) {
        transformView.setScale(scale);
        setImageCorners();
    }

    public double getTargetScale() {
        return targetScale;
    }

    public void setRotation(Double targetAngle) {
        transformView.setRotation(targetAngle);
        setImageCorners();
        sortCorners();
        //setTranslation(getTranslation());
        //repaintContainer(true);
    }

    public double getRotation() {
        if(transformView.getRotation() == null)
            return 0;
        else
            return transformView.getRotation();
    }

    public void movePointTo(Point2D from, Point2D to) {
        Point2D.Double move = new Point2D.Double();
        Point2D trans = transformView.getTranslation();

	move.x = to.getX() - from.getX() + trans.getX();
	move.y = to.getY() - from.getY() + trans.getY();

        setTranslation(move);
    }

    public void setTranslationLimits() {
        if(imageCorners[0] == null) {
            return;
        }

        Point2D old = new Point2D.Double();
        old.setLocation(transformView.getTranslation());
//        double deltaX = t.getX() - old.getX();
//        double deltaY = t.getY() - old.getY();

        double minX2 = imageCorners[sortedX[0]].getX();
        double minY2 = imageCorners[sortedY[0]].getY();

        double maxX2 = imageCorners[sortedX[3]].getX();
        double maxY2 = imageCorners[sortedY[3]].getY();

        double deltaX = correctDelta(0, containerBounds.getMinX(), containerBounds.getMaxX(), minX2, maxX2);
        double deltaY = correctDelta(0, containerBounds.getMinY(), containerBounds.getMaxY(), minY2, maxY2);

        old.setLocation(old.getX() + deltaX, old.getY() + deltaY);
        transformView.setTranslation(old);
        setImageCorners();
    }

    public void setTranslation(Point2D t) {
        transformView.setTranslation(t);
        //repaintContainer(true);
        setImageCorners();
    }

    public Point2D getTranslation() {
        return transformView.getTranslation();
    }

    private void setImageCorners() {
        Rectangle2D ib = mapView.getImageBounds();
        if(ib != null) {
            imageCorners[0] = new Point2D.Double(0, 0);
            imageCorners[1] = new Point2D.Double(ib.getWidth(), 0);
            imageCorners[2] = new Point2D.Double(ib.getWidth(), ib.getHeight());
            imageCorners[3] = new Point2D.Double(0, ib.getHeight());
            transformView.getTransform().transform(imageCorners, 0, imageCorners, 0, 4);
        }
    }

    private double correctDelta(double deltaX, double boundsMinX, double boundsMaxX, double minX, double maxX) {
        double left = boundsMinX - minX - deltaX;
        double right = maxX + deltaX - boundsMaxX;

        if (left < 0 || right < 0) {
            if (left + right > 0) {
                if(left < 0) {
                    return boundsMinX - minX;
                }
                else {
                    return boundsMaxX - maxX;
                }
            }
            else {
                return (boundsMinX - minX - (maxX - boundsMaxX))/2;
            }
        }

        return deltaX;
    }

    private void sortCorners() {
        if(imageCorners[0] == null) {
            return;
        }

        //To fit the whole map inside the view bounds, we can use the outer
        //bounding box of the transformed map. However to fit the view
        //inside the map (no blank areas) we need to use the inner bounding box.
        //The inner bounding box is formed by the inner
        //X and Y coordinates of the left, right, top, bottom sides.
        //This only changes when the rotation changes, so we should create an
        //array of the indexes of the X and Y coordinates in order, and set
        //it only on translation, then use these indexes to get the current
        //coordinates like this:
        //corners[orderedX[0]].getX() points to the X value of the leftmost corner.

        boolean firstIsMin;
        firstIsMin = imageCorners[0].getX() <= imageCorners[1].getX();
        sortedX[0] = sortedX[2] = firstIsMin? 0 : 1;
        sortedX[1] = sortedX[3] = firstIsMin? 1 : 0;

        firstIsMin = imageCorners[0].getY() <= imageCorners[1].getY();
        sortedY[0] = sortedY[2] = firstIsMin? 0 : 1;
        sortedY[1] = sortedY[3] = firstIsMin? 1 : 0;

        for (int i = 2; i < imageCorners.length; i++) {
            Point2D p = imageCorners[i];

            if (p.getX() <= imageCorners[sortedX[0]].getX()) {
                sortedX[1] = sortedX[0];
                sortedX[0] = i;
            }
            else if (p.getX() <= imageCorners[sortedX[1]].getX()) {
                sortedX[1] = i;
            }

            if (p.getY() <= imageCorners[sortedY[0]].getY()) {
                sortedY[1] = sortedY[0];
                sortedY[0] = i;
            }
            else if (p.getY() <= imageCorners[sortedY[1]].getY()) {
                sortedY[1] = i;
            }

            if (p.getX() >= imageCorners[sortedX[3]].getX()) {
                sortedX[2] = sortedX[3];
                sortedX[3] = i;
            }
            else if (p.getX() >= imageCorners[sortedX[2]].getX()) {
                sortedX[2] = i;
            }

            if (p.getY() >= imageCorners[sortedY[3]].getY()) {
                sortedY[2] = sortedY[3];
                sortedY[3] = i;
            }
            else if (p.getY() >= imageCorners[sortedY[2]].getY()) {
                sortedY[2] = i;
            }

        }
    }

    public void zoomIn(Point2D pivot) {
        zoom(1, pivot);
    }

    public void zoomOut(Point2D pivot) {
        zoom(-1, pivot);
    }

    public void zoomIn() {
	zoomIn(new Point2D.Double(containerBounds.getCenterX(), containerBounds.getCenterY()));
    }

    public void zoomOut() {
        zoomOut(new Point2D.Double(containerBounds.getCenterX(), containerBounds.getCenterY()));
    }

    public void zoom(int levels, Point2D pivot) {
        zoomAndCenter(levels, pivot, pivot);
    }

    public void zoomAndCenter(int levels, Point2D pivot, Point2D target) {
        //TODO restart the animatiion if the zoom limit is already reached
        double newScale = targetScale * Math.pow(zoomFactor, levels);
        newScale = Math.max(minScale, newScale);
        newScale = Math.min(maxScale, newScale);

        startPivot_view.setLocation(pivot);
        targetPivot_view.setLocation(target);

        if(newScale < targetScale || newScale > targetScale) {
            targetScale = newScale;
            startAnimation();
        }
    }

    protected void startAnimation() {
	if (animator.isRunning()) {
	    animator.stop();
        }

        zoomAnimator.setTargetScale(targetScale);
        zoomAnimator.setStartPivot(startPivot_view);
        zoomAnimator.setTargetPivot(startPivot_view);
	animator.start();
    }

    public boolean testBoundsInsideMap() {
        Rectangle2D ib = mapView.getImageBounds();
        Point2D mapCorners[] = new Point2D[] {
            new Point2D.Double(0, 0),
            new Point2D.Double(ib.getWidth(), 0),
            new Point2D.Double(ib.getWidth(), ib.getHeight()),
            new Point2D.Double(0, ib.getHeight())
        };

        Point2D bounds[] = new Point2D[] {
            new Point2D.Double(0, 0),
            new Point2D.Double(containerBounds.getWidth(), 0),
            new Point2D.Double(containerBounds.getWidth(), containerBounds.getHeight()),
            new Point2D.Double(0, containerBounds.getHeight())
        };

        transformView.getTransform().transform(mapCorners, 0, mapCorners, 0, 4);
        for(int i = 0; i < 4; i++) {
            boolean boundsInsideMap = GeoShapeCalculations.shapeContains(Arrays.asList(mapCorners), bounds[i]);
            if(boundsInsideMap == false) {
                return false;
            }
        }

        return true;
    }

}
