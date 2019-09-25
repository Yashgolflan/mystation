/*
 * 
 */
package com.stayprime.view.golfcourse;

import java.awt.Color;
import java.awt.image.BufferedImage;
import org.jdesktop.swingx.util.GraphicsUtilities;
import org.jdesktop.swingx.image.AbstractFilter;

/**
 *
 * @author benjamin
 */
public class ColorFilter extends AbstractFilter {
    private Color color = Color.white;

    public ColorFilter() {
    }

    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    public BufferedImage filter(BufferedImage src, BufferedImage dst) {
        if (dst == null) {
            dst = createCompatibleDestImage(src, null);
        }

        int width = src.getWidth();
        int height = src.getHeight();

        int[] pixels = new int[width * height];
        GraphicsUtilities.getPixels(src, 0, 0, width, height, pixels);
        mixColor(pixels);
        GraphicsUtilities.setPixels(dst, 0, 0, width, height, pixels);

        return dst;
    }

    private void mixColor(int[] pixels) {
        for (int i = 0; i < pixels.length; i++) {
            int argb = pixels[i];
            pixels[i] = argb & (0xff000000 | color.getRGB());
        }
    }
    
}
