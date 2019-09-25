/*
 * 
 */

package com.stayprime.view;

import com.stayprime.geo.Coordinates;
import com.stayprime.view.control.Controller;
import com.stayprime.view.control.MousePanControl;
import com.stayprime.view.control.MouseZoomControl;
import com.stayprime.view.control.ViewControl;
import com.stayprime.view.map.MapView;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jdesktop.swingx.JXPanel;

/**
 *
 * @author benjamin
 */
public class Dashboard2 extends JXPanel implements DrawableContainer {
    private static final Logger log = LoggerFactory.getLogger(Dashboard2.class);

    private List<DrawableView> drawableViews;
    private boolean quick = true;
    private boolean dirty = false;

    //Holding just for convenience
    private TransformView transformView;
    private ViewControl viewControl;
    private Controller panControl;
    private Controller zoomControl;

    public Dashboard2() {
	log.debug("Creating Dashboard2");
	drawableViews = new ArrayList<DrawableView>();
    }

    public TransformView getTransformView() {
        return transformView;
    }

    public void setTransformView(TransformView transformView) {
        this.transformView = transformView;
    }

    public ViewControl getViewControl() {
        return viewControl;
    }

    public void setViewControl(ViewControl viewControl) {
        this.viewControl = viewControl;
    }

    public void addDrawableView(DrawableView d) {
	log.debug("Adding drawable view: " + d);
	//d.setContainer(this);
	drawableViews.add(d);
    }

    public void removeDrawableView(DrawableView d) {
        if (drawableViews.remove(d)) {
            log.debug("Removed drawable view: " + d);
        }
    }

    public void setDirty() {
        this.dirty = true;
    }

    public boolean isDirty() {
        return dirty;
    }

    @Override
    public void repaint() {
	repaint(quick);
    }

    public void repaint(boolean quick) {
	this.quick = quick;
	super.repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
	super.paintComponent(g);
        boolean rendered = false;

//	for(Drawable d: drawables) {
//	    d.render((Graphics2D) g, quick);
//            rendered = true;
//	}

        Graphics2D g2d = (Graphics2D) g.create();
        if(quick) {
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        }
        else {
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        }

        for(DrawableView d: drawableViews) {
	    if (d.render(g2d, new AffineTransform(), quick)) {
                rendered = true;
            }
	}
        g2d.dispose();
        dirty = false;

//	if(debug && (invert = !invert)) {
//            g.setXORMode(Color.BLACK);
//            g.setColor(Color.WHITE);
//            g.fillRect(0,0,768,1024);
//	}
    }

    public Point2D toViewPoint(Coordinates coord) {
        if (transformView != null) {
            return transformView.toViewPoint(coord);
        }
        return null;
    }

    public Coordinates fromViewPoint(Point2D point) {
        if (transformView != null) {
            return transformView.fromViewPoint(point);
        }
        return null;
    }

    public Controller getPanControl() {
        return panControl;
    }

    public void setPanControl(MousePanControl panControl) {
        this.panControl = panControl;
    }

    public Controller getZoomControl() {
        return zoomControl;
    }

    public void setZoomControl(MouseZoomControl zoomControl) {
        this.zoomControl = zoomControl;
    }

}
