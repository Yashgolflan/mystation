/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.stayprime.basestation2.ui.modules;

import com.stayprime.basestation2.renderers.DashboardCartTrackRenderer;
import com.stayprime.basestation2.renderers.DistanceRenderer;
import com.stayprime.basestation2.renderers.DrawableDistance;
import com.aeben.elementos.mapview.BasicObjectRenderer;
import com.aeben.golfclub.CourseDefinition;
import com.aeben.golfclub.GolfCourseObject;
import com.aeben.golfclub.DrawableGreen;
import com.aeben.golfclub.HoleDefinition;
import com.aeben.elementos.mapview.DrawableObject;
import com.aeben.elementos.mapview.DrawableShapeObject;
import com.aeben.elementos.mapview.MapProjection;
import com.aeben.golfclub.view.Dashboard;
import com.aeben.golfcourse.util.DistanceUnits;
import com.stayprime.basestation2.renderers.DashboardObjectRenderer;
import com.stayprime.geo.Coordinates;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.*;
import javax.swing.Timer;

/**
 *
 * @author benjamin
 */
public class DrawingToolObjectRenderer extends BasicObjectRenderer {

    private GolfCourseObject modifiedObject;
    private Dashboard component;
    private Timer outlineTimer;
    private float outlinePhase = 0;
    private Map<DrawableObject, Rectangle> objectRectangles;
    private CourseDefinition courseFilter;
    private HoleDefinition holeFilter;
    private boolean editingShape = false;
    private boolean editingGridLines = false;
    protected int selectedShapePointIndex = -1;
    protected int gridSystemNumberOfGrids = 8;
    private DashboardCartTrackRenderer trackRenderer;
    private DashboardObjectRenderer objectRenderer;

    public DrawingToolObjectRenderer() {
        objectRectangles = new HashMap<DrawableObject, Rectangle>();

        outlineTimer = new Timer(100, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                outlinePhase = (outlinePhase + 1) % 15;
                if(component != null)
                    component.repaint();
            }
        });
	
	trackRenderer = new DashboardCartTrackRenderer();
    }

    public void setComponent(Dashboard component) {
        this.component = component;
    }

    public void setObjectRenderer(DashboardObjectRenderer objectRenderer) {
        this.objectRenderer = objectRenderer;
    }

    private DistanceUnits rulerUnits = DistanceUnits.Yards;

    public void setRulerUnits(DistanceUnits rulerUnits) {
	this.rulerUnits = rulerUnits;
    }

    public DistanceUnits getRulerUnits() {
	return rulerUnits;
    }

    public void setGridSystemNumberOfGrids(int gridSystemNumberOfGrids) {
        this.gridSystemNumberOfGrids = gridSystemNumberOfGrids;
    }

    public void setEditingObject(GolfCourseObject modifiedObject) {
        this.modifiedObject = modifiedObject;
        if(component != null)
            component.repaint();

//        if(modifiedObject instanceof DrawableShapeObject)
//            outlineTimer.start();
//        else
//            outlineTimer.stop();

	setSelectedShapePointIndex();
    }

    private void setSelectedShapePointIndex() {
        if(modifiedObject instanceof DrawableShapeObject ) {
            DrawableShapeObject object = (DrawableShapeObject) modifiedObject;
	    List<Coordinates> shape = object.getShapeCoordinates();
	    
	    if(object instanceof DrawableGreen && isEditingGridLines()) {
		DrawableGreen drawableGreen = (DrawableGreen) object;
		shape = drawableGreen.gridLines;
	    }
	    
            if(shape != null)
                setSelectedShapePointIndex(object.getShapeCoordinates().size() - 1);
            else
                setSelectedShapePointIndex(-1);
        }
    }

    public GolfCourseObject getEditingObject() {
        return modifiedObject;
    }

    public void setObjectFilter(CourseDefinition courseFilter, HoleDefinition holeFilter) {
        this.courseFilter = courseFilter;
        this.holeFilter = holeFilter;
    }

    public DashboardCartTrackRenderer getTrackRenderer() {
	return trackRenderer;
    }

    @Override
    public void renderObjects(Graphics g, MapProjection p, boolean quickDraw) {
	Graphics2D g2d = (Graphics2D) g.create();
        if(courseObjects != null) {

            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    quickDraw? RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR :
                        RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    quickDraw? RenderingHints.VALUE_ANTIALIAS_OFF :
                        RenderingHints.VALUE_ANTIALIAS_ON);

            for(DrawableObject object: courseObjects) {
                if(object != modifiedObject) {
                    HoleDefinition hole = null;
                    CourseDefinition course = null;
                    if(object instanceof GolfCourseObject) {
                        GolfCourseObject golfCourseObject = (GolfCourseObject) object;

                        if(golfCourseObject.getParentObject() instanceof HoleDefinition) {
                            hole = (HoleDefinition) golfCourseObject.getParentObject();

                            if(hole.getParentObject() instanceof CourseDefinition)
                                course = (CourseDefinition) hole.getParentObject();
                        }
                    }

                    if((courseFilter == null || course == courseFilter)
                            && (holeFilter == null || hole == holeFilter)) {
                        Shape s = renderCourseObject(object, g2d, p);
                        object.setLastDrawnShape(s);
                    }
                    else {
                        object.setLastDrawnShape(null);
                    }
                }
            }

            if(modifiedObject instanceof DrawableObject) {
                DrawableObject object = (DrawableObject) modifiedObject;
                Shape s = renderCourseObject(object, g2d, p);
                object.setLastDrawnShape(s);
            }

        }
	
	distanceRenderer.renderObjects(g, p, quickDraw);
	trackRenderer.renderObjects(g, p, quickDraw);
	g2d.dispose();
    }

    public Shape renderCourseObject(DrawableObject object, Graphics2D g, MapProjection p) {
        if(object == modifiedObject) {
            Shape shape = null;
            if(modifiedObject instanceof DrawableShapeObject) {
                DrawableShapeObject shapeObject = (DrawableShapeObject) modifiedObject;
                shape = renderShapeObject(shapeObject, g, p);

                if(shapeObject instanceof DrawableGreen) {
                    DrawableGreen green = (DrawableGreen) shapeObject;
                    renderGreenGrid(green, shape, g, p);
                    if(green.front != null)
                        renderPoint(green.front, Color.green, g, p);
                    if(green.middle != null)
                        renderPoint(green.middle, Color.green, g, p);
                    if(green.back != null)
                        renderPoint(green.back, Color.green, g, p);
                }
                else {
                    objectRenderer.tryToRenderCartPath(object, g, p);
                }
		
		if(isEditingShape()) {
		    drawEditingPoints(shapeObject.getShapeCoordinates(), selectedShapePointIndex, g, p);
		}

            }
            else {
                shape = super.renderCourseObject(object, g, p);
            }

            return shape;
        }
        else {
            Shape pathShape = objectRenderer.tryToRenderCartPath(object, g, p);
            if (pathShape != null) {
                return pathShape;
            }
            else {
                return super.renderCourseObject(object, g, p);
            }
        }
    }

    private void drawEditingPoints(List<Coordinates> shape, int selectedPoint,
	    Graphics2D g, MapProjection p) {
	
	if(shape != null) {
	    int i = 0;
	    for(Coordinates coord: shape) {
		Point2D point;
		if(selectedPoint == i++)
		    point = renderEditSquare(coord, Color.blue, g, p);
		else
		    point = renderEditSquare(coord, Color.white, g, p);
	    }

	}
    }

    private Point2D renderEditSquare(Coordinates coord, Color color, Graphics2D g2d, MapProjection p) {
        if(coord != null) {
            Point2D point = p.project(coord);
            
	    g2d.setColor(color);
	    Rectangle2D rect = new Rectangle2D.Double(point.getX() - 3, point.getY() - 3, 6, 6);
            g2d.fill(rect);
            g2d.setColor(Color.black);
            g2d.setStroke(new BasicStroke(1f));
            g2d.draw(rect);

            return point;
        }
        return null;
    }

    public void setEditingGridLines(boolean editingGridLines) {
	this.editingGridLines = editingGridLines;
	setSelectedShapePointIndex();
    }

    public boolean isEditingGridLines() {
	return editingGridLines;
    }

    public void renderGreenGrid(DrawableGreen green, Shape greenShape, Graphics2D g2d, MapProjection p) {
	if(green.gridPoints != null) {
	    for(int i = 0; i < green.gridPoints.size(); i++) {
		Coordinates coord = green.gridPoints.get(i);
		renderNumberedPoint(coord, i + 1, Color.yellow, Color.black, Color.black, new Font("Sans", Font.BOLD, 12), new BasicStroke(2.0F), g2d, p);
	    }
	}
	if(green.gridLines != null) {
	    Shape clip = g2d.getClip();
	    g2d.setClip(greenShape);
	    renderShape(green.gridLines, null, Color.white, getStroke(1, 1), false, g2d, p);
	    g2d.setClip(clip);
	    
	    if(isEditingGridLines())
		drawEditingPoints(green.gridLines, selectedShapePointIndex, g2d, p);
	}
//        if(green.gridPoints != null && green.gridPoints.size() > 0 && green.gridPoints.get(0) != null) {
//            g2d.setColor(Color.blue);
//            g2d.setStroke(new BasicStroke(1));
//
//            Point2D middle = p.project(green.gridPoints.get(0));
//
//            //Draw circular center mark
//            Shape center = new Ellipse2D.Double(middle.getX() - 4, middle.getY() - 4, 8, 8);
//            g2d.draw(center);
//            
//            if(green.gridPoints.size() > 1 && green.gridPoints.get(1) != null) {
//                Point2D front = p.project(green.gridPoints.get(1));
//
//                //Draw triangular direction mark
//                Path2D.Double direction = new Path2D.Double();
//                direction.moveTo(-2, -4);
//                direction.lineTo(-2, 4);
//                direction.lineTo(6, 0);
//                direction.closePath();
//                direction.transform(AffineTransform.getRotateInstance(
//			Math.atan2(front.getY() - middle.getY(), front.getX() - middle.getX())));
//                direction.transform(AffineTransform.getTranslateInstance(front.getX(), front.getY()));
//                g2d.draw(direction);
//
//                double angle = Math.atan2((front.getY() - middle.getY()), (front.getX() - middle.getX()));
//                Line2D l = null;
//
//                switch(gridSystemNumberOfGrids) {
//                    case 2:
//                    case 3:
//                    case 4:
//                    case 6:
//                    case 8:
//                        if(gridSystemNumberOfGrids % 2 == 0) { //divide in half
//                            l = DrawingToolCalculations.projectLineInPolygon(middle, angle, greenShape);
//                            if(l != null)
//                                g2d.draw(l);
//                        }
//
//                        //m = -1/m;
//                        angle = angle + Math.PI / 2;
//                        if(gridSystemNumberOfGrids % 3 != 0) { //divide the other half
//                            l = DrawingToolCalculations.projectLineInPolygon(middle, angle, greenShape);
//                            if(l != null)
//                                g2d.draw(l);
//                        }
//
//                        if(gridSystemNumberOfGrids == 3 || gridSystemNumberOfGrids > 4) { //two more divisions
//                            l = DrawingToolCalculations.projectLineInPolygon(front, angle, greenShape);
//                            if(l != null)
//                                g2d.draw(l);
//
//                            Point2D frontMirror = 
//				    new Point2D.Double(2*middle.getX() - front.getX(), 2*middle.getY() - front.getY());
//                            l = DrawingToolCalculations.projectLineInPolygon(frontMirror, angle, greenShape);
//                            if(l != null)
//                                g2d.draw(l);
//                        }
//
//    //                    DrawableShapeObject o = new BasicDrawableShapeObject(
//    //                            green.gridPoints.subList(2, green.gridPoints.size()),
//    //                            true, 1, Color.red, new Color(0x44FFFFFF, true));
//    //                    super.renderShapeObject(o, g2d, map, scale, imageRect, canvasSize);
//                        for(int i = 2; i < green.gridPoints.size(); i++) {
//                            Coordinates coord = green.gridPoints.get(i);
//                            renderNumberedPoint(coord, i - 1, Color.yellow, Color.black, Color.black, new Font("Sans", Font.BOLD, 12), new BasicStroke(2.0F), g2d, p);
//                        }
//
//                }
//            }
//        }
    }

    @Override
    protected Stroke getStroke(DrawableShapeObject object, float scale) {
        if(object == modifiedObject) {
            return new BasicStroke(2/scale, BasicStroke.CAP_ROUND,
                    BasicStroke.JOIN_ROUND, 0, new float[] {10/scale, 5/scale},
                    outlinePhase/scale);
        }
        else {
            return super.getStroke(object, scale);
        }
    }

    @Override
    protected Color getStrokeColor(DrawableShapeObject object) {
        if(object == modifiedObject)
            return Color.black;
        else 
            return super.getStrokeColor(object);
    }

    public Rectangle getStrokeBounds(Shape s, float strokeWidth) {
        int strokeWidht = (int) (strokeWidth + 0.5f);
        Rectangle rect = s.getBounds();
        rect.translate(-strokeWidht, -strokeWidht);
        rect.grow(2*strokeWidht, 2*strokeWidht);
        return rect;
    }

    public boolean isEditingShape() {
        return editingShape;
    }

    public void setEditingShape(boolean editingShape) {
        this.editingShape = editingShape;
	setSelectedShapePointIndex();
    }

    public int getSelectedShapePointIndex() {
        return selectedShapePointIndex;
    }

    public void setSelectedShapePointIndex(int selectedShapePointIndex) {
        this.selectedShapePointIndex = selectedShapePointIndex;
    }

    private Coordinates rulerPoints[];
    private DistanceRenderer distanceRenderer = new DistanceRenderer();
    public void setRulerPoints(Coordinates coord[]) {
	rulerPoints = coord;
	if(rulerPoints != null && rulerPoints.length > 1) {
	    distanceRenderer.setDrawableObjects(Arrays.asList(new DrawableObject[] {
		new DrawableDistance(rulerPoints[0], rulerPoints[1],
			new Font("Sans", Font.PLAIN, 14), rulerUnits)}));
	}
	else
	    distanceRenderer.setDrawableObjects(Collections.EMPTY_LIST);
    }

    public Coordinates[] getRulerPoints() {
	return rulerPoints;
    }

}
