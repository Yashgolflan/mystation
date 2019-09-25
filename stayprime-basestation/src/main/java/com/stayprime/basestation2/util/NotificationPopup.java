/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stayprime.basestation2.util;

import com.alee.managers.notification.NotificationIcon;
import com.alee.managers.notification.NotificationManager;
import com.alee.managers.notification.WebNotificationPopup;
import javax.swing.JOptionPane;

/**
 *
 * @author Omer
 */
public class NotificationPopup {

    private NotificationIcon icon;
    private String message;
    private long displayTime;

    /**
     * long length to display text in miliseconds
     */
    public static final long LENGTH_LONG = 10000;
    public static final long LENGTH_SHORT = 3000;
    
    public static final String ERROR_TITLE = "ERROR";

    
    public static NotificationPopup build(NotificationIcon icon) {
        return build(null, icon);
    }
    
    public static NotificationPopup build(String message, NotificationIcon icon) {
        return build(message, icon, LENGTH_SHORT);
    }

    public static NotificationPopup build(String message, NotificationIcon icon, long displayTime) {

        NotificationPopup notification = new NotificationPopup();
        notification.displayTime = displayTime;
        notification.message = message;
        notification.icon = icon;

        return notification;
    }

    public void show() {

        WebNotificationPopup notificationPopup = new WebNotificationPopup();
        notificationPopup.setDisplayTime(displayTime);
        notificationPopup.setContent(message);
        notificationPopup.setIcon(icon);

        NotificationManager.showNotification(notificationPopup);
    }

    public NotificationIcon getIcon() {
        return icon;
    }

    public void setIcon(NotificationIcon icon) {
        this.icon = icon;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getDisplayTime() {
        return displayTime;
    }

    public void setDisplayTime(long displayTime) {
        this.displayTime = displayTime;
    }
    
    public static void showErrorPopup() {
        JOptionPane.showMessageDialog(null,
                    "Failed to connect to the internet, you will be seeing cached data",
                    "Connection Error",
                    JOptionPane.ERROR_MESSAGE);
    }
    
    public static void showErrorPopup(String entity) {
        JOptionPane.showMessageDialog(null,
                    "Coudln't process the action" + (entity == null ? "" : " on " + entity),
                    "Connection Error",
                    JOptionPane.ERROR_MESSAGE);
    }
    
    public static void showErrorPopup(String entity, String title) {
        JOptionPane.showMessageDialog(null,
                    "Coudln't process the action on " + entity,
                    title,
                    JOptionPane.ERROR_MESSAGE);
    }
    
    public static void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(null,
                    message,
                    ERROR_TITLE,
                    JOptionPane.ERROR_MESSAGE);
    }
    public static void showIncompeleteDetailsErrorPopup(String message){
        JOptionPane.showMessageDialog(null,
                    message,
                    "Incompelete Details",
                    JOptionPane.INFORMATION_MESSAGE);
    }

}
