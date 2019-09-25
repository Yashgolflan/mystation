/*
 * 
 */
package com.stayprime.view.control;

import com.stayprime.view.Dashboard2;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import javax.swing.SwingUtilities;

/**
 *
 * @author benjamin
 */
public class MouseRotationControl extends MouseAdapter implements MouseMotionListener {
    private final ViewControl zoom;
    private final Point2D translation;
    private Point startPoint;
    private boolean leftMouseButton;

    public MouseRotationControl(ViewControl zoom) {
        this(zoom, false);
    }

    public MouseRotationControl(ViewControl zoom, boolean leftMouseButton) {
        this.zoom = zoom;
        translation = new Point2D.Double();
        this.leftMouseButton = leftMouseButton;
    }

    public static void installRotationControl(Dashboard2 dashboard2, ViewControl zoom) {
        MouseRotationControl.installRotationControl(dashboard2, zoom, false);
    }

    public static void installRotationControl(Dashboard2 dashboard2, ViewControl zoom, boolean leftMouseButton) {
        MouseRotationControl listener = new MouseRotationControl(zoom, leftMouseButton);
        dashboard2.addMouseListener(listener);
        dashboard2.addMouseMotionListener(listener);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (startPoint != null) {
            Point2D t = zoom.getTranslation();
            double x = t.getX() + e.getPoint().getX() - startPoint.getX();
            double y = t.getY() + e.getPoint().getY() - startPoint.getY();
            translation.setLocation(x, y);

            if(leftMouseButton && SwingUtilities.isLeftMouseButton(e)) {
                zoom.setRotation(zoom.getRotation() + (startPoint.getX() - e.getPoint().getX())*0.01);
                zoom.setTranslationLimits();
            }
            else if(!leftMouseButton && SwingUtilities.isRightMouseButton(e)) {
                zoom.setRotation(zoom.getRotation() + (startPoint.getX() - e.getPoint().getX())*0.01);
                zoom.setTranslationLimits();
            }

            zoom.repaintContainer(true);
            startPoint.setLocation(e.getPoint());
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        startPoint = new Point(e.getPoint());
        zoom.setRotationCenter(startPoint);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        startPoint = null;
        zoom.repaintContainer(false);
    }

}
