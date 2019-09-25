/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.stayprime.basestation2.renderers;

import com.aeben.golfclub.CourseDefinition;
import com.aeben.golfclub.GolfCourseObject;
import com.aeben.golfclub.DrawableGreen;
import com.aeben.golfclub.HoleDefinition;
import com.stayprime.imageview.ImageConverter;
import com.aeben.elementos.mapview.BasicObjectRenderer;
import com.aeben.elementos.mapview.DrawableObject;
import com.aeben.elementos.mapview.MapProjection;
import com.aeben.elementos.mapview.Projection;
import com.aeben.golfclub.DrawableCourseShape;
import com.aeben.golfclub.view.Dashboard;
import com.stayprime.geo.Coordinates;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 *
 * @author benjamin
 */
public class DashboardObjectRenderer extends BasicObjectRenderer implements PropertyChangeListener {
    public static final String PROP_RENDER_GREEN_GRID = "renderGreenGrid";
    public static final String PROP_RENDER_GREEN_POINTS = "renderGreenPoints";

    private Component component;
    private CourseDefinition courseFilter;
    private HoleDefinition holeFilter;
    private boolean renderGreenGrid = false;
    private boolean renderGreenPoints = false;
    private Dashboard dashboard;
    private Color cartPathColor = new Color(0f, 0f, 0f, 0.5f);
    private Color cartPathWarningColor = new Color(1f, 1f, 0f, 0.2f);
    private float cartPathWarningWidth = 10f;
    private float cartPathWidth = 8f;
    private boolean renderCartPathWidth;

    public DashboardObjectRenderer() {
	this(null);
    }

    public DashboardObjectRenderer(ImageConverter imageConverter) {
        super(imageConverter);
    }

    public void setComponent(Component component) {
        this.component = component;
    }

    public void setDashboard(Dashboard dashboard) {
        this.dashboard = dashboard;
    }

    public void setRenderCartPathWidth(boolean renderCartPathWidth) {
        this.renderCartPathWidth = renderCartPathWidth;
    }

    public void setCartPathWidth(float cartPathWidth) {
        this.cartPathWidth = cartPathWidth;
    }

    public void setCartPathWarningWidth(float cartPathWarningWidth) {
        this.cartPathWarningWidth = cartPathWarningWidth;
    }

    public void setObjectFilter(CourseDefinition courseFilter, HoleDefinition holeFilter) {
        this.courseFilter = courseFilter;
        this.holeFilter = holeFilter;
    }

    @Override
    public void renderObjects(Graphics g, MapProjection p, boolean quickDraw) {
        if(courseObjects != null) {
            Graphics2D g2d = (Graphics2D) g.create();

            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    quickDraw? RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR :
                        RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    quickDraw? RenderingHints.VALUE_ANTIALIAS_OFF :
                        RenderingHints.VALUE_ANTIALIAS_ON);

            for(DrawableObject object: courseObjects) {
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

            g2d.dispose();
        }
    }

    public Shape renderCourseObject(DrawableObject object, Graphics2D g, MapProjection p) {
        if(object instanceof DrawableGreen) {
            DrawableGreen green = (DrawableGreen) object;
            Shape shape = super.renderCourseObject(object, g, p);

            if(renderGreenGrid && green.gridPoints != null && green.gridPoints.size() > 2)
                renderGreenGrid(green, shape, green.gridPoints.size() - 2, g, p);

            if(renderGreenPoints) {
                if(green.front != null)
                    renderPoint(green.front, Color.green, g, p);
                if(green.middle != null)
                    renderPoint(green.middle, Color.green, g, p);
                if(green.back != null)
                    renderPoint(green.back, Color.green, g, p);
            }

            return shape;
        }

        Shape pathShape = tryToRenderCartPath(object, g, p);
        if (pathShape != null) {
            return pathShape;
        }

        return super.renderCourseObject(object, g, p);
    }

    public Shape tryToRenderCartPath(DrawableObject object, Graphics2D g, MapProjection p) {
        if (object instanceof DrawableCourseShape) {
            DrawableCourseShape shape = (DrawableCourseShape) object;
            if (shape.type == GolfCourseObject.ObjectType.CARTH_PATH) {
                return renderCartPath(shape, g, p, renderCartPathWidth);
            }
        }
        return null;
    }

    public Shape renderCartPath(DrawableCourseShape object, Graphics2D g2d, Projection p, boolean withWarning) {
        float totalScale = (float) (p.getScale() / dashboard.getMapScale());
        if (withWarning) {
            renderShape(object.getShapeCoordinates(), null,
                    cartPathWarningColor, getStroke(cartPathWarningWidth, totalScale),
                    false, g2d, p);
        }
        return renderShape(object.getShapeCoordinates(), null,
                cartPathColor, getStroke(cartPathWidth, totalScale),
                false, g2d, p);
    }

    public void renderGreenGrid(DrawableGreen green, Shape greenShape, int gridSystemNumberOfGrids, Graphics2D g2d, MapProjection p) {
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
//                double angle = Math.atan2(front.getY() - middle.getY(), front.getX() - middle.getX());
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
//                            Point2D frontMirror = new Point2D.Double(2*middle.getX() - front.getX(), 2*middle.getY() - front.getY());
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
//                            renderNumberedPoint(coord, i - 1, null, Color.black, null, new Font("Sans", Font.BOLD, 8), new BasicStroke(2f), g2d, p);
//                        }
//                        for(int i = 2; i < green.gridPoints.size(); i++) {
//                            Coordinates coord = green.gridPoints.get(i);
//                            renderNumberedPoint(coord, i - 1, Color.yellow, null, null, new Font("Sans", Font.BOLD, 8), new BasicStroke(2f), g2d, p);
//                        }
//                        for(int i = 2; i < green.gridPoints.size(); i++) {
//                            Coordinates coord = green.gridPoints.get(i);
//                            renderNumberedPoint(coord, i - 1, null, null, Color.black, new Font("Sans", Font.BOLD, 8), new BasicStroke(2f), g2d, p);
//                        }
//
//                }
//            }
//        }
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if(evt.getPropertyName().equals(PROP_RENDER_GREEN_GRID))
            renderGreenGrid = evt.getNewValue().toString().equals("true");
	else if(evt.getPropertyName().equals(PROP_RENDER_GREEN_POINTS))
            renderGreenPoints = evt.getNewValue().toString().equals("true");
    }

    public void setRenderGreenGrid(boolean renderGreenGrid) {
        this.renderGreenGrid = renderGreenGrid;
    }

    public void setRenderGreenPoints(boolean renderGreenPoints) {
        this.renderGreenPoints = renderGreenPoints;
    }

}
