/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.stayprime.ui;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JCheckBox;

/**
 *
 * @author benjamin
 */
public class PersistentCheckBox extends JCheckBox {

    public PersistentCheckBox(String text, Icon icon, boolean selected) {
        super(text, icon, selected);
    }

    public PersistentCheckBox(String text, Icon icon) {
        super(text, icon);
    }

    public PersistentCheckBox(String text, boolean selected) {
        super(text, selected);
    }

    public PersistentCheckBox(Action a) {
        super(a);
    }

    public PersistentCheckBox(String text) {
        super(text);
    }

    public PersistentCheckBox(Icon icon, boolean selected) {
        super(icon, selected);
    }

    public PersistentCheckBox(Icon icon) {
        super(icon);
    }

    public PersistentCheckBox() {
        super();
    }

}
