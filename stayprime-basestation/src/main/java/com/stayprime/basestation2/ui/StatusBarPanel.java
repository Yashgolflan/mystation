/*
 * 
 */
package com.stayprime.basestation2.ui;

import com.stayprime.basestation2.BaseStation2App;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Icon;
import javax.swing.Timer;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.TaskMonitor;

/**
 *
 * @author benjamin
 */
public class StatusBarPanel extends javax.swing.JPanel {
    private Timer messageTimer;
    private Timer busyIconTimer;
    private Icon idleIcon;
    private Icon[] busyIcons = new Icon[15];
    private int busyIconIndex = 0;

    /**
     * Creates new form StatusBarPanel
     */
    public StatusBarPanel() {
        initComponents();
    }

    public void init() {
        BaseStation2App app = BaseStation2App.getApplication();
        ResourceMap resourceMap = app.getMainView().getResourceMap();

        // status bar initialization - message timeout, idle icon and busy animation, etc
        int messageTimeout = resourceMap.getInteger("StatusBar.messageTimeout");
        messageTimer = new Timer(messageTimeout, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                statusMessageLabel.setText("");
            }
        });
        messageTimer.setRepeats(false);
        int busyAnimationRate = resourceMap.getInteger("StatusBar.busyAnimationRate");
        for (int i = 0; i < busyIcons.length; i++) {
            busyIcons[i] = resourceMap.getIcon("StatusBar.busyIcons[" + i + "]");
        }
        busyIconTimer = new Timer(busyAnimationRate, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                busyIconIndex = (busyIconIndex + 1) % busyIcons.length;
                statusAnimationLabel.setIcon(busyIcons[busyIconIndex]);
            }
        });
        idleIcon = resourceMap.getIcon("StatusBar.idleIcon");
        statusAnimationLabel.setIcon(idleIcon);
        progressBar.setVisible(false);

        // connecting action tasks to status bar via TaskMonitor
        TaskMonitor taskMonitor = new TaskMonitor(app.getContext());
        taskMonitor.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            @Override
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                String propertyName = evt.getPropertyName();
                if ("started".equals(propertyName)) {
                    if (!busyIconTimer.isRunning()) {
                        statusAnimationLabel.setIcon(busyIcons[0]);
                        busyIconIndex = 0;
                        busyIconTimer.start();
                    }
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(true);
                }
                else if ("done".equals(propertyName)) {
                    busyIconTimer.stop();
                    statusAnimationLabel.setIcon(idleIcon);
                    progressBar.setVisible(false);
                    progressBar.setValue(0);
                }
                else if ("message".equals(propertyName)) {
                    String text = (String)(evt.getNewValue());
                    statusMessageLabel.setText((text == null) ? "" : text);
                    messageTimer.restart();
                }
                else if ("progress".equals(propertyName)) {
                    int value = (Integer)(evt.getNewValue());
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(false);
                    progressBar.setValue(value);
                }
            }
        });
    }

    public void showStatusMessage(String message) {
        statusMessageLabel.setText(message);
        messageTimer.restart();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        statusAnimationLabel = new javax.swing.JLabel();
        statusMessageLabel = new javax.swing.JLabel();
        progressBar = new javax.swing.JProgressBar();

        setOpaque(false);
        setLayout(new java.awt.BorderLayout(8, 2));

        statusAnimationLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        statusAnimationLabel.setText(" "); // NOI18N
        statusAnimationLabel.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        statusAnimationLabel.setName("statusAnimationLabel"); // NOI18N
        add(statusAnimationLabel, java.awt.BorderLayout.WEST);

        statusMessageLabel.setName("statusMessageLabel"); // NOI18N
        add(statusMessageLabel, java.awt.BorderLayout.CENTER);

        progressBar.setName("progressBar"); // NOI18N
        progressBar.setVerifyInputWhenFocusTarget(false);
        add(progressBar, java.awt.BorderLayout.EAST);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JLabel statusAnimationLabel;
    private javax.swing.JLabel statusMessageLabel;
    // End of variables declaration//GEN-END:variables
}