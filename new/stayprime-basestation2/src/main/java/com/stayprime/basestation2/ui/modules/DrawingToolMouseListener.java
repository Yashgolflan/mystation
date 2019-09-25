/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.stayprime.basestation2.ui.modules;

import com.aeben.golfclub.view.Dashboard;
import com.stayprime.legacy.Pair;
import com.aeben.golfclub.DrawableCourseShape;
import com.aeben.golfclub.GolfCourseObject;
import com.aeben.golfclub.DrawableGreen;
import com.aeben.golfclub.HoleDefinition;
import com.aeben.golfcourse.util.DistanceUnits;
import com.stayprime.basestation2.renderers.DistanceRenderer;
import com.stayprime.basestation2.util.PinLocationUtil;
import com.stayprime.geo.Coordinates;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.List;
import javax.swing.SwingUtilities;
import org.jhotdraw.geom.Polygon2D;

/**
 *
 * @author benjamin
 */
public class DrawingToolMouseListener implements MouseMotionListener, MouseListener {

    //private DrawableCourseShape hoverObject;
    //private Color originalColor;
    private Dashboard dashboard;
    private DrawingToolInterface drawingTool;
    private int movingPointIndex = -1;
    private int selectingPointIndex = -1;
    private final DrawingToolObjectRenderer objectRenderer;
    private DistanceRenderer distanceRenderer;

    public static enum Operation {NONE, EDIT_SHAPE, MOVE_SHAPE, EDIT_POINT};
    protected Operation operation = Operation.NONE;

    private boolean measurementMode;
    public static final String PROP_MEASUREMENTMODE = "measurementMode";

    private Point startDragPoint;

    Font font = new Font("Droid Sans", Font.PLAIN, 16);

    public DrawingToolMouseListener(DrawingToolInterface drawingTool, DrawingToolObjectRenderer objectRenderer) {
        this.drawingTool = drawingTool;
        this.objectRenderer = objectRenderer;
        distanceRenderer = new DistanceRenderer();
    }

    public DistanceRenderer getDistanceRenderer() {
        return distanceRenderer;
    }

    public void setDashboard(Dashboard dashboard) {
        this.dashboard = dashboard;
    }

    @Override
    public void mouseMoved(MouseEvent e) {
	Cursor cursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
        distanceRenderer.setDrawableObjects(null);
        if(getOperation() != Operation.NONE) {
            if(getOperation() == Operation.EDIT_SHAPE) {
		cursor = Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
		
                if(drawingTool.getEditingObject() instanceof DrawableCourseShape) {
                    DrawableCourseShape shape = (DrawableCourseShape) drawingTool.getEditingObject();
                    Pair<Integer,Float> minDistance = null;

                    if(shape.getShapeCoordinates() != null) {// && shape.lastDrawnShape instanceof Polygon2D.Double)
                        minDistance = getMinDistancePointIndex(shape, e.getPoint());
                        if(minDistance.secondItem < 10f) {
                            cursor = Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR);
                        }
                    }

                    if (shape.getType() == GolfCourseObject.ObjectType.PIN_LOCATIONS) {
                        showPinMeasurements(e, shape);
                    }
                }
            }
	    else if(getOperation() == Operation.MOVE_SHAPE) {
                if(drawingTool.getEditingObject() instanceof DrawableCourseShape) {
		    DrawableCourseShape shape = (DrawableCourseShape) drawingTool.getEditingObject();
		    if(shape.lastDrawnShape instanceof Polygon2D.Double) {
			Polygon2D.Double poly = (Polygon2D.Double) shape.lastDrawnShape;
			if(poly.contains(e.getPoint()))
			    cursor = Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR);
		    }
		}
	    }
	    else if(getOperation() == Operation.EDIT_POINT) {
		cursor = Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
	    }
        }
        
	dashboard.setCursor(cursor);
    }

    private Coordinates rulerPoints[] = new Coordinates[2];
    public void mouseDragged(MouseEvent e) {
	if(isMeasurementMode()) {
	    if(SwingUtilities.isLeftMouseButton(e)) {
		rulerPoints[1] = dashboard.convertMapPointToCoordinates(
			dashboard.convertViewPointToMap(e.getPoint()));
		objectRenderer.setRulerPoints(rulerPoints);
		dashboard.repaint();
	    }
	}
	else if(getOperation() == Operation.EDIT_SHAPE) {
            if(movingPointIndex >= 0)
                setShapePoint((DrawableCourseShape) drawingTool.getEditingObject(), movingPointIndex, e.getPoint());
            else if(startDragPoint != null) {
                if(startDragPoint.distance(e.getPoint()) >= dashboard.getStartDragDistance()) {
                    startDragPoint = null;

                    selectingPointIndex = -1;
                    dashboard.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                }
            }
        }
	else if(getOperation() == Operation.MOVE_SHAPE) {
	    if(drawingTool.getEditingObject() instanceof DrawableCourseShape) {
		DrawableCourseShape shape = (DrawableCourseShape) drawingTool.getEditingObject();
		if(shape.lastDrawnShape instanceof Polygon2D.Double) {
		    Polygon2D.Double poly = (Polygon2D.Double) shape.lastDrawnShape;

		    if(startDragPoint != null) {
			moveShape(startDragPoint, e.getPoint());
			startDragPoint = e.getPoint();
		    }
		}
	    }
	}
        else if(getOperation() == Operation.EDIT_POINT) {
            setObjectPoint(drawingTool.getEditingObject(), e.getPoint(),true);
        }
    }

    public void mouseClicked(MouseEvent e) {
	if(SwingUtilities.isRightMouseButton(e)) {
	    objectRenderer.setRulerPoints(null);
	    dashboard.repaint();
	}
    }

    public void mousePressed(MouseEvent e) {
        movingPointIndex = -1;
        selectingPointIndex = -1;
	startDragPoint = null;

	if(isMeasurementMode()) {
	    if(SwingUtilities.isLeftMouseButton(e)) {
		dashboard.setPanEnabled(false);
		rulerPoints[0] = dashboard.convertMapPointToCoordinates(
			dashboard.convertViewPointToMap(e.getPoint()));
		objectRenderer.setRulerPoints(null);
		dashboard.repaint();
	    }
	}
	else if(getOperation() == Operation.EDIT_SHAPE) {

            if(drawingTool.getEditingObject() instanceof DrawableCourseShape) {
                DrawableCourseShape shape = (DrawableCourseShape) drawingTool.getEditingObject();
                Pair<Integer,Float> minDistance = null;

                if(shape.getShapeCoordinates() != null)// && shape.lastDrawnShape instanceof Polygon2D.Double)
                    minDistance = getMinDistancePointIndex(shape, e.getPoint());

                if(minDistance != null) {
                    if(minDistance.secondItem < 10f) {
                        if(SwingUtilities.isLeftMouseButton(e)) {
                            dashboard.setPanEnabled(false);
                            movingPointIndex = minDistance.firstItem;
                            setShapePoint(shape, movingPointIndex, e.getPoint());
                            dashboard.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                        }
                        else if(SwingUtilities.isRightMouseButton(e)) {
                            drawingTool.deleteShapePoint(shape, minDistance.firstItem);
                        }
                    }
                    else {
                        if(SwingUtilities.isLeftMouseButton(e)) {
                            startDragPoint = e.getPoint();
                            movingPointIndex = -1;
                            dashboard.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
                        }
                        else if(SwingUtilities.isRightMouseButton(e)) {
                            drawingTool.deleteSelectedShapePoint(shape);
                        }
                    }
                }
                else {
		    if(SwingUtilities.isLeftMouseButton(e)) {
			startDragPoint = e.getPoint();
			movingPointIndex = -1;
		    }
		    else if(SwingUtilities.isRightMouseButton(e)) {
			drawingTool.deleteSelectedShapePoint(shape);
		    }
                }
            }
        }
	else if(getOperation() == Operation.MOVE_SHAPE) {
            if(drawingTool.getEditingObject() instanceof DrawableCourseShape) {
                DrawableCourseShape shape = (DrawableCourseShape) drawingTool.getEditingObject();

                if(shape.lastDrawnShape instanceof Polygon2D.Double) {
		    Polygon2D.Double poly = (Polygon2D.Double) shape.lastDrawnShape;

                    if(poly.contains(e.getPoint())) {
			if(SwingUtilities.isLeftMouseButton(e)) {
                            dashboard.setPanEnabled(false);
                            dashboard.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			    startDragPoint = e.getPoint();
                        }
                    }
                }
            }
        }
        else if(getOperation() == Operation.EDIT_POINT && SwingUtilities.isLeftMouseButton(e)) {
            dashboard.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
            dashboard.setPanEnabled(false);
            setObjectPoint(drawingTool.getEditingObject(), e.getPoint(), true);
        }
    }

    public void mouseReleased(MouseEvent e) {
        if(isMeasurementMode()) {
	    if(SwingUtilities.isLeftMouseButton(e)) {
		setMeasurementMode(false);
		dashboard.setPanEnabled(true);
	    }
	}
	else if(getOperation() == Operation.EDIT_SHAPE) {
            if(movingPointIndex >= 0) {
                dashboard.setPanEnabled(true);
                movingPointIndex = -1;
            }
            else if(startDragPoint != null) {
                if(drawingTool.getEditingObject() instanceof DrawableCourseShape) {
                    if(selectingPointIndex >= 0) { //Clicked on an existing point
                        drawingTool.setSelectedShapePointIndex(selectingPointIndex);
                    }
                    else {//if(isAddingPoints()) { //Create a new point on the shape
                        DrawableCourseShape shape = (DrawableCourseShape) drawingTool.getEditingObject();
                        addShapePoint(shape, e.getPoint());
                    }
                }
                startDragPoint = null;
            }
        }
	else if(getOperation() == Operation.MOVE_SHAPE) {
	    dashboard.setPanEnabled(true);
	    startDragPoint = null;
        }
        else if(getOperation() == Operation.EDIT_POINT) {
            dashboard.setPanEnabled(true);
            setObjectPoint(drawingTool.getEditingObject(), e.getPoint(), false);
        }
        dashboard.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    private void addShapePoint(DrawableCourseShape shape, Point2D p) {
        if(dashboard != null && dashboard.getMap() != null && dashboard.getImageRectangle() != null) {
	    Point2D mapPoint = dashboard.convertViewPointToMap(p);
	    Coordinates coord = dashboard.convertMapPointToCoordinates(mapPoint);
//            Coordinates coord =
//                    CoordinateCalculations.getCoordinatesFromXYRelativeToMapPixel(
//                    new Point2D.Double(p.getX() - dashboard.getImageRectangle().getX(), p.getY() - dashboard.getImageRectangle().getY()),
//                    dashboard.getMap(), dashboard.getImageRectangle());

            drawingTool.addShapePoint(shape, coord);
        }
    }

    private void setShapePoint(DrawableCourseShape shape, int pointIndex, Point2D p) {
        if(dashboard != null && dashboard.getMap() != null && dashboard.getImageRectangle() != null) {
	    Point2D mapPoint = dashboard.convertViewPointToMap(p);
	    Coordinates coord = dashboard.convertMapPointToCoordinates(mapPoint);
//            Coordinates coord =
//                    CoordinateCalculations.getCoordinatesFromXYRelativeToMapPixel(
//                    new Point2D.Double(p.getX() - dashboard.getImageRectangle().getX(), p.getY() - dashboard.getImageRectangle().getY()),
//                    dashboard.getMap(), dashboard.getImageRectangle());

            drawingTool.setShapePoint(shape, pointIndex, coord);
        }
    }

    private void setObjectPoint(GolfCourseObject object, Point2D p, boolean isAdjusting) {
        if(dashboard != null && dashboard.getMap() != null && dashboard.getImageRectangle() != null) {
	    Point2D mapPoint = dashboard.convertViewPointToMap(p);
	    Coordinates coord = dashboard.convertMapPointToCoordinates(mapPoint);
//            Coordinates coord =
//                    CoordinateCalculations.getCoordinatesFromXYRelativeToMapPixel(
//                    new Point2D.Double(p.getX() - dashboard.getImageRectangle().getX(), p.getY() - dashboard.getImageRectangle().getY()),
//                    dashboard.getMap(), dashboard.getImageRectangle());

            drawingTool.setObjectPoint(object, coord, isAdjusting);
            dashboard.repaint();
        }
    }

    private void moveShape(Point startPoint, Point endPoint) {
	if(drawingTool.getEditingObject() instanceof DrawableCourseShape) {
	    DrawableCourseShape shape = (DrawableCourseShape) drawingTool.getEditingObject();
	    if(shape.shapeCoordinates != null) {
		for(int i = 0; i < shape.shapeCoordinates.size(); i++) {
		    Coordinates coord = shape.shapeCoordinates.get(i);
		    shape.shapeCoordinates.set(i, calculateNewPointCoordinates(coord, startPoint, endPoint));
		}

		if(shape instanceof DrawableGreen) {
		    DrawableGreen green = (DrawableGreen) shape;
		    if(green.gridPoints != null) {
			for(int i = 0; i < green.gridPoints.size(); i++) {
			    Coordinates coord = green.gridPoints.get(i);
			    green.gridPoints.set(i, calculateNewPointCoordinates(coord, startPoint, endPoint));
			}
		    }
		    if(green.gridLines != null) {
			for(int i = 0; i < green.gridLines.size(); i++) {
			    Coordinates coord = green.gridLines.get(i);
			    green.gridLines.set(i, calculateNewPointCoordinates(coord, startPoint, endPoint));
			}
		    }
		    if(green.front != null)
			green.front = calculateNewPointCoordinates(green.front, startPoint, endPoint);
		    if(green.middle != null)
			green.middle = calculateNewPointCoordinates(green.middle, startPoint, endPoint);
		    if(green.back != null)
			green.back = calculateNewPointCoordinates(green.back, startPoint, endPoint);
		}

		drawingTool.setObjectInformationChanged(true);

		if(dashboard != null)
		    dashboard.repaint();
	    }
	}
    }

    private Coordinates calculateNewPointCoordinates(Coordinates coord, Point startPoint, Point endPoint) {
	Point2D point = dashboard.convertCoordinatesToMapPoint(coord);
	double x = point.getX() + endPoint.x - startPoint.x;
	double y = point.getY() + endPoint.y - startPoint.y;
	point.setLocation(x, y);
	return dashboard.convertMapPointToCoordinates(point);
    }

    public Operation getOperation() {
	return operation;
    }

    public void setOperation(Operation operation) {
	//Operation oldOperation = this.operation;
	this.operation = operation;
        movingPointIndex = -1;
	//firePropertyChange(PROP_OPERATION, oldOperation, operation);
    }

    private Pair<Integer,Float> getMinDistancePointIndex(DrawableCourseShape shape, Point2D p) {
        if(dashboard != null && dashboard.getMap() != null && dashboard.getImageRectangle() != null
                && shape != null && shape.getShapeCoordinates() != null) {
            float minDistance = Float.POSITIVE_INFINITY;
            int minDistanceIndex = 0;
            for(int i = 0; i < shape.getShapeCoordinates().size(); i++) {
                Point2D point = dashboard.convertCoordinatesToMapPoint(shape.getShapeCoordinates().get(i));
//                Point2D point = CoordinateCalculations.getXYRelativeToMapPixelCoordinates(
//                        shape.getShapeCoordinates().get(i), dashboard.getMap(), dashboard.getImageRectangle());
		
                float distance = (float) p.distance(new Point2D.Double(dashboard.getImageRectangle().getX() + point.getX(),
                        dashboard.getImageRectangle().getY() + point.getY()));
                if(distance < minDistance) {
                    minDistanceIndex = i;
                    minDistance = distance;
                }
            }

            return new Pair(minDistanceIndex, minDistance);
        }
        else
            return null;
    }

    public boolean isMeasurementMode() {
	return measurementMode;
    }

    public void setMeasurementMode(boolean measurementMode) {
	boolean oldMeasurementMode = this.measurementMode;
	this.measurementMode = measurementMode;
	propertyChangeSupport.firePropertyChange(PROP_MEASUREMENTMODE, oldMeasurementMode, measurementMode);

	if(oldMeasurementMode == false && measurementMode) {
	    
	}
    }

    private void showPinMeasurements(MouseEvent e, DrawableCourseShape shape) {
        if(dashboard != null && dashboard.getMap() != null && dashboard.getImageRectangle() != null) {
            GolfCourseObject p = shape.getParentObject();
            if(p instanceof DrawableGreen && p.getParentObject() instanceof HoleDefinition) {
                DrawableGreen green = (DrawableGreen) p;
                HoleDefinition hole = (HoleDefinition) shape.getParentObject().getParentObject();

                if(hole != null && green.getShapeCoordinates() != null) {
                    List<Point2D> points = dashboard.toMapPoints(hole.green.getShapeCoordinates());
                    List<Point2D> greenShape = dashboard.toViewPoints(points);
                    List distances = PinLocationUtil.createPinMeasurements(
                            e.getPoint(), hole, greenShape, dashboard, 
                            font, DistanceUnits.Yards);

                    distanceRenderer.setDrawableObjects(distances);
                }
            }
        }
    }

    private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    public void addPropertyChangeListener(PropertyChangeListener listener) {
	propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
	propertyChangeSupport.removePropertyChangeListener(listener);
    }

}
