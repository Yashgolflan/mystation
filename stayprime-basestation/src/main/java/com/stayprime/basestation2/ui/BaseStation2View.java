/*
 * BaseStation2View.java
 */

package com.stayprime.basestation2.ui;

import com.aeben.golfclub.GolfClub;
import com.stayprime.basestation.ui.FileChooser;
import com.stayprime.basestation2.BaseStation2App;
import com.stayprime.basestation2.Constant;
import com.stayprime.basestation2.CourseSettingsManager;
import com.stayprime.basestation2.comm.NotificationsManager;
import com.stayprime.basestation2.services.Services;
import com.stayprime.basestation2.ui.dialog.ExportGreensDialog;
import com.stayprime.basestation2.services.SendMessageControl;
import com.stayprime.basestation2.ui.dialog.SettingsDialog;
import com.stayprime.basestation2.ui.mainview.CartInfoFilter;
import com.stayprime.basestation2.ui.mainview.MainActions;
import com.stayprime.basestation2.ui.mainview.MainPanel;
import com.stayprime.basestation2.ui.util.ApplicationUtil;
import com.stayprime.basestation2.util.ExportGreensTask;
import com.stayprime.hibernate.entities.CartInfo;
import java.awt.event.ActionEvent;
import org.jdesktop.application.Action;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.FrameView;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.List;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.jdesktop.application.Application;
import org.jdesktop.application.ApplicationAction;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.Task;

/**
 * The application's main frame.
 */
public class BaseStation2View extends FrameView {
    private final FileChooser fileChooser;

    private ExportGreensDialog exportGreensDialog;

    private SettingsDialog settingsDialog;

    private JDialog aboutBox;

    private PropertiesConfiguration config;

    private Services services;

    private CartInfoFilter cartInfoFilter;

    public BaseStation2View(SingleFrameApplication app) {
        super(app);
        fileChooser = new FileChooser();
        cartInfoFilter = new CartInfoFilter();
    }

    public void setAppConfiguration(PropertiesConfiguration config) {
        this.config = config;
    }

    public void setServices(Services services) {
        this.services = services;
        mainPanel.setServices(services);
    }

    public void setCourseSettingsManager(CourseSettingsManager courseSettingsManager) {
        mainPanel.setCourseSettingsManager(courseSettingsManager);
    }

    public void setNotificationsManager(NotificationsManager notificationsManager) {
        mainPanel.setNotificationsManager(notificationsManager);
    }

    public void cartInfoUpdated(List<CartInfo> carts) {
        mainPanel.cartInfoUpdated(carts);
    }

    public void init() {
        initComponents();
        initMenuItems();
        mainPanel.init();
        mainPanel.setConfig(config);
        mainPanel.setCartInfoFilter(cartInfoFilter);
        mainPanel.setFileChooser(fileChooser);
        statusBarPanel.init();
    }

    private void initMenuItems() {
        final ApplicationAction adminAction = mainPanel.getAction(MainActions.administratorLogin);
        final JMenuItem adminItem = new JMenuItem(adminAction);

        adminAction.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                adminItem.setText(adminAction.isSelected()?
                        "Administrator log out" : "Administrator log in...");
            }
        });

        systemMenu.add(adminItem, 0);
        viewMenu.add(Application.getInstance().getContext().getActionMap(mainPanel).get("zoomIn"));
        viewMenu.add(Application.getInstance().getContext().getActionMap(mainPanel).get("zoomOut"));

	if (config.getBoolean("showWeatherButton", false)) {
	    courseMenu.add(mainPanel.getAction(MainActions.weatherAlert));
        }

        if (config.getBoolean("showCartPathButton", false)) {
	    courseMenu.add(mainPanel.getAction(MainActions.cartPathOnly));
        }
//        JMenuItem item = new JCheckBoxMenuItem(Application.getInstance().getContext().getActionMap(mainPanel).get("weatherAlert"));
//        courseMenu.add(item);
//        item = new JCheckBoxMenuItem(Application.getInstance().getContext().getActionMap(mainPanel).get("cartPathOnly"));
//        courseMenu.add(item);
        courseMenu.addSeparator();
        courseMenu.add(Application.getInstance().getContext().getActionMap(mainPanel).get("showDashboardPanel"));

	if(config.getBoolean("showFoodAndBeverageButton", false))
	    courseMenu.add(Application.getInstance().getContext().getActionMap(mainPanel).get("showFoodAndBeverageScreen"));

	courseMenu.add(Application.getInstance().getContext().getActionMap(mainPanel).get("showPinLocationDialog"));
        courseMenu.addSeparator();
        courseMenu.add(Application.getInstance().getContext().getActionMap(mainPanel).get("showCourseManagementScreen"));
        courseMenu.add(Application.getInstance().getContext().getActionMap(mainPanel).get("showHoleManagementScreen"));
        courseMenu.add(Application.getInstance().getContext().getActionMap(mainPanel).get("showCartManagementScreen"));
//        courseMenu.add(Application.getInstance().getContext().getActionMap(mainPanel).get("showReport"));
        courseMenu.add(Application.getInstance().getContext().getActionMap(mainPanel).get("showDrawingTool"));

        courseMenu.addSeparator();
        courseMenu.add(ApplicationUtil.getAction(this, "exportGreens"));
    }

    @Override
    public ResourceMap getResourceMap() {
        return super.getResourceMap(); //To change body of generated methods, choose Tools | Templates.
    }

    public CartInfoFilter getCartInfoFilter() {
        return cartInfoFilter;
    }

    public void showStatusMessage(String message) {
        statusBarPanel.showStatusMessage(message);
    }

    @Action
    public void showAboutBox() {
        JFrame mainFrame = BaseStation2App.getApplication().getMainFrame();
        aboutBox = new BaseStation2AboutBox(mainFrame);
        aboutBox.setLocationRelativeTo(mainFrame);
        aboutBox.pack();
        BaseStation2App.getApplication().show(aboutBox);
        //aboutBox.setVisible(true);
    }

    @Action
    public void showSettingsDialog() {
        if(settingsDialog == null) {
            boolean showWebAppSettings = config.getBoolean(Constant.showWebAppSettings, false);
            boolean showATACSettings = config.getBoolean(Constant.showATACSettings, false);

            settingsDialog = new SettingsDialog(Application.getInstance(BaseStation2App.class).getMainFrame(), true);
            settingsDialog.initialize();
            settingsDialog.setShowWebAppSettings(showWebAppSettings);
            settingsDialog.setShowATACSettings(showATACSettings);
            settingsDialog.setCourseService(services.getCourseService());
            settingsDialog.setName("settingsDialog");
            settingsDialog.pack();
        }
        Application.getInstance(BaseStation2App.class).show(settingsDialog);
    }

    public void runSyncGolfClubTask() {
        javax.swing.Action action = ApplicationUtil.getAction(this, "syncCourse");
        action.actionPerformed(new ActionEvent(mainPanel, ActionEvent.ACTION_PERFORMED, "syncCourse"));
    }

    @Action (block = Task.BlockingScope.APPLICATION)
    public Task syncCourse() {
        return new SyncCourseTask(getApplication(), true);
    }

    @Action
    public void reloadCourse() {
        BaseStation2App.getApplication().loadGolfClub();
    }

    public void golfClubLoaded(GolfClub golfClub) {
        mainPanel.setGolfClub(golfClub);
    }

    public void setSendMessageControl(SendMessageControl messageControl) {
        mainPanel.setSendMessageControl(messageControl);
    }

    private class SyncCourseTask extends Task<GolfClub, Void> {
        SyncCourseTask(Application app, boolean sync) {
            super(app);
        }
        @Override protected GolfClub doInBackground() throws InterruptedException {
            BaseStation2App.getApplication().syncGolfClub();
            return null;
        }
        @Override protected void succeeded(GolfClub gc) {
            BaseStation2App.getApplication().loadGolfClub();
        }
    }

    @Action
    public Task exportGreens() {
        BaseStation2App app = BaseStation2App.getApplication();

        if (exportGreensDialog == null) {
            exportGreensDialog = new ExportGreensDialog(app.getMainFrame());
            exportGreensDialog.setFileChooser(fileChooser);
        }
        exportGreensDialog.setVisible(true);

        if (exportGreensDialog.getResult() == 1) {
            File file = exportGreensDialog.getSelectedFile();
            ExportGreensDialog.Layout l = exportGreensDialog.getPageLayout();
            boolean pins = exportGreensDialog.isPinPositionsSelected();
            return new ExportGreensTask(app, app.getGolfClub(), null, l.horiz, l.vert, file, pins);
        }
        else {
            return null;
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        menuBar = new javax.swing.JMenuBar();
        systemMenu = new javax.swing.JMenu();
        settingsMenuItem = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        javax.swing.JMenuItem exitMenuItem = new javax.swing.JMenuItem();
        viewMenu = new javax.swing.JMenu();
        courseMenu = new javax.swing.JMenu();
        reloadCourse = new javax.swing.JMenuItem();
        javax.swing.JMenu helpMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem aboutMenuItem = new javax.swing.JMenuItem();
        mainPanel = new com.stayprime.basestation2.ui.mainview.MainPanel();
        statusBarPanel = new com.stayprime.basestation2.ui.StatusBarPanel();

        menuBar.setName("menuBar"); // NOI18N

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance().getContext().getResourceMap(BaseStation2View.class);
        systemMenu.setText(resourceMap.getString("systemMenu.text")); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance().getContext().getActionMap(BaseStation2View.class, this);
        settingsMenuItem.setAction(actionMap.get("showSettingsDialog")); // NOI18N
        settingsMenuItem.setText(resourceMap.getString("settingsMenuItem.text")); // NOI18N
        settingsMenuItem.setName("settingsMenuItem"); // NOI18N
        systemMenu.add(settingsMenuItem);

        jSeparator1.setName("jSeparator1"); // NOI18N
        systemMenu.add(jSeparator1);

        exitMenuItem.setAction(actionMap.get("quit")); // NOI18N
        exitMenuItem.setName("exitMenuItem"); // NOI18N
        systemMenu.add(exitMenuItem);

        menuBar.add(systemMenu);

        viewMenu.setMnemonic('V');
        viewMenu.setText(resourceMap.getString("viewMenu.text")); // NOI18N
        viewMenu.setName("viewMenu"); // NOI18N
        menuBar.add(viewMenu);

        courseMenu.setText(resourceMap.getString("courseMenu.text")); // NOI18N
        courseMenu.setName("courseMenu"); // NOI18N

        reloadCourse.setAction(actionMap.get("reloadCourse")); // NOI18N
        reloadCourse.setName("reloadCourse"); // NOI18N
        courseMenu.add(reloadCourse);

        menuBar.add(courseMenu);

        helpMenu.setMnemonic('H');
        helpMenu.setText(resourceMap.getString("helpMenu.text")); // NOI18N
        helpMenu.setName("helpMenu"); // NOI18N

        aboutMenuItem.setAction(actionMap.get("showAboutBox")); // NOI18N
        aboutMenuItem.setName("aboutMenuItem"); // NOI18N
        helpMenu.add(aboutMenuItem);

        menuBar.add(helpMenu);

        mainPanel.setName("mainPanel"); // NOI18N

        statusBarPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 2, 2, 2));
        statusBarPanel.setName("statusBarPanel"); // NOI18N

        setComponent(mainPanel);
        setMenuBar(menuBar);
        setStatusBar(statusBarPanel);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu courseMenu;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    com.stayprime.basestation2.ui.mainview.MainPanel mainPanel;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenuItem reloadCourse;
    private javax.swing.JMenuItem settingsMenuItem;
    private com.stayprime.basestation2.ui.StatusBarPanel statusBarPanel;
    private javax.swing.JMenu systemMenu;
    private javax.swing.JMenu viewMenu;
    // End of variables declaration//GEN-END:variables

    public com.stayprime.basestation2.ui.mainview.MainPanel getMainPanel() {
        return mainPanel;
    }
}
