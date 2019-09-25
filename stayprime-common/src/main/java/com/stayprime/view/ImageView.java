/*
 *
 */

package com.stayprime.view;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author benjamin
 */
public class ImageView implements DrawableView {
    private static final Logger log = LoggerFactory.getLogger(ImageView.class);

    private BufferedImage image;
    private Rectangle2D imageBounds;

    private boolean stretch = true;

    private boolean buffered = true;
    private boolean quick = true;

    public ImageView() {
    }

    public void setImage(BufferedImage image) {
	this.image = image;

	if(image != null) {
            imageBounds = new Rectangle2D.Double(0, 0, image.getWidth(), image.getHeight());
	}
        else {
            imageBounds = null;
        }
    }

    public BufferedImage getImage() {
	return image;
    }

    public Rectangle2D getImageBounds() {
        return imageBounds;
    }

    public boolean render(Graphics2D g2d, AffineTransform parentTransform, boolean quick) {
        if(image != null) {
            g2d.drawImage(image, parentTransform, null);
            return true;
        }

        return false;
    }
}
