/*
 * 
 */
package com.stayprime.basestation2.ui.mainview;

import com.aeben.golfclub.GolfClub;
import com.aeben.golfclub.utils.Formatters;
import com.stayprime.basestation2.BaseStation2App;
import com.stayprime.basestation2.CourseSettingsManager;
import com.stayprime.basestation2.ui.custom.CartStatusUICellRenderer;
import com.stayprime.comm.encoder.PacketType;
import com.stayprime.hibernate.entities.CartInfo;
import java.awt.Component;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author benjamin
 */
public class CartStatusCellRenderer extends CartStatusUICellRenderer implements TableCellRenderer {

    private final Icon okIcon, cautionIcon, warningIcon, standByIcon, commLostIcon, atacIcon;
    private final Icon batt[];

    private final ImageIcon messageIcon;
    private final ImageIcon messageWarningIcon;

    private GolfClub golfClub;

    public CartStatusCellRenderer() {
        dateFormat = DateFormat.getTimeInstance();

        okIcon = new ImageIcon(getClass().getResource("/com/stayprime/basestation2/resources/ball_green32.png"));
        cautionIcon = new ImageIcon(getClass().getResource("/com/stayprime/basestation2/resources/ball_yellow32.png"));
        warningIcon = new ImageIcon(getClass().getResource("/com/stayprime/basestation2/resources/ball_red32.png"));
        standByIcon = new ImageIcon(getClass().getResource("/com/stayprime/basestation2/resources/ball_white32.png"));
        commLostIcon = new ImageIcon(getClass().getResource("/com/stayprime/basestation2/resources/ball_grey32.png"));

        atacIcon = new ImageIcon(getClass().getResource("/com/stayprime/basestation2/resources/security_32.png"));
        messageIcon = new ImageIcon(getClass().getResource("/com/stayprime/basestation2/resources/send-message14.png"));
        messageWarningIcon = new ImageIcon(getClass().getResource("/com/stayprime/basestation2/resources/warning16.png"));

        batt = new Icon[]{
            new ImageIcon(getClass().getResource("/com/stayprime/basestation2/resources/batt_1.png")),
            new ImageIcon(getClass().getResource("/com/stayprime/basestation2/resources/batt_2.png")),
            new ImageIcon(getClass().getResource("/com/stayprime/basestation2/resources/batt_3.png")),
            new ImageIcon(getClass().getResource("/com/stayprime/basestation2/resources/batt_4.png"))
        };
    }

    public void setGolfClub(GolfClub golfClub) {
        this.golfClub = golfClub;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (value instanceof CartInfo) {
            CartInfo cartInfo = (CartInfo) value;
            setBackgroundPainter(isSelected ? selected : normal);
            setCartLabel(cartInfo);
            setPlayingHoleLabel(cartInfo);
            setPaceOfPlay(cartInfo);
            setMessageStatus(cartInfo);

            if (getPreferredSize().height != table.getRowHeight(row) && row != table.getEditingRow()) {
                table.setRowHeight(row, getPreferredSize().height);
            }

            return this;
        }
        else {
            return empty;
        }
    }

    private void setCartLabel(CartInfo cartInfo) {
        Integer mode = cartInfo.getCartMode();
        if (mode != null && mode == PacketType.STATUS_APPMODE_ATAC) {
            messageLabel.setText("Ranger " + cartInfo.getCartNumber());
        }
        else {
            messageLabel.setText("Cart " + cartInfo.getCartNumber());
        }
    }

    private void setPlayingHoleLabel(CartInfo cartInfo) {
        if (golfClub != null && cartInfo.getPlayingHole() != null && cartInfo.getPlayingHole() != 0) {
            detailsLabel.setText(Formatters.getShortCourseAndHoleString(golfClub.getAbsoluteHoleNumber(cartInfo.getPlayingHole())));
        }
        else {
            detailsLabel.setText(null);
        }
    }

    private void setPaceOfPlay(CartInfo cartInfo) {
        CourseSettingsManager courseSettingsManager = BaseStation2App.getApplication().getCourseSettingsManager();

        if (cartInfo.getLocationLastUpdated() != null) {
            Calendar updated = new GregorianCalendar(), updateLimit = new GregorianCalendar();
            updated.setTime(cartInfo.getLocationLastUpdated());
            updateLimit.setTime(cartInfo.getLocationLastUpdated());
            updateLimit.add(Calendar.SECOND, courseSettingsManager.getCommunicationLostTimeout());
            GregorianCalendar now = new GregorianCalendar();

            if (updateLimit.before(now)) {
                setCommLostDetails(cartInfo, updated, now);
            }
            else {
                setPaceOfPlayDetails(cartInfo, courseSettingsManager.getPaceOfPlayThreshold());
                setBatteryLevel(cartInfo);
            }
        }
        else {
            messageLabel.setIcon(commLostIcon);
            midRightLabel.setText(null);
        }
    }

    private void setCommLostDetails(CartInfo cartInfo, Calendar updated, GregorianCalendar now) {
        messageLabel.setIcon(commLostIcon);

        String since = (updated.get(Calendar.YEAR) != now.get(Calendar.YEAR)
                || updated.get(Calendar.MONTH) != now.get(Calendar.MONTH)
                || updated.get(Calendar.DATE) != now.get(Calendar.DATE)
                        ? DateFormat.getDateInstance(DateFormat.SHORT) : dateFormat)
                .format(cartInfo.getLocationLastUpdated());

        detailsLabel.setText("Comm lost: " + since);
        midRightLabel.setText(null);
    }

    private void setPaceOfPlayDetails(CartInfo cartInfo, String thresholdSetting) {
        Integer pace = cartInfo.getPaceOfPlay();
        if (cartInfo.isPlaying() && thresholdSetting != null) {
            midRightLabel.setText(Formatters.getPaceOfPlayString(pace));
            int thresholds[] = Formatters.getPaceOfPlayThresholdArray(thresholdSetting);
            if (thresholds != null) {
                if (pace < thresholds[1]) {
                    messageLabel.setIcon(warningIcon);
                }
                else if (pace < thresholds[0]) {
                    messageLabel.setIcon(cautionIcon);
                }
                else {
                    messageLabel.setIcon(okIcon);
                }
            }
        }
        else if (cartInfo.getCartMode() != null && cartInfo.getCartMode() == PacketType.STATUS_APPMODE_ATAC) {
            messageLabel.setIcon(atacIcon);
        }
        else {
            midRightLabel.setText(null);
            messageLabel.setIcon(standByIcon);
        }
    }

    private void setBatteryLevel(CartInfo cartInfo) {
//                    if (cartStatus.getBatteryLevel() != null) {
//                        topRightLabel.setIcon(batt[Math.round(cartStatus.getBatteryLevel() * 3)]);
//                    }
    }

    private void setMessageStatus(CartInfo cartInfo) {
        String pending = cartInfo.getProperties().get("pendingMessage");
        String failed = cartInfo.getProperties().get("failedMessage");
        boolean clear = true;
        if (StringUtils.isNotBlank(pending)) {
            int msg = pending.indexOf('t');
            if (msg >= 0) {
                clear = false;
                topRightLabel.setIcon(messageIcon);
            }
        }
        else if (StringUtils.isNotBlank(failed)) {
            int msg = failed.indexOf('t');
            if (msg >= 0) {
                clear = false;
                topRightLabel.setIcon(messageWarningIcon);
            }
        }
        if (clear) {
            topRightLabel.setIcon(null);
        }
    }
}
