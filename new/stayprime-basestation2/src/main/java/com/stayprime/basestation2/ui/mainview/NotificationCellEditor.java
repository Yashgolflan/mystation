/*
 * 
 */
package com.stayprime.basestation2.ui.mainview;

import com.aeben.golfclub.RequestType;
import com.ezware.dialog.task.TaskDialogs;
import com.stayprime.basestation2.BaseStation2App;
import com.stayprime.basestation2.comm.NotificationsManager;
import com.stayprime.hibernate.entities.ServiceRequest;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.EventObject;
import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.Task;

/**
 *
 * @author benjamin
 */
public class NotificationCellEditor extends AbstractCellEditor implements TableCellEditor {

    private static final Logger log = LoggerFactory.getLogger(NotificationsPanel.class);

    private final NotificationCellRenderer notificationCellRenderer;

    private NotificationsManager notificationsManager;

    private Object cellEdiorValue;

    private ServiceRequest updateRequest;

    public NotificationCellEditor() {
        notificationCellRenderer = new NotificationCellRenderer();

        JButton cancel = new JButton("Dismiss");
        cancel.setFont(notificationCellRenderer.getFont());
        notificationCellRenderer.bottomExtraPanel.add(cancel);
        cancel.addActionListener(getDismissRequestAction());

        JButton confirm = new JButton("Confirm");
        confirm.setFont(notificationCellRenderer.getFont());
        notificationCellRenderer.bottomExtraPanel.add(confirm);
        confirm.addActionListener(getReplyRequestAction());
    }

    public void setNotificationsManager(NotificationsManager notificationsManager) {
        this.notificationsManager = notificationsManager;
    }

    private javax.swing.Action getReplyRequestAction() {
        return Application.getInstance().getContext().getActionMap(this).get("replyRequest");
    }

    @Action(block = Task.BlockingScope.COMPONENT)
    public Task replyRequest() {
        return getReplyTask(true);
    }

    private javax.swing.Action getDismissRequestAction() {
        return Application.getInstance().getContext().getActionMap(this).get("dismissRequest");
    }

    @Action(block = Task.BlockingScope.COMPONENT)
    public Task dismissRequest() {
        return getReplyTask(false);
    }

    private Task getReplyTask(boolean reply) {
        if(updateRequest != null && updateRequest.getStatus() < RequestType.REQUEST_SERVICED) {
            BaseStation2App app = Application.getInstance(BaseStation2App.class);
            return new ReplyRequestTask(app, updateRequest, reply);
        }
        stopCellEditing();
        return null;
    }

    private class ReplyRequestTask extends org.jdesktop.application.Task<Object, Void> {
        private final ServiceRequest request;
        private final boolean reply;

        ReplyRequestTask(Application app, ServiceRequest request, boolean reply) {
            super(app);
            this.request = request;
            this.reply = reply;
        }

        @Override
        protected Object doInBackground() {
            try {
                notificationsManager.replyServiceRequest(request, reply);
                return null;
            }
            catch(Exception ex) {
                return ex;
            }
        }
        
        @Override
        protected void succeeded(Object result) {
            if(result instanceof Exception) {
                setMessage("Reply request database operation failed: " + result);
                TaskDialogs.showException((Throwable) result);
            }
            else {
            }
	    updateRequest = null;
        }
    }

    @Override
    public boolean isCellEditable(EventObject e) {
        if(e instanceof MouseEvent && e.getSource() instanceof JTable) {
            MouseEvent mouseEvent = (MouseEvent) e;
            JTable table = (JTable) e.getSource();

            if(mouseEvent.getClickCount() > 0) {//SwingUtilities.isLeftMouseButton(mouseEvent))
                Point point = mouseEvent.getPoint();
                int row = table.rowAtPoint(point),
                        col = table.columnAtPoint(point);

                if(row >= 0 && col >= 0) {
                    Object value = table.getValueAt(row, col);

                    if(value instanceof ServiceRequest
                            && ((ServiceRequest)value).getStatus() < RequestType.REQUEST_SERVICED)
                        return true;
                }
            }
            else
                return false;
        }

        return false;
    }

    @Override
    public Object getCellEditorValue() {
        return cellEdiorValue;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        Component component = notificationCellRenderer.getTableCellRendererComponent(table, value, true, true, row, column);
        cellEdiorValue = value;

        if (value instanceof ServiceRequest) {
            updateRequest = (ServiceRequest) value;
        }
        else {
            updateRequest = null;
        }
        
        return component;
    }
}
