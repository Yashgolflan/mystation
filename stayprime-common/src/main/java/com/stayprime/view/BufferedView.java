/*
 * 
 */
package com.stayprime.view;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import org.apache.commons.lang.ObjectUtils;

/**
 *
 * @author benjamin
 */
public class BufferedView implements DrawableView {
    private DrawableView view;
    private long renderTimer;
    private BufferedImage buffer;
    private DrawableContainer container;
    private Rectangle2D containerBounds;
    private boolean changed = true;
    private boolean rendered = true;
    private boolean quick = true;

    public BufferedView(DrawableView view, DrawableContainer container) {
        this.view = view;
        this.container = container;
    }

    private void checkBufferSize() {
	if(container == null || container.getBounds() == null) {
	    if(buffer != null) {
		buffer.flush();
		buffer = null;
	    }
	}
	else if(ObjectUtils.notEqual(containerBounds, container.getBounds())) {
//	    log.debug("Buffer size changed");
	    containerBounds = container.getBounds();
	    buffer = new BufferedImage(
		    (int) Math.ceil(containerBounds.getWidth()),
		    (int) Math.ceil(containerBounds.getHeight()),
		    BufferedImage.TYPE_INT_RGB);
	}
    }

    public boolean render(Graphics2D g2d, AffineTransform parentTransform, boolean quick) {
	renderTimer = System.currentTimeMillis();
        if(quick == false) {
            checkBufferSize();
            if(changed || this.quick != quick || container.isDirty()) {// || projection.isModified()) {
                Graphics2D bg2d = buffer.createGraphics();
                renderView(bg2d, parentTransform, quick);
                bg2d.dispose();
            }
            g2d.drawImage(buffer, 0, 0, null);
        }
        else {
            renderView(g2d, parentTransform, quick);
        }

	changed = false;
	this.quick = quick;

        return rendered;
    }

    private void renderView(Graphics2D g2d, AffineTransform parentTransform, boolean quick) {
	rendered = view.render(g2d, parentTransform, quick);
    }
    
}
