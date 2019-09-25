/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.stayprime.legacy.screen;

import java.awt.Component;

/**
 *
 * @author benjamin
 */
public interface Screen {
    public void enterScreen(ScreenParent screen);

    public boolean exitScreen();

    public String getName();

    public Component getToolbarComponent();
}
