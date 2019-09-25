/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.stayprime.imageview;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

/**
 *
 * @author benjamin
 */
public class BasicImageRenderer implements ImageRenderer {
    private BufferedImage image;
    private Paint backgroundPaint;

    public BasicImageRenderer() {
    }

    public void setBackgroundPaint(Paint backgroundPaint) {
	this.backgroundPaint = backgroundPaint;
    }

    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }

    public void renderImage(Graphics g, double scale, Rectangle2D imageRect,
            Dimension canvasSize, boolean quickDraw) {
        Graphics2D g2d = (Graphics2D) g;
        
        Object interpolation = g2d.getRenderingHint(RenderingHints.KEY_INTERPOLATION);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                quickDraw? RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR :
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        
        if(getImage() == null) {
	    if(backgroundPaint != null) {
		g2d.setPaint(backgroundPaint);
		g2d.fillRect(0, 0, canvasSize.width, canvasSize.height);
	    }
        }
        else {
            double width = imageRect.getWidth();
            double height = imageRect.getHeight();

	    if(backgroundPaint != null) {
		if(width < canvasSize.getWidth() || height < canvasSize.getHeight()) {
		    g2d.setPaint(backgroundPaint);
		    g2d.fillRect(0, 0, canvasSize.width, canvasSize.height);
		}
            }

            g2d.drawImage(getImage(), (int)imageRect.getX(), (int)imageRect.getY(),
                    (int)width, (int) height, null);
        }

        if(interpolation != null)
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, interpolation);
    }

    public boolean isImageValid() {
        return getImage() != null;
    }

    public int getImageWidth() {
        return getImage().getWidth();
    }

    public int getImageHeight() {
        return getImage().getHeight();
    }
}
