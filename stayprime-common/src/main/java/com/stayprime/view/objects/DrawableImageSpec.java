/*
 * 
 */

package com.stayprime.view.objects;

/**
 *
 * @author benjamin
 */
public class DrawableImageSpec {
    public final float centerX, centerY;
    public final float meterWidth;
    public final float minPixelWidth, maxPixelWidth;

    public DrawableImageSpec(float centerX, float centerY, Float meterWidth, Float minPixelWidth, Float maxPixelWidth) {
	this.minPixelWidth = minPixelWidth;
	this.maxPixelWidth = maxPixelWidth;
	this.meterWidth = meterWidth;
	this.centerX = centerX;
	this.centerY = centerY;
    }
    
}
