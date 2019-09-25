/*
 * 
 */
package com.stayprime.basestation2.ui.custom;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPopupMenu;
import javax.swing.JTable;

/**
 *
 * @author benjamin
 */
public class JTableMenuListener extends MouseAdapter {
    private boolean selectCell;
    private JPopupMenu menu;
    private ActionListener doubleClickAction;

    public JTableMenuListener(JPopupMenu menu) {
        this(menu, true);
    }

    public JTableMenuListener(JPopupMenu menu, boolean selectCell) {
        this.menu = menu;
        this.selectCell = selectCell;
    }

    public void setDoubleClickAction(ActionListener doubleClickAction) {
        this.doubleClickAction = doubleClickAction;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        mouseReleased(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.isPopupTrigger()) {
            JTable source = (JTable) e.getSource();
            int row = source.rowAtPoint(e.getPoint());
            if (row >= 0) {
                int column = source.columnAtPoint(e.getPoint());
                if (selectCell && !source.isRowSelected(row)) {
                    source.changeSelection(row, column, false, false);
                }
                menu.show(e.getComponent(), e.getX(), e.getY());
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (doubleClickAction != null && e.getClickCount() == 2) {
            doubleClickAction.actionPerformed(new ActionEvent(e.getSource(), ActionEvent.ACTION_PERFORMED, "Double click"));
        }
    }

}
