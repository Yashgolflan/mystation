/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.stayprime.imageview;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Transparency;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

/**
 *
 * @author benjamin
 */
public class CachedImageRenderer implements ImageRenderer {
    private ImageRenderer renderer;
    private BufferedImage cacheImage;
    private Rectangle2D cachedRectangle;
    private Dimension cachedCanvas;
    private Color background;

    public CachedImageRenderer() {
	this(null);
    }

    public CachedImageRenderer(ImageRenderer renderer) {
        this.renderer = renderer == null? new NullRenderer() : renderer;
        cachedRectangle = new Rectangle2D.Double();
        cachedCanvas = new Dimension();
    }

    public final void setImageRenderer(ImageRenderer renderer) {
	this.renderer = renderer == null? new NullRenderer() : renderer;
	cachedRectangle.setRect(0, 0, 0, 0);
    }

    public void renderImage(Graphics g, double scale, Rectangle2D imageRect, Dimension canvasSize, boolean quickDraw) {
        Graphics2D g2d;
        if(quickDraw == false) {
            if(createCacheImage(canvasSize)) {
                updateCachedRectangle(imageRect);
                g2d = cacheImage.createGraphics();

		if(background != null) {
		    g2d.setColor(background);
		    g2d.clearRect(0, 0, cacheImage.getWidth(), cacheImage.getHeight());
		}
                renderer.renderImage(g2d, scale, imageRect, canvasSize, quickDraw);
                g2d.dispose();
            }
            else if(updateCachedRectangle(imageRect)) {
                g2d = cacheImage.createGraphics();

		if(background != null) {
		    g2d.setColor(background);
		    g2d.clearRect(0, 0, cacheImage.getWidth(), cacheImage.getHeight());
		}
                renderer.renderImage(g2d, scale, imageRect, canvasSize, quickDraw);
                g2d.dispose();
            }

	    if(background != null) {
		g.setColor(background);
		g.clearRect(0, 0, canvasSize.width, canvasSize.height);
	    }
	    g.drawImage(cacheImage, 0, 0, cachedCanvas.width, cachedCanvas.height, null);
        }
        else {
	    if(background != null) {
		g.setColor(background);
		g.clearRect(0, 0, canvasSize.width, canvasSize.height);
	    }

            renderer.renderImage(g, scale, imageRect, canvasSize, quickDraw);
        }
    }

    private boolean createCacheImage(Dimension canvasSize) {
        if(canvasSize == null || cachedCanvas.equals(canvasSize)) {
            return false;
        }
        else {
            cachedCanvas = canvasSize;
            cacheImage = GraphicsEnvironment.getLocalGraphicsEnvironment()
                    .getDefaultScreenDevice().getDefaultConfiguration()
                    .createCompatibleImage(cachedCanvas.width, cachedCanvas.height, Transparency.OPAQUE);
            return true;
        }
    }

    private boolean updateCachedRectangle(Rectangle2D imageRect) {
        if(imageRect == null || cachedRectangle.equals(imageRect))
            return false;
        else  {
            cachedRectangle.setRect(imageRect);
            return true;
        }
    }

    public boolean isImageValid() {
        return renderer.isImageValid();
    }

    public int getImageWidth() {
        return renderer.getImageWidth();
    }

    public int getImageHeight() {
        return renderer.getImageHeight();
    }

    public void setImage(BufferedImage image) {
        renderer.setImage(image);
        cachedRectangle.setRect(0, 0, 0, 0);
    }

    public BufferedImage getImage() {
	return renderer.getImage();
    }

    public void setBackground(Color color) {
	this.background = color;
    }

    private class NullRenderer implements ImageRenderer {
	public void setImage(BufferedImage image) {
	}

	public void renderImage(Graphics g, double scale, Rectangle2D imageRect, Dimension canvasSize, boolean quickDraw) {
	}

	public boolean isImageValid() {
	    return false;
	}

	public int getImageWidth() {
	    return 0;
	}

	public int getImageHeight() {
	    return 0;
	}

	public BufferedImage getImage() {
	    return null;
	}

    }
}
