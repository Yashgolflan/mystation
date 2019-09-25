/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.stayprime.ui;

import java.util.Vector;
import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;

/**
 *
 * @author benjamin
 */
public class PersistentComboBox extends JComboBox {

    public PersistentComboBox() {
        super();
    }

    public PersistentComboBox(Vector<?> items) {
        super(items);
    }

    public PersistentComboBox(Object[] items) {
        super(items);
    }

    public PersistentComboBox(ComboBoxModel aModel) {
        super(aModel);
    }


}
