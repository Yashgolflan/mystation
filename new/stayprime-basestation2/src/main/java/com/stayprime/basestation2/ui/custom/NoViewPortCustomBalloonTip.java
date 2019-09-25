/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.stayprime.basestation2.ui.custom;

import java.awt.Point;
import java.awt.Rectangle;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import net.java.balloontip.CustomBalloonTip;
import net.java.balloontip.styles.BalloonTipStyle;

/**
 *
 * @author benjamin
 */
public class NoViewPortCustomBalloonTip extends CustomBalloonTip {
    private final JLabel label;

    public NoViewPortCustomBalloonTip(JComponent attachedComponent, JLabel label, Rectangle offset, BalloonTipStyle style, Orientation alignment, AttachLocation attachLocation, int horizontalOffset, int verticalOffset, boolean useCloseButton) {
	super(attachedComponent, label, offset, style, alignment, attachLocation, horizontalOffset, verticalOffset, useCloseButton);
        this.label = label;
    }

    @Override
    public void refreshLocation() {
	Point location = SwingUtilities.convertPoint(attachedComponent, getLocation(), this);
	try {
            Rectangle offset = getOffset();
	    Rectangle attached = (offset != null) ? new Rectangle(location.x + offset.x, location.y + offset.y, offset.width, offset.height)
		    : new Rectangle(location.x, location.y, attachedComponent.getWidth(), attachedComponent.getHeight());
	    positioner.determineAndSetLocation(attached);

	    // Determine whether the balloon's tip still is visible in the viewport
	    JComponent viewport = getAttachedComponent();
	    if (viewport != null && viewport.isShowing()) {
		Rectangle view = new Rectangle(SwingUtilities.convertPoint(viewport, viewport.getLocation(), getTopLevelContainer()), viewport.getSize());
		Point tipLocation = positioner.getTipLocation();
		if (tipLocation.y >= view.y - 1 // -1 because we still want to allow balloons that are attached to the very top...
			&& tipLocation.y <= (view.y + view.height)
			&& (tipLocation.x) >= view.x
			&& (tipLocation.x) <= (view.x + view.width)) {
		    visibilityControl.setCriterionAndUpdate("withinViewport", true);
		}
		else {
		    visibilityControl.setCriterionAndUpdate("withinViewport", false);
		}
	    }
	}
	catch (NullPointerException exc) {
	}
    }

    public JComponent getContents() {
	return contents;
    }

    public void setLabelText(String text) {
        label.setText(text);
    }
}
