/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.stayprime.ui;

/**
 *
 * @author benjamin
 */
public class ToggleButtonState {

    private boolean selected;

    public ToggleButtonState() {
        super();
    }

    public ToggleButtonState(boolean selected) {
        super();

        this.selected = selected;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }


}
