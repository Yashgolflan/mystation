/*
 * 
 */

package com.stayprime.ui;

import java.awt.Dimension;
import org.jdesktop.swingx.JXPanel;

/**
 *
 * @author benjamin
 */
public class ProportionalPanel extends JXPanel {
    private int proportionalWidth;
    public static final String PROP_PROPORTIONALWIDTH = "proportionalWidth";

    private int proportionalHeight;
    public static final String PROP_PROPORTIONALHEIGHT = "proportionalHeight";

    private boolean growAllowed = true;
    public static final String PROP_GROWALLOWED = "growAllowed";

    private boolean horizontal;

    public ProportionalPanel() {
	this(1, 1);
    }

    public ProportionalPanel(int width, int height) {
	this(width, height, true);
    }

    public ProportionalPanel(int width, int height, boolean horizontal) {
	this.proportionalWidth = width;
	this.proportionalHeight = height;
	this.horizontal = horizontal;
    }

    @Override
    public Dimension getPreferredSize() {
	Dimension d = getSize(), p;

	if(horizontal) {
	    if(d.getSize().width == 0)
		p = new Dimension(proportionalWidth, proportionalHeight);
	    else if(d.getSize().width <= proportionalWidth || isGrowAllowed())
		p = new Dimension(d.width, d.width*proportionalHeight/proportionalWidth);
	    else
		p = new Dimension(proportionalWidth, proportionalHeight);
	}
	else {
	    if(d.getSize().height == 0)
		p = new Dimension(proportionalWidth, proportionalHeight);
	    else if(d.getSize().height <= proportionalHeight || isGrowAllowed())
		p = new Dimension(d.height*proportionalWidth/proportionalHeight, d.height);
	    else
		p = new Dimension(proportionalWidth, proportionalHeight);
	}
	
	return p;
    }

    @Override
    public Dimension getMaximumSize() {
	return getPreferredSize();
    }

    public int getProportionalWidth() {
	return proportionalWidth;
    }

    public void setProportionalWidth(int proportionalWidth) {
	int oldProportionalWidth = this.proportionalWidth;
	this.proportionalWidth = proportionalWidth;
	firePropertyChange(PROP_PROPORTIONALWIDTH, oldProportionalWidth, proportionalWidth);
    }

    public int getProportionalHeight() {
	return proportionalHeight;
    }

    public void setProportionalHeight(int proportionalHeight) {
	int oldProportionalHeight = this.proportionalHeight;
	this.proportionalHeight = proportionalHeight;
	firePropertyChange(PROP_PROPORTIONALHEIGHT, oldProportionalHeight, proportionalHeight);
    }

    public boolean isGrowAllowed() {
	return growAllowed;
    }

    public void setGrowAllowed(boolean growAllowed) {
	boolean oldGrowAllowed = this.growAllowed;
	this.growAllowed = growAllowed;
	firePropertyChange(PROP_GROWALLOWED, oldGrowAllowed, growAllowed);
    }

}