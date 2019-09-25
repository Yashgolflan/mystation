/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.aeben.elementos.mapview;

import com.stayprime.geo.BasicMapImage;
import com.stayprime.geo.Coordinates;
import com.stayprime.geo.MapUtils;
import java.awt.Shape;
import java.awt.image.BufferedImage;

/**
 *
 * @author benjamin
 */
public class DrawableMapImage extends BasicMapImage implements DrawableObject {

    private BufferedImage cachedImage;
    public Shape lastDrawnShape;
    public float opacity = 1f;

    public DrawableMapImage(String imagePath, Coordinates topLeft, Coordinates topRight, Coordinates bottomLeft, Coordinates bottomRight) {
	super(imagePath, topLeft, topRight, bottomLeft, bottomRight);
    }

    public DrawableMapImage(BasicMapImage map) {
        super(map);
    }

    @Override
    public Shape getLastDrawnShape() {
	return lastDrawnShape;
    }

    @Override
    public void setLastDrawnShape(Shape s) {
	lastDrawnShape = s;
    }

    public void setCachedImage(BufferedImage cachedImage) {
	this.cachedImage = cachedImage;
    }

    public BufferedImage getCachedImage() {
	return cachedImage;
    }

}
