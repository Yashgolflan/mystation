/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stayprime.basestation2.util;

import java.awt.Window;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

/**
 *Utility class to fix the window maximizing issue on Linux mint Edition
 * @author Omer
 */
public class WindowUtil implements WindowStateListener {

    private String[] OSArray = {"gnome-shell", "mate", "cinnamon", "mint"}; // add more desktop flavours here
    private final String desktop;
    private Field metacity_wm;
    private Field awt_wmgr;
    private boolean applyFix = false;
    
    private static WindowUtil instance = new WindowUtil();
    
    /**
     * Initialize the singleton instance
     * @return 
     */
    public static WindowUtil getInstance() {
        return instance;
    }

    private WindowUtil() {
        
        List<String> linuxDesktops = Arrays.asList(OSArray);
        desktop = System.getenv("DESKTOP_SESSION");
        if (desktop != null && linuxDesktops.contains(desktop.toLowerCase())) {
            try {
                Class<?> xwm = Class.forName("sun.awt.X11.XWM");
                awt_wmgr = xwm.getDeclaredField("awt_wmgr");
                awt_wmgr.setAccessible(true);
                Field other_wm = xwm.getDeclaredField("OTHER_WM");
                other_wm.setAccessible(true);
                if (awt_wmgr.get(null).equals(other_wm.get(null))) {
                    metacity_wm = xwm.getDeclaredField("METACITY_WM");
                    metacity_wm.setAccessible(true);
                    applyFix = true;
                    System.out.println("applied fix.....");
                }
            } catch (Exception ex) {
                //ignore
            }
        }
    }

    @Override
    public void windowStateChanged(WindowEvent e) {
        try {
            awt_wmgr.set(null, metacity_wm.get(null));
        } catch (IllegalArgumentException ex) {
            //ignore
        } catch (IllegalAccessException ex) {
            //ignore
        }
    }

    public void apply(Window w) {
        if (!applyFix) {
            return;
        }
        w.removeWindowStateListener(this);
        w.addWindowStateListener(this);
    }
}
