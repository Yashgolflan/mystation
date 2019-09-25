/*
 * 
 */
package com.stayprime.view;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

/**
 * Interface to represent a layer that can be rendered on a Graphics2D object.
 * @author benjamin
 */
public interface DrawableView {
    /**
     * Renders this layer on g2d.
     * @param g2d the Graphics2D object to render this layer on.
     * @param parentTransform the transform applied to g2d by this view's parent.
     * @param quick true if this can be a lower quality render for speed.
     * @return true if anything was actually rendered.
     */
    public boolean render(Graphics2D g2d, AffineTransform parentTransform, boolean quick);
}
