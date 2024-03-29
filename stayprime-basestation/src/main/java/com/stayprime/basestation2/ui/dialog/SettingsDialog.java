/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * SettingsDialog.java
 *
 * Created on 19/04/2011, 01:56:10 AM
 */
package com.stayprime.basestation2.ui.dialog;

import com.ezware.dialog.task.TaskDialogs;
import com.stayprime.basestation2.BaseStation2App;
import com.stayprime.basestation2.db.LoginPaneSQLConnectionProvider;
import com.stayprime.basestation2.services.CourseService;
import com.stayprime.basestation2.util.NotificationPopup;
import com.stayprime.hibernate.entities.UserLogin;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.Properties;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.JToggleButton;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.jdesktop.application.Action;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.Task;

/**
 *
 * @author benjamin
 */
public class SettingsDialog extends javax.swing.JDialog {

    public static DecimalFormat format = new DecimalFormat("#.#");

    CourseService courseService;
    
//    private Timer timer;
    /**
     * Creates new form SettingsDialog
     * @param parent
     * @param modal
     */
    public SettingsDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        popThresholdSettingsPanel1.init();
        setShowWebAppSettings(false);
        setShowATACSettings(false);
    }

    public void initialize() {
        popThresholdSettingsPanel1.loadSettings();
    }

    public void setCourseService(CourseService courseService) {
        this.courseService = courseService;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        settingsTabbedPanel = new javax.swing.JTabbedPane();
        atacTab = new org.jdesktop.swingx.JXPanel();
        atacSetting = new com.stayprime.basestation2.ui.custom.JXTitledSeparator();
        atacSettingsPanel = new org.jdesktop.swingx.JXPanel();
        lblUsername = new javax.swing.JLabel();
        atacUsernmaeFld = new javax.swing.JTextField();
        lblPassword = new javax.swing.JLabel();
        atacPwFld = new javax.swing.JPasswordField();
        atacIntervalLabel = new javax.swing.JLabel();
        atacIntervalSpinner = new javax.swing.JSpinner();
        loginButton = new javax.swing.JToggleButton();
        systemSettingsTab = new org.jdesktop.swingx.JXPanel();
        userMgmtPanel = new org.jdesktop.swingx.JXPanel();
        userMgmtSeparator = new com.stayprime.basestation2.ui.custom.JXTitledSeparator();
        createAdminUserButton = new javax.swing.JButton();
        popThresholdSettingsPanel1 = new com.stayprime.basestation2.ui.dialog.PopThresholdSettingsPanel();
        webAppTab = new org.jdesktop.swingx.JXPanel();
        syncSettingsTitle = new com.stayprime.basestation2.ui.custom.JXTitledSeparator();
        syncSettingsPanel = new org.jdesktop.swingx.JXPanel();
        pinIntervalLabel = new javax.swing.JLabel();
        pinIntervalSpinner = new javax.swing.JSpinner();
        pinUpdateButton = new javax.swing.JToggleButton();
        pushDataButton = new javax.swing.JButton();
        statusLabel = new org.jdesktop.swingx.JXLabel();
        dialogButtonsPanel = new org.jdesktop.swingx.JXPanel();
        closeButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance().getContext().getResourceMap(SettingsDialog.class);
        setTitle(resourceMap.getString("Form.title")); // NOI18N
        setName("Form"); // NOI18N

        settingsTabbedPanel.setName("settingsTabbedPanel"); // NOI18N

        atacTab.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        atacTab.setName("atacTab"); // NOI18N
        com.stayprime.ui.VerticalLayout2 verticalLayout23 = new com.stayprime.ui.VerticalLayout2();
        verticalLayout23.setAlignment(3);
        verticalLayout23.setVgap(10);
        atacTab.setLayout(verticalLayout23);

        atacSetting.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        atacSetting.setTitle(resourceMap.getString("atacSetting.title")); // NOI18N
        atacSetting.setName("atacSetting"); // NOI18N
        atacTab.add(atacSetting);

        atacSettingsPanel.setName("atacPanel"); // NOI18N
        java.awt.GridBagLayout atacSettingsPanelLayout = new java.awt.GridBagLayout();
        atacSettingsPanelLayout.columnWidths = new int[] {0, 5, 0};
        atacSettingsPanelLayout.rowHeights = new int[] {0, 5, 0, 5, 0, 5, 0};
        atacSettingsPanel.setLayout(atacSettingsPanelLayout);

        lblUsername.setText(resourceMap.getString("lblUsername.text")); // NOI18N
        lblUsername.setName("lblUsername"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        atacSettingsPanel.add(lblUsername, gridBagConstraints);

        atacUsernmaeFld.setText(resourceMap.getString("atacUsernmaeFld.text")); // NOI18N
        atacUsernmaeFld.setName("atacUsernmaeFld"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        atacSettingsPanel.add(atacUsernmaeFld, gridBagConstraints);

        lblPassword.setText(resourceMap.getString("lblPassword.text")); // NOI18N
        lblPassword.setName("lblPassword"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        atacSettingsPanel.add(lblPassword, gridBagConstraints);

        atacPwFld.setText(resourceMap.getString("atacPwFld.text")); // NOI18N
        atacPwFld.setName("atacPwFld"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        atacSettingsPanel.add(atacPwFld, gridBagConstraints);

        atacIntervalLabel.setText(resourceMap.getString("atacIntervalLabel.text")); // NOI18N
        atacIntervalLabel.setName("atacIntervalLabel"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        atacSettingsPanel.add(atacIntervalLabel, gridBagConstraints);

        atacIntervalSpinner.setModel(new javax.swing.SpinnerNumberModel(10, 0, 60, 10));
        atacIntervalSpinner.setName("atacIntervalSpinner"); // NOI18N
        atacIntervalSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                atacIntervalSpinnerStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        atacSettingsPanel.add(atacIntervalSpinner, gridBagConstraints);

        loginButton.setText(resourceMap.getString("loginButton.text")); // NOI18N
        loginButton.setName("loginButton"); // NOI18N
        loginButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loginButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        atacSettingsPanel.add(loginButton, gridBagConstraints);

        atacTab.add(atacSettingsPanel);

        settingsTabbedPanel.addTab(resourceMap.getString("atacTab.TabConstraints.tabTitle"), atacTab); // NOI18N

        systemSettingsTab.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        systemSettingsTab.setMaximumSize(new java.awt.Dimension(402, 178));
        systemSettingsTab.setName("systemSettingsTab"); // NOI18N
        systemSettingsTab.setPreferredSize(new java.awt.Dimension(580, 540));
        com.stayprime.ui.VerticalLayout2 verticalLayout21 = new com.stayprime.ui.VerticalLayout2();
        verticalLayout21.setAlignment(3);
        verticalLayout21.setVgap(10);
        systemSettingsTab.setLayout(verticalLayout21);

        userMgmtPanel.setName("userMgmtPanel"); // NOI18N
        org.jdesktop.swingx.VerticalLayout verticalLayout1 = new org.jdesktop.swingx.VerticalLayout();
        verticalLayout1.setGap(10);
        userMgmtPanel.setLayout(verticalLayout1);

        userMgmtSeparator.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        userMgmtSeparator.setTitle(resourceMap.getString("userMgmtSeparator.title")); // NOI18N
        userMgmtSeparator.setName("userMgmtSeparator"); // NOI18N
        userMgmtPanel.add(userMgmtSeparator);

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance().getContext().getActionMap(SettingsDialog.class, this);
        createAdminUserButton.setAction(actionMap.get("createAdminUser")); // NOI18N
        createAdminUserButton.setName("createAdminUserButton"); // NOI18N
        userMgmtPanel.add(createAdminUserButton);

        systemSettingsTab.add(userMgmtPanel);

        popThresholdSettingsPanel1.setName("popThresholdSettingsPanel1"); // NOI18N
        systemSettingsTab.add(popThresholdSettingsPanel1);

        settingsTabbedPanel.addTab(resourceMap.getString("systemSettingsTab.TabConstraints.tabTitle"), systemSettingsTab); // NOI18N

        webAppTab.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        webAppTab.setName("webAppTab"); // NOI18N
        com.stayprime.ui.VerticalLayout2 verticalLayout22 = new com.stayprime.ui.VerticalLayout2();
        verticalLayout22.setAlignment(3);
        verticalLayout22.setVgap(10);
        webAppTab.setLayout(verticalLayout22);

        syncSettingsTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        syncSettingsTitle.setTitle(resourceMap.getString("syncSettingsTitle.title")); // NOI18N
        syncSettingsTitle.setName("syncSettingsTitle"); // NOI18N
        webAppTab.add(syncSettingsTitle);

        syncSettingsPanel.setName("syncSettingsPanel"); // NOI18N
        java.awt.GridBagLayout jXPanel4Layout = new java.awt.GridBagLayout();
        jXPanel4Layout.columnWidths = new int[] {0, 5, 0};
        jXPanel4Layout.rowHeights = new int[] {0, 10, 0, 10, 0, 10, 0};
        syncSettingsPanel.setLayout(jXPanel4Layout);

        pinIntervalLabel.setText(resourceMap.getString("pinIntervalLabel.text")); // NOI18N
        pinIntervalLabel.setName("pinIntervalLabel"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        syncSettingsPanel.add(pinIntervalLabel, gridBagConstraints);

        pinIntervalSpinner.setModel(new javax.swing.SpinnerNumberModel(10, 0, 60, 10));
        pinIntervalSpinner.setName("pinIntervalSpinner"); // NOI18N
        pinIntervalSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                pinIntervalSpinnerStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        syncSettingsPanel.add(pinIntervalSpinner, gridBagConstraints);

        pinUpdateButton.setText(resourceMap.getString("pinUpdateButton.text")); // NOI18N
        pinUpdateButton.setName("pinUpdateButton"); // NOI18N
        pinUpdateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pinUpdateButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        syncSettingsPanel.add(pinUpdateButton, gridBagConstraints);

        pushDataButton.setAction(actionMap.get("pushData")); // NOI18N
        pushDataButton.setText(resourceMap.getString("pushDataButton.text")); // NOI18N
        pushDataButton.setName("pushDataButton"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        syncSettingsPanel.add(pushDataButton, gridBagConstraints);

        statusLabel.setLineWrap(true);
        statusLabel.setText(resourceMap.getString("statusLabel.text")); // NOI18N
        statusLabel.setName("statusLabel"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        syncSettingsPanel.add(statusLabel, gridBagConstraints);

        webAppTab.add(syncSettingsPanel);

        settingsTabbedPanel.addTab(resourceMap.getString("webAppTab.TabConstraints.tabTitle"), webAppTab); // NOI18N

        getContentPane().add(settingsTabbedPanel, java.awt.BorderLayout.CENTER);

        dialogButtonsPanel.setName("dialogButtonsPanel"); // NOI18N
        dialogButtonsPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        closeButton.setAction(actionMap.get("closeDialog")); // NOI18N
        closeButton.setName("closeButton"); // NOI18N
        dialogButtonsPanel.add(closeButton);

        getContentPane().add(dialogButtonsPanel, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void pinIntervalSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_pinIntervalSpinnerStateChanged

        try {
            JSpinner jSpinner = (JSpinner)evt.getSource();
            String value = jSpinner.getValue().toString();
            PropertiesConfiguration config = BaseStation2App.getApplication().getConfig();
            config.setProperty("pinUpdateInterval", value);
            config.save();
        }
        catch (ConfigurationException ex) {
        }
    }//GEN-LAST:event_pinIntervalSpinnerStateChanged

    private void pinUpdateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pinUpdateButtonActionPerformed
           JToggleButton button = (JToggleButton) evt.getSource();
        if (button.isSelected()) {
            startPinUpdate();
        } else {
            stopPinUpdate();
        }
    }//GEN-LAST:event_pinUpdateButtonActionPerformed

    private void atacIntervalSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_atacIntervalSpinnerStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_atacIntervalSpinnerStateChanged

    private void loginButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loginButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_loginButtonActionPerformed

    @Action
    public void closeDialog() {
        setVisible(false);
    }

    @Action
    public void createAdminUser() {
        CreateUserDialog dialog = CreateUserDialog.showCreateUserDialog(this, null, null, false, true);
        if (dialog.isConfirmed()) {
            try {
//                Properties connectionProperties = new Properties();
//                connectionProperties.put("zeroDateTimeBehavior", "convertToNull");
//                LoginPaneSQLConnectionProvider adminConnectionProvider = new LoginPaneSQLConnectionProvider(
//                        "jdbc:mysql://localhost:3306/stayprime", connectionProperties, this);
//                adminConnectionProvider.connect();

                if (true) {
                    UserLogin login = new UserLogin();
                    login.setDateCreated(new Date());
                    login.setFirstName(dialog.getName());
                    login.setUserName(dialog.getUserName());
                  //  login.setLastName(Boolean.toString(dialog.isCreateUsersAllowed()));
                    login.setPassword(new String(dialog.getPassword()));

                    courseService.saveUserLogin(login);
                    JOptionPane.showMessageDialog(this, "Administrator user created");
                }
            } catch (Exception ex) {
//                TaskDialogs.showException(ex);
                NotificationPopup.showErrorDialog("Couldn't create user");
            }
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel atacIntervalLabel;
    private javax.swing.JSpinner atacIntervalSpinner;
    private javax.swing.JPasswordField atacPwFld;
    private org.jdesktop.swingx.JXTitledSeparator atacSetting;
    private org.jdesktop.swingx.JXPanel atacSettingsPanel;
    private org.jdesktop.swingx.JXPanel atacTab;
    private javax.swing.JTextField atacUsernmaeFld;
    private javax.swing.JButton closeButton;
    private javax.swing.JButton createAdminUserButton;
    private org.jdesktop.swingx.JXPanel dialogButtonsPanel;
    private javax.swing.JLabel lblPassword;
    private javax.swing.JLabel lblUsername;
    private javax.swing.JToggleButton loginButton;
    private javax.swing.JLabel pinIntervalLabel;
    private javax.swing.JSpinner pinIntervalSpinner;
    private javax.swing.JToggleButton pinUpdateButton;
    private com.stayprime.basestation2.ui.dialog.PopThresholdSettingsPanel popThresholdSettingsPanel1;
    private javax.swing.JButton pushDataButton;
    private javax.swing.JTabbedPane settingsTabbedPanel;
    private org.jdesktop.swingx.JXLabel statusLabel;
    private org.jdesktop.swingx.JXPanel syncSettingsPanel;
    private org.jdesktop.swingx.JXTitledSeparator syncSettingsTitle;
    private org.jdesktop.swingx.JXPanel systemSettingsTab;
    private org.jdesktop.swingx.JXPanel userMgmtPanel;
    private org.jdesktop.swingx.JXTitledSeparator userMgmtSeparator;
    private org.jdesktop.swingx.JXPanel webAppTab;
    // End of variables declaration//GEN-END:variables

    public void setShowWebAppSettings(boolean show) {
        settingsTabbedPanel.remove(webAppTab);

        if (show) {
            BaseStation2App app = BaseStation2App.getApplication();
            ResourceMap resourceMap = app.getContext().getResourceMap(SettingsDialog.class);
            settingsTabbedPanel.addTab(resourceMap.getString(webAppTab.getName()
                    + ".TabConstraints.tabTitle"), webAppTab);
        }
    }

    public void setShowATACSettings(boolean show) {
        settingsTabbedPanel.remove(atacTab);

        if (show) {
            BaseStation2App app = BaseStation2App.getApplication();
            ResourceMap resourceMap = app.getContext().getResourceMap(SettingsDialog.class);
            settingsTabbedPanel.addTab(resourceMap.getString(atacTab.getName()
                    + ".TabConstraints.tabTitle"), atacTab);
        }
    }


    @Action(block = Task.BlockingScope.APPLICATION)
    public Task pushData() {
        return new PushDataTask(org.jdesktop.application.Application.getInstance());
    }


    private void startPinUpdate() {
        //Ben: I have removed this code because there are 2 big issues:
        //-This long running code (web service call + DB write) shouldn't run
        // in a Swing timer, because it will bock the UI while running.
        //-It was using PinLocation's course_id and hole_id to match the server's
        // pins with the local Pins, but these are generated ids and won't match
        // the local course number and hole number. We may need a special service
        // which returns Pins with hole number and course number (or index)

//        int interval = (Integer)pinIntervalSpinner.getValue();
//        int delay = (int) TimeUnit.MINUTES.toMillis(interval);
//
//        if (timer!=null && timer.isRunning () ) {
//                    timer.stop ();
//         }
//
//        timer = new javax.swing.Timer(delay, new ActionListener() {
//
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                System.out.println("start updating pins");
//                WebGolfDao ws = new WebGolfDao(WebServicePath.BASE_URL_SP);
//                ws.login("spadmin@stayprime.com", "12345");
//                Site site = ws.getSite(2);
//                com.stayprime.model.golf.PinLocation[] list = ws.listPinlocation(site);
//
//                List<PinLocation> pins = new ArrayList<PinLocation>(list.length);
//                Date date = new Date();
//                for(com.stayprime.model.golf.PinLocation pin : list) {
//                    pins.add(new PinLocation(pin.course_id, pin.hole_id, pin.position, date));
//                }
//                CourseStorageService courseService = BaseStation2App.getApplication().getCourseStorageService();
//
//            }
//        });
//        timer.start();
//        pinUpdateButton.setText("Stop Pin Update");
    }

    public void stopPinUpdate(){
//         if (timer!=null && timer.isRunning () ) {
//             timer.stop ();
//         }
//         pinUpdateButton.setText("Start Pin Update");
    }

    private class PushDataTask extends org.jdesktop.application.Task<String, Void> {
        private String webServiceUrl;
        private String webServiceUser;
        private String webServicePass;

        PushDataTask(org.jdesktop.application.Application app) {
            super(app);
            PropertiesConfiguration config = BaseStation2App.getApplication().getConfig();
//            webServiceUrl = config.getString("webServiceUrl", WebServicePath.BASE_URL_SP);
            webServiceUser = config.getString("webServiceUser", "spadmin@stayprime.com");
            webServicePass = config.getString("webServicePass", "12345");
        }

        @Override protected String doInBackground() {
//            try {
//                BaseStation2App app = BaseStation2App.getApplication();
//                GolfClub gc = app.getManager().getGolfClub();
//                int siteId = app.getSiteId();
//
//                WebGolfDao dao = new WebGolfDao(webServiceUrl);
//                dao.login(webServiceUser, webServicePass);
//
//                GolfclubSchemaConverter ws = new GolfclubSchemaConverter(dao);
//
//                Site site = ws.updateFullSite(gc, siteId);
//                app.setSiteId(site.getSiteId());
//                return site.toString();
//            }
//            catch (Exception ex) {
//                return ExceptionUtils.getFullStackTrace(ex);
//            }
            return null;
        }
        @Override protected void succeeded(String result) {
            statusLabel.setText(result);
        }

    }

}
