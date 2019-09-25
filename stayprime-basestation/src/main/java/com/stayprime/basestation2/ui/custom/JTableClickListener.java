/*
 * 
 */
package com.stayprime.basestation2.ui.custom;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JTable;

/**
 *
 * @author benjamin
 */
public abstract class JTableClickListener {
    public static void installClickListener(final JTable table, final JTableClickListener listener) {
        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
		int row = table.rowAtPoint(e.getPoint());
                listener.tableClicked(table, row, table.isRowSelected(row), e);
            }
	};
        table.addMouseListener(mouseAdapter);
    }

    public abstract void tableClicked(JTable table, int row, boolean selected, MouseEvent e);
}
