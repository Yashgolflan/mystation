/*
 * 
 */
package com.stayprime.basestation2.ui.util;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import org.apache.commons.lang.ObjectUtils;

/**
 *
 * @author benjamin
 */
public class ColumnChooserMenu implements ActionListener, Runnable {
    private final TableColumnModel columnModel;
    private final List<TableColumn> allColumns;
    private final List<TableColumn> removedColumns;
    private final JPopupMenu menu;
    private JCheckBoxMenuItem item;

    public ColumnChooserMenu(final JTable table) {
        this.columnModel = table.getColumnModel();
        int columnCount = columnModel.getColumnCount();
        allColumns = new ArrayList<>(columnCount);
        removedColumns = new ArrayList<>();
        menu = new JPopupMenu();
        for (int i = 0; i < columnModel.getColumnCount(); i++) {
            TableColumn column = columnModel.getColumn(i);
            allColumns.add(column);
            JCheckBoxMenuItem checkbox = new JCheckBoxMenuItem(column.getIdentifier().toString());
            checkbox.setSelected(true);
            checkbox.addActionListener(this);
            menu.add(checkbox);
        }
    }

    void updateAndShowMenu(Component invoker, Point p) {
        Component[] items = menu.getComponents();
        removedColumns.clear();
        for (int i = 0; i < allColumns.size(); i++) {
            TableColumn col = allColumns.get(i);
            boolean found = false;
            for (int j = 0; j < columnModel.getColumnCount(); j++) {
                if (ObjectUtils.equals(col, columnModel.getColumn(j))) {
                    found = true;
                    break;
                }
            }
            JCheckBoxMenuItem menuItem = (JCheckBoxMenuItem) items[i];
            menuItem.setSelected(found);
            if (found == false) {
                removedColumns.add(col);
            }
        }
        menu.show(invoker, p.x, p.y);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        item = (JCheckBoxMenuItem) e.getSource();
        //Avoid exception related to dragging column
        SwingUtilities.invokeLater(this);
    }

    @Override
    public void run() {
        int componentIndex = menu.getComponentIndex(item);
        TableColumn col = allColumns.get(componentIndex);
        if (item.isSelected()) {
            if (removedColumns.contains(col)) {
                removedColumns.remove(col);
                int lastIndex = columnModel.getColumnCount();
                int newIndex = Math.min(componentIndex, lastIndex);
                columnModel.addColumn(col);
                columnModel.moveColumn(lastIndex, newIndex);
            }
        }
        else {
            if (removedColumns.contains(col) == false) {
                removedColumns.add(col);
                columnModel.removeColumn(col);
            }
        }
    }
    
}
