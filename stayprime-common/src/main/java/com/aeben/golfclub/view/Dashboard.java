/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.aeben.golfclub.view;

import com.stayprime.imageview.ImageView;
import com.aeben.elementos.mapview.MapProjection;
import com.aeben.elementos.mapview.ObjectRenderer;
import com.aeben.golfclub.GolfClub;
import com.stayprime.geo.Coordinates;
import com.aeben.golfclub.HoleDefinition;
import com.aeben.golfclub.HoleDefinitionClient;
import com.stayprime.geo.BasicMapImage;
import com.stayprime.geo.CoordinateCalculations;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.JComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author benjamin
 */
public class Dashboard extends ImageView implements 
        HoleDefinitionClient, MapProjection {
    private static final Logger log = LoggerFactory.getLogger(Dashboard.class);
    
    private BasicMapImage courseMapImage;
    private List<ObjectRenderer> objectRenderers;
    private HoleDefinition holeDefinition;
    private GolfClub golfClub;

    private boolean transientDraw = false;
    private boolean panEnabled = true;
    private float startDragDistance = 10;
    private Point startDragPoint = null;
    private Dimension oldSize;
    private Point2D imageCenterPivot;

    private double maxScaleFactor = Math.pow(1.5, 4);
    private boolean minScaleFillsViewport = true;
//    private boolean useAbsoluteScaleLimits = false;
    private double mapScale;

    private DashboardListener dashboardListener;

    public Dashboard() {
        super(true, true);

        objectRenderers = new ArrayList<ObjectRenderer>();

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                resetMap();
                oldSize = getSize();
                removeComponentListener(this);

                addComponentListener(new ComponentAdapter() {
                    @Override
                    public void componentResized(ComponentEvent e) {
                        if(isImageValid()) {
                            //Rectangle2D rect = getImageRectangle();

                            double newScale;
                            if(getWidth() < oldSize.width || getHeight() < oldSize.height)
                                newScale = Math.min((double)getWidth()/oldSize.width*getScale(), (double)getHeight()/oldSize.height*getScale());
                            else
                                newScale = Math.max((double)getWidth()/oldSize.width*getScale(), (double)getHeight()/oldSize.height*getScale());

                            setScale(newScale);

                            reCenterImage(imageCenterPivot);
                            repaint();
                        }
                        oldSize = getSize();
                    }
                });
            }
        });

    }

    public void setDashboardListener(DashboardListener dashboardListener) {
        this.dashboardListener = dashboardListener;
    }

    @Override
    public void setImageLocation(double x, double y, boolean repaint) {
        super.setImageLocation(x, y, repaint);

        Rectangle2D rect = getImageRectangle();
        imageCenterPivot = new Point2D.Double(
                (float) ((getWidth()/2d - rect.getX())/rect.getWidth()),
                (float) ((getHeight()/2d - rect.getY())/rect.getHeight()));

        if (dashboardListener != null) {
            dashboardListener.viewChanged();
        }
    }

    public void setPanEnabled(boolean panEnabled) {
        this.panEnabled = panEnabled;
    }

    @Override
    protected void startDrag(Point center) {
        if(panEnabled) {
            startDragPoint = center;
        }
    }

    @Override
    protected void endDrag(Point center) {
        if(panEnabled) {
            startDragPoint = null;
            super.endDrag(center);
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
    }

    @Override
    protected void dragging(Point center) {
        if(panEnabled) {
            if(startDragPoint != null) {
                if(center.distance(startDragPoint) > startDragDistance) {
                    super.startDrag(center);
                    startDragPoint = null;
                }
            }

            if(startDragPoint == null) {
                super.dragging(center);
                setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
            }
        }
    }

    public float getStartDragDistance() {
        return startDragDistance;
    }

    public void setStartDragDistance(float startDragDistance) {
        this.startDragDistance = startDragDistance;
    }

    public void zoomInAndCenter(Coordinates coordinates, double scale) {
        if(isImageValid()) {
            Point2D point = convertCoordinatesToMapPoint(coordinates);

            if(scale*getScale() > getMaxScale())
                scale = getMaxScale() / getScale();

            if(Math.abs(scale - 1.0) < 0.000001)
                super.center(point);
            else
                super.zoomInAndCenter(point, scale);
        }
    }

    public void centerCoordinates(Coordinates coordinates) {
        if(isImageValid()) {
            Point2D point = convertCoordinatesToMapPoint(coordinates);
            super.center(point);
        }
    }

    public static Point2D convertMapPointToView(Point2D mapPoint, Rectangle2D imageRect) {
	if(mapPoint instanceof Point2D.Float) {
	    return new Point2D.Float(
		    (float) (mapPoint.getX() + imageRect.getX()),
		    (float) (mapPoint.getY() + imageRect.getY()));
	}
	else {
	    return new Point2D.Double(
		    mapPoint.getX() + imageRect.getX(),
		    mapPoint.getY() + imageRect.getY());
	}

    }

    public Point2D convertMapPointToView(Point2D mapPoint) {
	if(isImageValid())
	    return convertMapPointToView(mapPoint, getImageRectangle());
	else
	    return null;
    }

    public static Point2D convertViewPointToMap(Point2D viewPoint, Rectangle2D imageRect) {
	if(viewPoint instanceof Point2D.Float) {
	    return new Point2D.Float(
		    (float) (viewPoint.getX() - imageRect.getX()),
		    (float) (viewPoint.getY() - imageRect.getY()));
	}
	else {
	    return new Point2D.Double(
		    viewPoint.getX() - imageRect.getX(),
		    viewPoint.getY() - imageRect.getY());
	}
    }

    public Point2D convertViewPointToMap(Point2D viewPoint) {
	if(isImageValid())
	    return convertViewPointToMap(viewPoint, getImageRectangle());
	else
	    return null;
    }

    public static Coordinates convertMapPointToCoordinates(Point2D mapPoint, BasicMapImage courseMap, Rectangle2D imageRect) {
	    Coordinates coord =
		CoordinateCalculations.getCoordinatesFromXYRelativeToMapPixel(mapPoint, courseMap, imageRect);

	    return coord;
    }

    public Coordinates convertMapPointToCoordinates(Point2D mapPoint) {
	if(getMap() != null && isImageValid())
	    return convertMapPointToCoordinates(mapPoint, getMap(), getImageRectangle());
	else
	    return null;
    }

    public static Point2D convertCoordinatesToMapPoint(Coordinates coord, BasicMapImage courseMap, Rectangle2D imageRect) {
	    Point2D.Double mapPoint = CoordinateCalculations.getXYRelativeToMapPixelCoordinates(
		coord, courseMap, imageRect);

	    return mapPoint;
    }

    public Point2D convertCoordinatesToMapPoint(Coordinates coord) {
	if(getMap() != null && isImageValid())
	    return convertCoordinatesToMapPoint(coord, getMap(), getImageRectangle());
	else
	    return null;
    }

    public List<Point2D> toMapPoints(List<Coordinates> shapeCoordinates) {
        if (getMap() != null && isImageValid() && shapeCoordinates != null) {
            List<Point2D> mapPoints = new ArrayList<Point2D>(shapeCoordinates.size());
            for (int i = 0; i < shapeCoordinates.size(); i++) {
                Coordinates coord = shapeCoordinates.get(i);
                mapPoints.add(convertCoordinatesToMapPoint(coord, getMap(), getImageRectangle()));
            }
            return mapPoints;
        }
        else {
            return Collections.EMPTY_LIST;
        }
    }

    public List<Point2D> toViewPoints(List<Point2D> mapPoints) {
        if (mapPoints != null) {
            List<Point2D> viewPoints = new ArrayList<Point2D>(mapPoints.size());
            for (Point2D p : mapPoints) {
                viewPoints.add(convertMapPointToView(p));
            }
            return viewPoints;
        }
        return null;
    }


    @Override
    public void paintComponent(Graphics g) {
        double oldScale = getScale();
        super.paintComponent(g);
        if(getScale() != oldScale) {
            oldSize = getSize();
        }
        
	renderObjects(g, getScale(), getImageRectangle(), getSize(), isTransientDraw());
    }

    public void renderObjects(Graphics g, double scale, Rectangle2D rect, Dimension size, boolean trans) {
        if(getMap() != null && isImageValid()) {
	    for(ObjectRenderer renderer: objectRenderers) {
		renderer.renderObjects(g, this, trans);
	    }
        }
    }

    @Override
    protected void setImageRectangle(Rectangle2D imageRectangle) {
	super.setImageRectangle(imageRectangle);
    }

    @Override
    public double getMinScale() {
//	if(useAbsoluteScaleLimits || isImageValid() == false)
//	    return super.getMinScale();
//	else
	return calculateMinScale();
    }

    public double calculateMinScale() {
        double newMinScale = 1;
        if (isImageValid()) {
            if(minScaleFillsViewport)
                newMinScale = Math.max((double) getWidth()/getImageRenderer().getImageWidth(),
                        (double) getHeight()/getImageRenderer().getImageHeight());
            else
                newMinScale = Math.min((double) getWidth()/getImageRenderer().getImageWidth(),
                        (double) getHeight()/getImageRenderer().getImageHeight());
        }

	return newMinScale;
    }

    @Override
    public double getMaxScale() {
//	if(useAbsoluteScaleLimits)
//	    return super.getMaxScale();
//	else
	    return calculateMaxScale();
    }

    public double calculateMaxScale() {
        double max = getMinScale()*getMaxScaleFactor();
        if(getScale() > max)
            return getScale();
        else
            return getMinScale()*getMaxScaleFactor();
    }

    @Override
    public double getInitialScale() {
        return getMinScale();
    }

    public double getMapScale() {
        return mapScale;
    }


    private void setMapScale() {
        if(courseMapImage != null && courseMapImage.hasFourCornerCoordinates()
                && isImageValid()) {
            double meters = courseMapImage.getTopLeft().metersTo(courseMapImage.getTopRight());
            mapScale = meters/getImageWidth();
        }
    }

    @Override
    public void setImage(BufferedImage image) {
        super.setImage(image);
        setMapScale();
    }

    public void setMap(BasicMapImage map) {
        courseMapImage = map;

	for(ObjectRenderer renderer: objectRenderers) {
	    renderer.reset();
        }

        setMapScale();
    }

    public BasicMapImage getMap() {
        return courseMapImage;
    }

    public JComponent getComponent() {
        return this;
    }

    public void addObjectRenderer(ObjectRenderer renderer) {
        if(renderer != null) {
            objectRenderers.remove(renderer);
            objectRenderers.add(renderer);
            repaint();
        }
    }

    public void removeObjectRenderer(ObjectRenderer renderer) {
        if(objectRenderers.remove(renderer))
            repaint();
    }

    public List getObjectRenderers() {
        return Collections.unmodifiableList(objectRenderers);
    }

    public void setObjectRenderers(List<ObjectRenderer> objectRenderers) {
        this.objectRenderers.clear();

        if(objectRenderers != null) {
            this.objectRenderers.addAll(objectRenderers);
        }

        repaint();
    }

    public GolfClub getGolfClub() {
        return golfClub;
    }
    
    public void setGolfClub(GolfClub golfClub) {
        if(getHoleDefinition() != null) {
            setHoleDefinition(null);
        }

        this.golfClub = golfClub;

        if(golfClub != null) {
            setMap(golfClub.getCourseImage());
        }
        else {
            setMap(null);
        }
        
        repaint();
    }

    public HoleDefinition getHoleDefinition() {
        return holeDefinition;
    }

    public void setHoleDefinition(HoleDefinition holeDefinition) {
        if(getGolfClub() != null)
            setGolfClub(null);

        this.holeDefinition = holeDefinition;

        if(holeDefinition != null)
            setMap(holeDefinition.map);
        else
            setMap(null);
        repaint();
    }

    public double getMaxScaleFactor() {
        return maxScaleFactor;
    }

    public void setMaxScaleFactor(double maxScaleFactor) {
        this.maxScaleFactor = maxScaleFactor;

        if(getScale() > getMinScale()*maxScaleFactor) {
            zoomOut(getScale()/getMinScale()/maxScaleFactor);
        }
    }

    public boolean isMinScaleFillsViewport() {
        return minScaleFillsViewport;
    }

    public void setMinScaleFillsViewport(boolean minScaleFillsViewport) {
        this.minScaleFillsViewport = minScaleFillsViewport;
    }

//Unused property!
//    public boolean isUseAbsoluteScaleLimits() {
//	return useAbsoluteScaleLimits;
//    }
//
//    public void setUseAbsoluteScaleLimits(boolean useAbsoluteScaleLimits) {
//	this.useAbsoluteScaleLimits = useAbsoluteScaleLimits;
//    }

    public void setTransientDraw(boolean b) {
        transientDraw = b;
    }

    @Override
    protected boolean isTransientDraw() {
        return transientDraw || super.isTransientDraw();
    }

    @Override
    public Point2D project(Coordinates coord) {
	return convertMapPointToView(convertCoordinatesToMapPoint(coord));
    }

    @Override
    public Coordinates projectInverse(Point2D point) {
	return convertMapPointToCoordinates(convertViewPointToMap(point));
    }

    @Override
    public double getAngle() {
	return 0;
    }

    public static interface DashboardListener {
        public void pointClicked(Coordinates pointCoordinates);
        public void viewChanged();
    }

}
