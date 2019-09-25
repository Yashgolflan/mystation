/*
 *
 */
package com.stayprime.util.image;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 * @author benjamin
 */
public class ImageUtil {

    public static BufferedImage readImage(File path) {
        if (path == null)
            return null;

        try {
            return ImageIO.read(path);
        }
        catch (IOException ex) {
        }

        return null;
    }
}
