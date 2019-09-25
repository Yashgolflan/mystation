/*
 * 
 */
package com.stayprime.basestation2.ui.util;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import org.apache.commons.lang.ArrayUtils;
import org.jdesktop.application.session.TableProperty;
import org.jdesktop.application.session.TableState;

/**
 *
 * @author benjamin
 */
public class TableUtil {

    public static void installColumnChooserMenu(final JTable table) {
        final ColumnChooserMenu c = new ColumnChooserMenu(table);
        final JTableHeader header = table.getTableHeader();

        header.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    c.updateAndShowMenu(header, e.getPoint());
                }
            }
        });
    }


    public static class ExtendedTableProperty extends TableProperty {
        private JTable checkComponent(Component component) {
            if (component == null) {
                throw new IllegalArgumentException("null component");
            }
            if (!(component instanceof JTable)) {
                throw new IllegalArgumentException("invalid component");
            }
            return (JTable) component;
        }

        @Override
        public Object getSessionState(Component c) {
            JTable table = checkComponent(c);
            TableColumnModel model = table.getColumnModel();
            int columnCount = model.getColumnCount();
            String[] columns = new String[columnCount];

            for (int i = 0; i < columnCount; i++) {
                TableColumn tc = model.getColumn(i);
                columns[i] = tc.getIdentifier().toString();
            }
            ExtendedTableState exState = new ExtendedTableState();
            exState.setColumnIds(columns);

            TableState state = (TableState) super.getSessionState(c);
            if (state != null) {
                exState.setColumnWidths(state.getColumnWidths());
            }

            return exState;
        }

        @Override
        public void setSessionState(Component c, Object state) {
            JTable table = checkComponent(c);
            ExtendedTableState exState = (ExtendedTableState) state;

            TableColumnModel model = table.getColumnModel();
            String[] columns = exState.getColumnIds();

            if (checkColumnsMatch(model, columns)) {
                removeColumns(model, columns);
            }

            super.setSessionState(c, state);
        }

        private boolean checkColumnsMatch(TableColumnModel model, String[] columns) {
            if (columns == null || columns.length == 0) {
                return false;
            }

            try {
                for (int i = 0; i < columns.length; i++) {
                    int index = model.getColumnIndex(columns[i]);
                    if (index < 0) {
                        return false;
                    }
                }
            }
            catch (IllegalArgumentException ex) {
                return false;
            }

            return true;
        }

        private void removeColumns(TableColumnModel model, String[] columns) {
            for (int i = model.getColumnCount() - 1; i >= 0; i--) {
                TableColumn tc = model.getColumn(i);
                if (ArrayUtils.indexOf(columns, tc.getIdentifier().toString()) == -1) {
                    model.removeColumn(tc);
                }
            }
        }
    }

    public static class ExtendedTableState extends TableState {
        private String[] columnIds = new String[0];

        public ExtendedTableState() {
        }

        public ExtendedTableState(int[] columnWidths) {
            super(columnWidths);
        }

        private String[] copyColumnIds(String[] columnIds) {
            if (columnIds == null) {
                throw new IllegalArgumentException("invalid columnIds");
            }
            String[] copy = new String[columnIds.length];
            System.arraycopy(columnIds, 0, copy, 0, columnIds.length);
            return copy;
        }

        public String[] getColumnIds() {
            return copyColumnIds(columnIds);
        }

        public void setColumnIds(String[] columnIds) {
            this.columnIds = copyColumnIds(columnIds);
        }
    }
}
