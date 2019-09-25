/*
 * 
 */

package com.stayprime.basestation2.util;

import java.awt.geom.Rectangle2D;

/**
 *
 * @author benjamin
 */
public class HoleGraphicDescription {
    private Rectangle2D mainRect;
    private Rectangle2D activeRect;

    public HoleGraphicDescription(Rectangle2D mainRect, Rectangle2D activeRect) {
	this.mainRect = mainRect;
	this.activeRect = activeRect;
    }

    public Rectangle2D getActiveRect() {
	return activeRect;
    }

    public Rectangle2D getMainRect() {
	return mainRect;
    }

}
