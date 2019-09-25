/*
 * 
 */

package com.stayprime.view;

import java.awt.geom.Rectangle2D;

/**
 *
 * @author benjamin
 */
public interface DrawableContainer {
    public Rectangle2D getBounds();
    public void repaint(boolean quick);
    public boolean isDirty();
    public void setDirty();
}
