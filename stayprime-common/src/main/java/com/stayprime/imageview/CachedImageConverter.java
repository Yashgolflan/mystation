/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.stayprime.imageview;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;

/**
 *
 * @author benjamin
 */
public class CachedImageConverter implements ImageConverter {
    private Map<String,BufferedImage> images;

    public CachedImageConverter() {
        images = new HashMap<String, BufferedImage>();
    }

    public BufferedImage convertImage(Object imageDescriptor) {
        if(images.containsKey(imageDescriptor.toString()))
            return images.get(imageDescriptor.toString());
        else {
            java.net.URL url = Thread.currentThread().getContextClassLoader().getResource(imageDescriptor.toString());
            if (url != null) {
                try {
                    BufferedImage image = ImageIO.read(url);
                    images.put(imageDescriptor.toString(), image);
                    return image;
                }
                catch (IOException ex) {
                }
            }
            return null;
        }
    }
}

