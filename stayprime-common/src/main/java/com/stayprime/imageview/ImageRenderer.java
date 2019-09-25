/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.stayprime.imageview;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

/**
 *
 * @author benjamin
 */
public interface ImageRenderer {

    public void setImage(BufferedImage image);
    public void renderImage(Graphics g, double scale, Rectangle2D imageRect,
            Dimension canvasSize, boolean quickDraw);
    public boolean isImageValid();
    public int getImageWidth();
    public int getImageHeight();

    public BufferedImage getImage();
}
