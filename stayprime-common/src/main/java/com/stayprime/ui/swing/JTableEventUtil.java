/*
 * 
 */
package com.stayprime.ui.swing;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JTable;

/**
 *
 * @author benjamin
 */
public class JTableEventUtil {
    public static void installClickListener(final JTable table, final JTableClickListener listener) {
        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
		int row = table.rowAtPoint(e.getPoint());
		int col = table.columnAtPoint(e.getPoint());
                listener.tableClicked(table, row, col, table.isRowSelected(row), e);
            }
	};
        table.addMouseListener(mouseAdapter);
    }

    public interface JTableClickListener {
        public void tableClicked(JTable table, int row, int col, boolean selected, MouseEvent e);
    }
}
