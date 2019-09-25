/*
 * 
 */
package com.stayprime.basestation2.ui.mainview;

import com.aeben.golfclub.RequestType;
import com.stayprime.basestation2.ui.custom.NotificationUICellRenderer;
import com.stayprime.basestation2.ui.util.CommonUtils;
import com.stayprime.hibernate.entities.ServiceRequest;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Rectangle;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.Icon;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author benjamin
 */
public class NotificationCellRenderer extends NotificationUICellRenderer implements TableCellRenderer {

    private Icon rangerIcon;
    private Icon emergencyIcon;
    private Icon ambulanceIcon;
    private Icon fnbIcon;
    private SimpleDateFormat format;
    private int textAreaTextHeight = 0;
    /* Added by javed on 10-04-19, this is for height of text*/
    private int buttonHeight;/* Added by javed on 10-04-19, this is for height of buttons which is shown below the message(Dismiss & Confirm)*/
    public NotificationCellRenderer() {
       format = new SimpleDateFormat("dd-MMM-hh:mm");
        dateFormat = DateFormat.getDateTimeInstance();
        rangerIcon = new javax.swing.ImageIcon(getClass().getResource("/com/stayprime/basestation2/resources/assistance32.png"));
        emergencyIcon = new javax.swing.ImageIcon(getClass().getResource("/com/stayprime/basestation2/resources/emergency32.png"));
        ambulanceIcon = new javax.swing.ImageIcon(getClass().getResource("/com/stayprime/basestation2/resources/ambulance32.png"));
        fnbIcon = new javax.swing.ImageIcon(getClass().getResource("/com/stayprime/basestation2/resources/food.png"));
        textAreaTextHeight = 17;
        /*Default text height, added by javed on 10-04-19 */
        buttonHeight = 35;
        /*Default button height, added by javed on 10-04-19 */

    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (value instanceof ServiceRequest) {
            ServiceRequest request = (ServiceRequest) value;
            setBackgroundPainter(isSelected ? selected : normal);
            messageLabel.setText("<html><p>"+ "Cart " + request.getCartNumber() +"</p><br/></html>");;
           
            topRightLabel.setText("<html><p>"+ format.format(request.getTime())+"</p></html>");
            String reply = "";
            if (request.getStatus() == RequestType.REQUEST_SERVICED) {
                reply = ": Confirmed" + (request.getRepliedBy() != null ? " by " + request.getRepliedBy() : "") + (request.getReplyTime() != null ? " at " + format.format(request.getReplyTime()) : "");
            } else if (request.getStatus() == RequestType.REQUEST_DENIED) {
                reply = ": Denied" + (request.getRepliedBy() != null ? " by " + request.getRepliedBy() : "") + (request.getReplyTime() != null ? " at " + format.format(request.getReplyTime()) : "");
            }
            /*
                both message and bottomheight variable
                added by javed on 10-04-19
                for FNB notification message UI
             */
            String message = "";
            int bottomHeight = 50;

            if (request.getType() == RequestType.RANGER.type) {
                message = "Ranger request" + reply;
                messageLabel.setIcon(rangerIcon);

                setRangerAndOtherMessage(bottomHeight, message);

            } else if (request.getType() == RequestType.EMERGENCY.type) {
                message = "Emergency request" + reply;
                messageLabel.setIcon(emergencyIcon);

                setRangerAndOtherMessage(bottomHeight, message);

            } else if (request.getType() == RequestType.AMBULANCE.type) {
                message = "Ambulance request" + reply;
                messageLabel.setIcon(ambulanceIcon);

                setRangerAndOtherMessage(bottomHeight, message);

            } else if (request.getType() == RequestType.FNB.type) {
                message = request.getAdditionalInfo() + reply;
                messageLabel.setIcon(fnbIcon);

                int lines = getMessagesLine(message);
                if (textAreaTextHeight > 0) {
                    bottomHeight = (lines * textAreaTextHeight) + buttonHeight;
                }

                setBottomHeight(bottomHeight, lines);
            }
         //   detailsLabel.setMargin(new Insets(0,50,0,0));
        // detailsLabel.setColumns(3);
            detailsLabel.setText(message);
          //  detailsLabel.setText(message);

            if (getPreferredSize().height != table.getRowHeight(row) && row != table.getEditingRow()) {
                table.setRowHeight(row, getPreferredSize().height);
            }

            return this;
        } else {
            return empty;
        }
    }

    /*
        added by javed on 10-04-19
        for FNB notification message UI
        this method created due to code use multiple time
     */
    void setRangerAndOtherMessage(int bottomHeight, String message) {
        bottomHeight = 50;
        if (message.toLowerCase().contains("by")) {
            bottomHeight = 65;
            setBottomHeight(bottomHeight, 2);
        } else {
            setBottomHeight(bottomHeight, 1);
        }

    }

    /*
        added by javed on 10-04-19
        for FNB notification message UI
        this method return number of lines in a message, which is further use for table cell height calculation
     */
    int getMessagesLine(String message) {
        String[] lines = message.split("\r\n|\r|\n");
        int noOfLines = lines.length;
        for (String oneLine : lines) {
            if (oneLine.length() > 27) {
                String[] mArray = CommonUtils.splitByNumber(oneLine, 27);
                noOfLines = noOfLines + (mArray.length - 1);
            }
        }

        return noOfLines;

    }

    /*
        added by javed on 10-04-19
        for FNB notification message UI
        this method created for text area height, which is commonly used for diffrent types of notification
     */
    void setBottomHeight(int bottomHeight, int lineCount) {
        int width = bottomExtraPanel.getPreferredSize().width;
        bottomExtraPanel.setMinimumSize(new Dimension(width, bottomHeight));
        bottomExtraPanel.setPreferredSize(new Dimension(width, bottomHeight));
        bottomExtraPanel.setMaximumSize(new Dimension(2*width, 2*bottomHeight));

        detailsLabel.setRows(lineCount);

        detailsLabel.invalidate();
    }

}
