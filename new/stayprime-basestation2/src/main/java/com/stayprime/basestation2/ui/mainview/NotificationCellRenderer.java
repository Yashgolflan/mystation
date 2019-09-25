/*
 * 
 */
package com.stayprime.basestation2.ui.mainview;

import com.aeben.golfclub.RequestType;
import com.stayprime.basestation2.ui.custom.DetailedCellRenderer;
import com.stayprime.hibernate.entities.ServiceRequest;
import java.awt.Component;
import java.text.DateFormat;
import javax.swing.Icon;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author benjamin
 */
public class NotificationCellRenderer extends DetailedCellRenderer implements TableCellRenderer {
    private Icon rangerIcon;
    private Icon emergencyIcon;
    private Icon ambulanceIcon;
    private Icon fnbIcon;

    public NotificationCellRenderer() {
        dateFormat = DateFormat.getTimeInstance();
        rangerIcon = new javax.swing.ImageIcon(getClass().getResource("/com/stayprime/basestation2/resources/assistance32.png"));
        emergencyIcon = new javax.swing.ImageIcon(getClass().getResource("/com/stayprime/basestation2/resources/emergency32.png"));
        ambulanceIcon = new javax.swing.ImageIcon(getClass().getResource("/com/stayprime/basestation2/resources/ambulance32.png"));
        fnbIcon = new javax.swing.ImageIcon(getClass().getResource("/com/stayprime/basestation2/resources/food.png"));
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (value instanceof ServiceRequest) {
            ServiceRequest request = (ServiceRequest) value;
            setBackgroundPainter(isSelected ? selected : normal);
            messageLabel.setText("Cart " + request.getCartNumber());
            topRightLabel.setText(dateFormat.format(request.getTime()));
            String reply = "";
            if (request.getStatus() == RequestType.REQUEST_SERVICED) {
                reply = ": Confirmed" + (request.getRepliedBy() != null ? " by " + request.getRepliedBy() : "") + (request.getReplyTime() != null ? " at " + dateFormat.format(request.getReplyTime()) : "");
            }
            else if (request.getStatus() == RequestType.REQUEST_DENIED) {
                reply = ": Denied" + (request.getRepliedBy() != null ? " by " + request.getRepliedBy() : "") + (request.getReplyTime() != null ? " at " + dateFormat.format(request.getReplyTime()) : "");
            }
            if (request.getType() == RequestType.RANGER.type) {
                detailsLabel.setText("Ranger request" + reply);
                messageLabel.setIcon(rangerIcon);
            }
            else if (request.getType() == RequestType.EMERGENCY.type) {
                detailsLabel.setText("Emergency request" + reply);
                messageLabel.setIcon(emergencyIcon);
            }
            else if (request.getType() == RequestType.AMBULANCE.type) {
                detailsLabel.setText("Ambulance request" + reply);
                messageLabel.setIcon(ambulanceIcon);
            }
            else if (request.getType() == RequestType.FNB.type) {
                detailsLabel.setText(request.getAdditionalInfo() + reply);
                messageLabel.setIcon(fnbIcon);
            }
            if (getPreferredSize().height != table.getRowHeight(row) && row != table.getEditingRow()) {
                table.setRowHeight(row, getPreferredSize().height);
            }
            return this;
        }
        else {
            return empty;
        }
    }
    
}
