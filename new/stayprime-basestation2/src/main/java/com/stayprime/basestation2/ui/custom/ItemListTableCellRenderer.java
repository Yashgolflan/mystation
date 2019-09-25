/*
 * 
 */
package com.stayprime.basestation2.ui.custom;

import java.awt.Component;
import java.util.Arrays;
import java.util.List;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Table cell renderer that displays indexed items (most likely strings) 
 * from integer values.
 * 
 * Also looks for a value in the index list and uses that value if it matches 
 * one in the list.
 * 
 * Displays the passed value if not found or the integer is out of range.
 * @author benjamin
 */
public class ItemListTableCellRenderer extends DefaultTableCellRenderer {
    private boolean convertToIndex;
    private List<Object> items;

    public ItemListTableCellRenderer(Object[] items, boolean convertToIndex) {
	this.convertToIndex = convertToIndex;
	this.items = Arrays.asList(items);
    }

    public void setItems(Object[] items) {
	this.items = Arrays.asList(items);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object originalValue, boolean isSelected, boolean hasFocus, int row, int column) {
	Object value = originalValue;
	if (items != null) {
	    if (convertToIndex && value instanceof Integer) {
		int index = (Integer) value;
		if (index >= 0 && items.size() > index)
		    value = items.get(index);
	    }
	    else if (items.contains(value)) {
		value = items.get(items.indexOf(value));
	    }
	}
	return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    }
    
}
