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
public class MousePanControl extends MouseAdapter implements Controller, MouseMotionListener  {
    private final ViewControl zoom;
    private final Point2D translation;
    private Point startPoint;
    private boolean leftMouseButton;
    private boolean enabled = true;

    public MousePanControl(ViewControl zoom) {
        this(zoom, true);
    }

    public MousePanControl(ViewControl zoom, boolean leftMouseButton) {
        this.zoom = zoom;
        translation = new Point2D.Double();
        this.leftMouseButton = leftMouseButton;
    }

    public static void installPanControl(Dashboard2 dashboard2, ViewControl zoom) {
        installPanControl(dashboard2, zoom, true);
    }

    public static void installPanControl(Dashboard2 dashboard2, ViewControl zoom, boolean leftMouseButton) {
        MousePanControl listener = new MousePanControl(zoom, leftMouseButton);
        dashboard2.addMouseListener(listener);
        dashboard2.addMouseMotionListener(listener);
        dashboard2.setPanControl(listener);
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (enabled && startPoint != null) {
            Point2D t = zoom.getTranslation();
            double x = t.getX() + e.getPoint().getX() - startPoint.getX();
            double y = t.getY() + e.getPoint().getY() - startPoint.getY();
            translation.setLocation(x, y);

            if(leftMouseButton && SwingUtilities.isLeftMouseButton(e)) {
                zoom.setTranslation(translation);
                zoom.setTranslationLimits();
            }
            else if(!leftMouseButton && SwingUtilities.isRightMouseButton(e)) {
                zoom.setTranslation(translation);
                zoom.setTranslationLimits();
            }
            zoom.repaintContainer(true);

            startPoint.setLocation(e.getPoint());
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        startPoint = new Point(e.getPoint());
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        startPoint = null;
        zoom.repaintContainer(false);
    }

}
