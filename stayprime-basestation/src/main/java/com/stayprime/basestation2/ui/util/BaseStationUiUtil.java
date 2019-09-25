/*
 * 
 */
package com.stayprime.basestation2.ui.util;

import java.awt.Font;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Enumeration;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;
import org.slf4j.Logger;

/**
 *
 * @author benjamin
 */
public class BaseStationUiUtil {
    public static void initializeFontSize(String fontFamily, float multiplier) {
        UIDefaults defaults = UIManager.getDefaults();

        for (Enumeration e = defaults.keys(); e.hasMoreElements();) {
            Object key = e.nextElement();
            Object value = defaults.get(key);

            if (value instanceof Font) {
                Font font = (Font) value;
                int newSize = Math.round(font.getSize() * multiplier);
                if (value instanceof FontUIResource) {
                    defaults.put(key, new FontUIResource(fontFamily, font.getStyle(), newSize));
                }
                else {
                    defaults.put(key, new Font(fontFamily, font.getStyle(), newSize));
                }
            }
        }
    }

    public static void applyFrameFix(Logger log) {
        log.info("Desktop session env variable: " + System.getenv("DESKTOP_SESSION"));
        // http://stackoverflow.com/questions/10572934/java-swing-mouse-pointer-shifted-on-context-menu-when-jframe-is-maximized
        // http://hg.netbeans.org/core-main/rev/409566c2aa65
        if (Arrays.asList("gnome-shell", "mate", "default", "default.desktop").contains(System.getenv("DESKTOP_SESSION"))) {
            log.info("Applying JFrame maximize fix");
            try {
                Class<?> xwm = Class.forName("sun.awt.X11.XWM");
                Field awt_wmgr = xwm.getDeclaredField("awt_wmgr");
                awt_wmgr.setAccessible(true);
                Field other_wm = xwm.getDeclaredField("OTHER_WM");
                other_wm.setAccessible(true);
                if (awt_wmgr.get(null).equals(other_wm.get(null))) {
                    Field metacity_wm = xwm.getDeclaredField("METACITY_WM");
                    metacity_wm.setAccessible(true);
                    awt_wmgr.set(null, metacity_wm.get(null));
                }
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

//    public static BufferedImage getThumbnail(File selection, int maxW, int maxH) {
//        try {
//            ImageInputStream iis = ImageIO.createImageInputStream(selection);
//            Iterator<ImageReader> irs = ImageIO.getImageReaders(iis);
//            if (irs.hasNext()) {
//                ImageReader ir = irs.next();
//                ir.setInput(iis);
//                ImageReadParam param = ir.getDefaultReadParam();
//                if (ir.) ImageUtils.getSizedImagePreview(src, maxH, true)
//                int w = ir.getWidth(0);
//                int h = ir.getHeight(maxH);
//                float ratio = Math.min((float)maxW/w, (float)maxH/h);
//                param.setSourceRenderSize(new Dimension(Math.round(w*ratio), Math.round(h*ratio)));
//                BufferedImage img = ir.read(0, param);
//                return img;
//            }
//        }
//        catch (Exception ex) {
//            ex.printStackTrace();
//        }
//        return null;
//    }

}
