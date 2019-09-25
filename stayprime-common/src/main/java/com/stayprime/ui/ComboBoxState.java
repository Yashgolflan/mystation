/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.stayprime.ui;

/**
 *
 * @author benjamin
 */
public class ComboBoxState {

    private int selectedIndex;

    public ComboBoxState() {
        super();
    }

    public ComboBoxState(int index) {
        super();

        this.selectedIndex = index;
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
    }

}
