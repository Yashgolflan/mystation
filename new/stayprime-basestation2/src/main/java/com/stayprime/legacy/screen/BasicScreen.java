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
public class BasicScreen implements Screen {
    private String name;
    private ScreenParent parent;
    private Component toolBarComponent;
    private Component screenComponent;

    public BasicScreen(String name) {
        this.name = name;
    }

    public boolean exitScreen() {
        return true;
    }

    public void enterScreen(ScreenParent screen) {
        this.parent = screen;
    }

    public String getName() {
        return name;
    }

    public Component getToolbarComponent() {
        return toolBarComponent;
    }

    public void setToolBarComponent(Component toolBarComponent) {
        this.toolBarComponent = toolBarComponent;
    }

    public Component getScreenComponent() {
        return screenComponent;
    }

    public void setScreenComponent(Component screenComponent) {
        this.screenComponent = screenComponent;
    }
    
}
