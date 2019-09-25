/*
 * 
 */
package com.stayprime.ui.swing;

import ca.odell.glazedlists.EventList;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JTable;

/**
 * Mouse listener for JTable double click action.
 * It can notify a standard ActionListener or a special TableActionListener.
 * @author benjamin
 */
public class TableActionAdapter<T> extends MouseAdapter {
    private EventList<T> eventList;
    private TableActionListener<T> listener;
    private ActionListener actionListener;

    public TableActionAdapter() {
    }

    public TableActionAdapter(EventList<T> list, TableActionListener<T> listener) {
        this.eventList = list;
        this.listener = listener;
    }

    public void setEventList(EventList<T> eventList) {
        this.eventList = eventList;
    }

    public void setListener(TableActionListener<T> listener) {
        this.listener = listener;
    }

    public void setActionListener(ActionListener actionListener) {
        this.actionListener = actionListener;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getSource() instanceof JTable && e.getClickCount() == 2) {
                JTable table = (JTable) e.getSource();
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());

                if (listener != null) {
                    if (eventList != null) {
                        listener.actionPerformed(eventList.get(row), row, col);
                    }
                    else {
                        listener.actionPerformed(null, row, col);
                    }
                }

                if (actionListener != null) {
                    actionListener.actionPerformed(new ActionEvent(table, ActionEvent.ACTION_PERFORMED, "double click"));
                }
        }
    }

}
