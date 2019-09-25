/*
 * 
 */
package com.stayprime.basestation2.ui.custom;

import java.awt.Component;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JTable;

/**
 *
 * @author benjamin
 */
public class ItemListComboBoxCellEditor extends DefaultCellEditor {
    private boolean convertToIndex;

    public ItemListComboBoxCellEditor(Object[] items, boolean convertToIndex) {
	super(new JComboBox(items));
	this.convertToIndex = convertToIndex;
	JComboBox comboBox = (JComboBox) getComponent();
	//comboBox.putClientProperty(SubstanceLookAndFeel.COLORIZATION_FACTOR, new Double(1));
	comboBox.setFocusable(false);
    }

    public void setItems(Object[] items) {
	JComboBox comboBox = (JComboBox) getComponent();
	comboBox.setModel(new DefaultComboBoxModel(items));
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
	JComboBox comboBox = (JComboBox) super.getTableCellEditorComponent(table, value, isSelected, row, column);
	if (convertToIndex && value instanceof Integer) {
	    int integer = (Integer) value;
	    if (integer >= 0 && comboBox.getItemCount() > integer) {
		comboBox.setSelectedIndex(integer);
	    }
	}
	return comboBox;
    }

    @Override
    public Object getCellEditorValue() {
	JComboBox comboBox = (JComboBox) getComponent();
	if (convertToIndex) {
	    return comboBox.getSelectedIndex();
	}
	else {
	    return comboBox.getSelectedItem();
	}
    }
    
}
