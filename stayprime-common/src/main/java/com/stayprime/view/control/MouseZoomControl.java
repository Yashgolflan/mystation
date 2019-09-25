/*
 * 
 */
package com.stayprime.view.control;

import com.stayprime.view.Dashboard2;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

/**
 *
 * @author benjamin
 */
public class MouseZoomControl implements Controller, MouseWheelListener {
    private final ViewControl zoom;
    private boolean enabled = true;

    public MouseZoomControl(ViewControl zoom) {
        this.zoom = zoom;
    }

    public static void installZoomControl(Dashboard2 dashboard2, ViewControl zoom)  {
        MouseZoomControl zoomControl = new MouseZoomControl(zoom);
        dashboard2.addMouseWheelListener(zoomControl);
        dashboard2.setZoomControl(zoomControl);
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (enabled) {
            if(e.getWheelRotation() < 0) {
                zoom.zoom(1, e.getPoint());
            }
            else {
                zoom.zoom(-1, e.getPoint());
            }
        }
    }

}
