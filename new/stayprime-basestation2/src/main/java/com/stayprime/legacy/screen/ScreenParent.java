/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.stayprime.legacy.screen;

/**
 *
 * @author benjamin
 */
public interface ScreenParent {
    public boolean showScreen(Screen selectedScreen);
    public void exitScreen(Screen screen);
}
