/*
 * 
 */
package com.stayprime.ui.swing;

import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

/**
 *
 * @author benjamin
 */
public class IconCache {
    private Map<String, ImageIcon> loadedIcons;
    private int maxHeight = 0;
    private String basePath = "";

    public IconCache() {
        loadedIcons = new HashMap<String, ImageIcon>();
    }

    public void setMaxHeight(int maxHeight) {
        this.maxHeight = maxHeight;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }
    
    public ImageIcon getIcon(String name) {
        if (loadedIcons.containsKey(name)) {
            return loadedIcons.get(name);
        }
        else {
            ImageIcon icon = loadIcon(name);
            loadedIcons.put(name, icon);
            return icon;
        }
    }

    private ImageIcon loadIcon(String name) {
        try {
            BufferedImage original = ImageIO.read(new File(basePath + name));
            ImageIcon icon = new ImageIcon();
            if (maxHeight > 0 && original.getHeight() > maxHeight) {
                int w = Math.round(((float) maxHeight) / icon.getIconHeight() * icon.getIconWidth() );
                BufferedImage scaled = new BufferedImage(w, maxHeight, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = scaled.createGraphics();
                g.drawImage(original, 0, 0, w, maxHeight, null);
                g.dispose();
                icon.setImage(scaled);
            }
            else {
                icon.setImage(original);
            }
            return icon;
        }
        catch (Exception ex) {
            return null;
        }
    }

}
