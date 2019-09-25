/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.stayprime.imageview;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import org.jdesktop.swingx.JXPanel;

/**
 *
 * @author benjamin
 */
public class ImageView extends JXPanel {
    public static final String PROP_IMAGERECTANGLE = "imageRectangle";

    // timer for zoom in and zoom out animation
    protected final Timer animationTimer;
    // our image
    private ImageRenderer mapRenderer;

    //Pixel boundaries of the image for rendering.
    private Rectangle2D imageRectangle;
    //Relative point of the map image, representing the center of the transform
    private Point2D imagePivot;
    //Point on this panel's coordinates, representing the center of the transform
    protected Point2D pivotLocation;
    //Scale state variables
    private double previousScale, scale = 1, targetScale;
    //Relative point of the map image, representing the point to center on screen
    private Point2D startingCenter, targetCenter;

    //Scale limits and modifiers
    private double minScale = Double.MIN_VALUE;
    private double maxScale = Double.MAX_VALUE;
    private double initialScale = 1;
    private double defaultZoom = 1.5;
    //Transient tells the renderers to perform a lower quality/faster rendering
    private boolean transientDraw = false;
    
    private boolean mouseClickPanEnabled;
    private boolean mouseWheelZoomEnabled;
    private MouseListener mouseListener;
    private MouseMotionListener mouseMotionListener;
    private MouseWheelListener mouseWheelListener;

    /**
     * Creates new form MapView with mouse pan and zoom enabled by default.
     */
    public ImageView() {
        this(true, true);
    }

    /**
     * Creates new form MapView specifying pan and zoom enable properties.
     * 
     * Creates the 
     * @param mouseClickPanEnabled Enables or disables the mouse pan.
     * @param mouseWheelZoomEnabled Enables or disables mouse wheel zoom.
     */
    public ImageView(boolean mouseClickPanEnabled, boolean mouseWheelZoomEnabled) {
        initComponents();
        
	// Create mouse listeners that call other methods for zoom and pan.
        mouseListener = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if(SwingUtilities.isLeftMouseButton(e))
                    startDrag(e.getPoint());
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                if(SwingUtilities.isLeftMouseButton(e))
                    endDrag(e.getPoint());
            }
        };
        mouseMotionListener = new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if(SwingUtilities.isLeftMouseButton(e))
                    dragging(e.getPoint());
            }
        };
        mouseWheelListener = new MouseWheelListener() {
            public void mouseWheelMoved(MouseWheelEvent e) {
                if(e.getWheelRotation() < 0) {
                    zoomIn(defaultZoom, e.getPoint());
                }
                else {
                    zoomOut(defaultZoom, e.getPoint());
                }
            }
        };


	// Set the, mouseClickPanEnabled and mouseWheelZoomEnabled properties.
        setMouseClickPanEnabled(mouseClickPanEnabled);
        setMouseWheelZoomEnabled(mouseWheelZoomEnabled);

	// Create animationTimer with 30ms fixed delay and anonymus ActionListener
        animationTimer = new Timer(30, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
		try {
		    //Call stepCenterAnimation and then stepZoomAnimation if imageValid.
		    //If both methods return false, the animation is stopped.
		    if(isImageValid() == false || (stepCenterAnimation() == false && stepZoomAnimation() == false))
			animationTimer.stop();
		}
		catch(Throwable t) {
		    animationTimer.stop();
		}
            }
        });

    }

    /**
     * Moves the centering animation one step forward.
     * Calculates the new image location and calls setImageLocation.
     * @return true if the animation stepped. false if there was no change, meaning the animation has ended.
     */
    private boolean stepCenterAnimation() {
        boolean stepped = false;

	if(targetCenter == null || startingCenter == null)
            stepped = false;
        else {
            Point2D.Double center = new Point2D.Double(
                    (getWidth()/2.0 - imageRectangle.getX()) / imageRectangle.getWidth(),
                    (getHeight()/2.0 - imageRectangle.getY()) / imageRectangle.getHeight());

            if(Math.abs(targetCenter.getX() - center.x) < 0.5/imageRectangle.getWidth()
                    && Math.abs(targetCenter.getX() - center.x) < 0.5/imageRectangle.getHeight()) {
                if(transientDraw) {
                    transientDraw = false;
                    repaint();
                }
                stepped = false;
            }
            else {
                double dx = (targetCenter.getX() - startingCenter.getX()) / 10;
                double dy = (targetCenter.getY() - startingCenter.getY()) / 10;
                double newX = center.getX() + dx;
                double newY = center.getY() + dy;

                if(Math.abs(targetCenter.getX() - newX) >
                        Math.abs(targetCenter.getX() - center.getX()))
                    newX = targetCenter.getX();
                if(Math.abs(targetCenter.getY() - newY) >
                        Math.abs(targetCenter.getY() - center.getY()))
                    newY = targetCenter.getY();

                transientDraw = true;
                setImageLocation(-(newX * imageRectangle.getWidth() - getWidth()/2.0),
                        -(newY * imageRectangle.getHeight() - getHeight()/2.0));

                double centerX = (getWidth()/2.0 - imageRectangle.getX()) / imageRectangle.getWidth();
                double centerY = (getHeight()/2.0 - imageRectangle.getY()) / imageRectangle.getHeight();

                stepped = Math.abs(centerX - newX) < 0.000001
                        || Math.abs(centerY - newY) < 0.000001;
            }
        }

        if(stepped) {
            pivotLocation = null;
            imagePivot = null;
        }
        else {
            startingCenter = null;
            targetCenter = null;
        }

        return stepped;
    }

    /**
     * Moves the zoom animation one step forward.
     * Calculates the new image location and scale and calls setImageLocation and setScale.
     * @return true if the animation stepped. false if there was no change, meaning the animation has ended.
     */
    private boolean stepZoomAnimation() {
	//Only start if pivotLocation and imagePivot are set
        if(pivotLocation == null || imagePivot == null)
            return false;

	//Avoid to keep going one way if the scale direction has changed
        double newScale = getScale();
        if(newScale > previousScale && newScale > targetScale)
            previousScale = newScale;
        if(newScale < previousScale && newScale < targetScale)
            previousScale = newScale;

        //Do not allow to go under or over scale limits
        if(targetScale < getMinScale())
            targetScale = getMinScale();
        else if(targetScale > getMaxScale())
            targetScale = getMaxScale();

        //If we have not reached the target scale, calculate the new scale
        if(Math.abs(newScale - targetScale) > 0.000001) {
            if(newScale < targetScale) {
                newScale += (targetScale - previousScale) / 10;
                if(newScale >= targetScale)
                    newScale = targetScale;
            }
            else {
                newScale -= (previousScale - targetScale) / 10;
                if(newScale <= targetScale)
                    newScale = targetScale;
            }

	    transientDraw = true;
            setScale(newScale);
	    setImageLocation(pivotLocation.getX() - imagePivot.getX() * newScale * getImageWidth(),
		    pivotLocation.getY() - imagePivot.getY() * newScale * getImageHeight());

	    return true;
        }
        else {
            if(transientDraw) {
                transientDraw = false;
                repaint();
            }
            previousScale = newScale;
            return false;
        }
    }

    /**
     * Called by the mouse listener to start pan operation on mouse dragging.
     * Set the pivotLocation and calculate imagePivot.
     * @param center the pivotLocation to start the panning.
     */
    protected void startDrag(Point center) {
        if(isImageValid()) {
            pivotLocation = center;
            imagePivot = new Point2D.Double((pivotLocation.getX() - getImageRectangle().getX()) / (getScale() * getImageWidth()),
                (pivotLocation.getY() - getImageRectangle().getY()) / (getScale() * getImageHeight()));
        }
    }

    /**
     * Called by the mouse listener to stop pan operation on mouse release.
     * @param center the point where dragging stopped.
     */
    protected void endDrag(Point center) {
        transientDraw = false;
        repaint();
    }

    /**
     * Called by the mouse listener to pan the image on mouse drag.
     * Set the pivotLocation and calculate imagePivot.
     * @param center the point where dragging stopped.
     */
    protected void dragging(Point center) {
        if(isImageValid()) {
            pivotLocation = center;
            if (pivotLocation == null || imagePivot == null)
                return;
            
            setImageLocation(pivotLocation.getX() - imagePivot.getX() * getScale() * getImageWidth(),
                pivotLocation.getY() - imagePivot.getY() * getScale() * getImageHeight());
            transientDraw = true;
            repaint();
        }
    }

    /**
     * Set the mapRenderer image.
     * @param image BufferedImage to pass to the image renderer.
     */
    public void setImage(BufferedImage image) {
        if(mapRenderer != null)
            mapRenderer.setImage(image);
        resetMap();
    }

    /**
     * Set the ImageRenderer object.
     * @param renderer new ImageRenderer object.
     */
    public void setImageRenderer(ImageRenderer renderer) {
        mapRenderer = renderer;
        resetMap();
    }

    /**
     * Get the current ImageRenderer object.
     * @return current ImageRenderer object.
     */
    public ImageRenderer getImageRenderer() {
        return mapRenderer;
    }

    /**
     * Resets the image rectangle and sets the initial scale.
     * Target scale and previous scale variables (should have methods for this).
     */
    public void resetMap() {
        setImageRectangle(null);

        if(isImageValid()) {
            setImageRectangle(new Rectangle2D.Double());
            setScale(getInitialScale());
            targetScale = previousScale = getScale();
            calculateImageRectangleSize();
            centerImage();
        }

        repaint();
    }

    private void calculateImageRectangleSize() {
        setImageRectangle(imageRectangle.getX(), imageRectangle.getY(),
                getImageWidth()*getScale(), getImageHeight()*getScale());
    }

    protected boolean isTransientDraw() {
        return transientDraw;
    }

    public void centerImage() {
        reCenterImage(new Point2D.Double(.5, .5));
    }

    public void reCenterImage(Point2D imagePivot) {
        Point2D viewPivot = new Point2D.Double(getWidth()/2d, getHeight()/2d);

        setImageLocation(viewPivot.getX() - imagePivot.getX() * getScale() * getImageWidth(),
                viewPivot.getY() - imagePivot.getY() * getScale() * getImageHeight(), false);
    }

    public int getImageWidth() {
        return mapRenderer.getImageWidth();
    }

    public int getImageHeight() {
        return mapRenderer.getImageHeight();
    }

    public boolean isImageValid() {
        return mapRenderer != null && mapRenderer.isImageValid();
    }

    public Rectangle2D getImageRectangle() {
        return imageRectangle;
    }

    public void setImageLocation(Point2D imageLocation) {
        setImageLocation(imageLocation, true);
    }

    public void setImageLocation(Point2D imageLocation, boolean repaint) {
        setImageLocation(imageLocation.getX(), imageLocation.getY(), repaint);
    }

    public void setImageLocation(double x, double y) {
        setImageLocation(x, y, true);
    }

    /**
     * Set the image location and rectify if it's out of boundaries.
     * @param x
     * @param y
     * @param repaint 
     */
    public void setImageLocation(double x, double y, boolean repaint) {
        if(!isImageValid())
            return;

        int imageWidth = (int) imageRectangle.getWidth(),
            imageHeight = (int) imageRectangle.getHeight();
        double originX = x,
                originY = y;

        if(imageWidth < this.getWidth())
            originX = (this.getWidth() - imageWidth)/2;
        else if(originX > 0)
            originX = 0;
        else if(originX+imageWidth < this.getWidth())
            originX = this.getWidth() - imageWidth;

        if(imageHeight < this.getHeight())
            originY = (this.getHeight() - imageHeight)/2;
        else if(originY > 0)
            originY = 0;
        else if(originY+imageHeight < this.getHeight())
            originY = this.getHeight() - imageHeight;

        Rectangle2D rect = new Rectangle2D.Double();
        rect.setRect(originX, originY,
                imageRectangle.getWidth(), imageRectangle.getHeight());

        if(rect.equals(imageRectangle) == false) {
            setImageRectangle(rect);
            if(repaint)
                repaint();
        }
    }

    protected void setImageRectangle(double x, double y, double w, double h) {
	setImageRectangle(new Rectangle2D.Double(x, y, w, h));
    }

    protected void setImageRectangle(Rectangle2D imageRectangle) {
	Rectangle2D oldImageRectangle = this.imageRectangle;
	this.imageRectangle = imageRectangle;
        firePropertyChange(PROP_IMAGERECTANGLE, oldImageRectangle, imageRectangle);
    }

    public double getScale() {
        return scale;
    }

    public void setScale(double scale) {
        if(scale < getMinScale())
            this.scale = getMinScale();
        else if(scale > getMaxScale())
            this.scale = getMaxScale();
        else
            this.scale = scale;

        if(isImageValid()) {
            calculateImageRectangleSize();
        }
    }

    public double getInitialScale() {
        return initialScale;
    }

    public void setInitialScale(double scale) {
        initialScale = scale;
    }

    public double getMinScale() {
        return minScale;
    }

    public void setMinScale(double scale) {
        minScale = scale;
    }

    public double getMaxScale() {
        return maxScale;
    }

    public void setMaxScale(double scale) {
        maxScale = scale;
    }

    public final void setMouseClickPanEnabled(boolean mouseClickPanEnabled) {
        this.mouseClickPanEnabled = mouseClickPanEnabled;
        if(mouseClickPanEnabled) {
            addMouseListener(mouseListener);
            addMouseMotionListener(mouseMotionListener);
        }
        else {
            removeMouseListener(mouseListener);
            removeMouseMotionListener(mouseMotionListener);
        }
    }

    public final boolean isMouseClickPanEnabled() {
        return mouseClickPanEnabled;
    }

    public final void setMouseWheelZoomEnabled(boolean mouseWheelZoomEnabled) {
        this.mouseWheelZoomEnabled = mouseWheelZoomEnabled;
        if(mouseWheelZoomEnabled)
            addMouseWheelListener(mouseWheelListener);
        else
            removeMouseWheelListener(mouseWheelListener);
    }

    public final boolean isMouseWheelZoomEnabled() {
        return mouseWheelZoomEnabled;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
	renderImage(g);
    }

    protected void renderImage(Graphics g) {
        if(isImageValid()) {
            if(getScale() > getMaxScale() || getScale() < getMinScale()) {
                Rectangle2D rect = getImageRectangle();
                Point2D centerImagePivot = new Point2D.Double(
                        (float) ((getWidth()/2d - rect.getX())/rect.getWidth()),
                        (float) ((getHeight()/2d - rect.getY())/rect.getHeight()));
                setScale(getScale());
                reCenterImage(centerImagePivot);
            }
            else {
//                setImageLocation(imageRectangle.getX(), imageRectangle.getY(), false);
            }
        }

	renderImage((Graphics2D) g, scale, getImageRectangle(), getSize(), isTransientDraw());
    }

    public void renderImage(Graphics2D g2d, double scale, Rectangle2D imageRect, Dimension size, boolean trans) {
        if(mapRenderer != null)
            mapRenderer.renderImage(g2d, scale, imageRect, size, trans);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {

        setBackground(java.awt.Color.darkGray);
        setLayout(new java.awt.BorderLayout());
    }// </editor-fold>

    public void zoomIn() {
        zoomIn(defaultZoom, new Point(getWidth()/2, getHeight()/2));
    }

    public void zoomIn(double i) {
        zoomIn(i, new Point(getWidth()/2, getHeight()/2));
    }

    public void zoomIn(double i, Point2D centerPoint) {
        if(!isImageValid())
            return;

        this.pivotLocation = centerPoint;
        imagePivot = new Point2D.Double((pivotLocation.getX() - getImageRectangle().getX()) / (getScale() * getImageWidth()),
                (pivotLocation.getY() - getImageRectangle().getY()) / (getScale() * getImageHeight()));

        targetScale *= i;

        animationTimer.start();
    }

    public void zoomInAndCenter(Point2D coord, double zoom) {
        Point point = new Point((int)(coord.getX() + getImageRectangle().getX()), (int)(coord.getY() + getImageRectangle().getY())),
                point2 = new Point((int) (getWidth()/2 - (getWidth()/2-point.getX())*zoom/(zoom - 1)),
                (int) (getHeight() / 2 - (getHeight() / 2 - point.getY()) * zoom / (zoom - 1)));

        if(Math.abs(zoom - 1.0) < 0.000001)
            center(coord);
        else
            zoomIn(zoom, point2);
    }

    public void zoomInAndCenterViewPoint(Point2D point, double zoom) {
        if(isImageValid()) {
            Point2D point2 =
                    new Point2D.Float(
                    (float) (point.getX() - getImageRectangle().getX()),
                    (float) (point.getY() - getImageRectangle().getY()));

            if(zoom*getScale() > getMaxScale())
                zoom = getMaxScale() / getScale();

            zoomInAndCenter(point2, zoom);
        }
    }

    public void zoomOut() {
        zoomOut(defaultZoom, new Point(getWidth()/2, getHeight()/2));
    }

    public void zoomOut(double i) {
        zoomOut(i, new Point(getWidth()/2, getHeight()/2));
    }

    public void zoomOut(double i, Point2D centerPoint) {
        if(!isImageValid())
            return;
        
        this.pivotLocation = centerPoint;
        imagePivot = new Point2D.Double((pivotLocation.getX() - getImageRectangle().getX()) / (getScale() * getImageWidth()),
                (pivotLocation.getY() - getImageRectangle().getY()) / (getScale() * getImageHeight()));
        
        targetScale /= i;
        
        animationTimer.start();
    }

    public void center(Point2D center) {
        startingCenter = new Point2D.Double(
                (getWidth()/2.0 - imageRectangle.getX()) / imageRectangle.getWidth(),
                (getHeight()/2.0 - imageRectangle.getY()) / imageRectangle.getHeight());
        targetCenter = new Point2D.Double(
                center.getX() / imageRectangle.getWidth(),
                center.getY() / imageRectangle.getHeight());
        animationTimer.start();
    }
}
