/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

 /*
 * MenuScreen.java
 *
 * Created on 28/01/2011, 08:51:31 AM
 */
package com.stayprime.basestation2.ui.modules;

import com.stayprime.basestation2.BaseStation2App;
import com.stayprime.basestation2.ui.mainview.MainPanel;
import com.ezware.dialog.task.TaskDialogs;
import com.stayprime.basestation2.services.CourseService;
import com.stayprime.basestation2.util.NotificationPopup;
import com.stayprime.hibernate.entities.HutsInfo;
import com.stayprime.hibernate.entities.MenuItems;
import com.stayprime.legacy.screen.Screen;
import com.stayprime.legacy.screen.ScreenParent;
import com.stayprime.storage.util.LocalStorage;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.JOptionPane;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.Task;
import org.jdesktop.application.TaskService;

/**
 *
 * @author benjamin
 */
public class MenuScreen extends javax.swing.JPanel implements Screen {

    private static final Logger log = LoggerFactory.getLogger(MainPanel.class);

    private ScreenParent screenParent;

    private CourseService courseService;

    /**
     * Creates new form MenuScreen
     */
    public MenuScreen() {
        initComponents();
    }

    public void init() {
        //    menuItemsList.init();
        //System.out.println(menuItemsList.)

    }

    public void setCourseService(CourseService courseService) {
        this.courseService = courseService;
    }

    @Action(block = Task.BlockingScope.APPLICATION)
    public Task refreshData() {
        return new RefreshDataTask(Application.getInstance(BaseStation2App.class));
    }

    private class RefreshDataTask extends Task<List<MenuItems>, Void> {

        List<HutsInfo> hutsInfo;

        RefreshDataTask(Application app) {
            super(app);
        }

        @Override
        protected List<MenuItems> doInBackground() {
            try {
                hutsInfo = courseService.listHutsInfo();
                return courseService.listMenuItems();
            } catch (Exception ex) {
                System.out.println(ex.toString());
                LocalStorage ls = BaseStation2App.getApplication().getMainView().getMainPanel().getLocalStorage();
                hutsInfo = ls.listHutsInfo();
                return ls.listMenuItems();
            }
        }

        @Override
        protected void succeeded(List<MenuItems> menuItems) {
            hutInfoPanel.setGolfClub(BaseStation2App.getApplication().getGolfClub());
            hutInfoPanel.setList(hutsInfo);
            menuItemsList.setList(menuItems);
        }

        @Override
        protected void failed(Throwable ex) {
            NotificationPopup.showErrorPopup("Menu Items");
        }
    }

    @Action(block = Task.BlockingScope.APPLICATION)
    public Task saveAndExit() {
        if (menuItemsList.getSourceList() != null) {
            for (MenuItems menuItem : menuItemsList.getSourceList()) {
                String[] name = {};
                if (menuItem != null) {
                    if (menuItem.getDescription() != null) {
                        name = menuItem.getDescription().split(";");
                    }

                    if (menuItem.getName() == null || StringUtils.isBlank(menuItem.getName()) || menuItem.getDescription() == null || StringUtils.isBlank(name[0])) {
                        NotificationPopup.showIncompeleteDetailsErrorPopup("Menu Item Details not Complete");
                        return null;
                    }
                }
            }
        }
        SaveAndExitTask task = new SaveAndExitTask(org.jdesktop.application.Application.getInstance());
        TaskService ts= Application.getInstance().getContext().getTaskService();
      //  ts.execute(task);
        BaseStation2App.getApplication().runTask(task);
        return null/*new SaveAndExitTask(org.jdesktop.application.Application.getInstance())*/;
    }

    private class SaveAndExitTask extends org.jdesktop.application.Task<Object, Void> {

        private List<MenuItems> menuItems = menuItemsList.getSourceList();
        private List<HutsInfo> hutsList = hutInfoPanel.getSourceList();

        SaveAndExitTask(org.jdesktop.application.Application app) {
            super(app);
        }

        @Override
        protected Object doInBackground() {
            setMessage("Saving the changes..");
            
            courseService.saveHutsInfo(hutsList); //Huts info first
            //MenuItems last because it writes the timestamp:
            courseService.saveMenuItems(menuItems);
            courseService.getLocalStorage().saveHutsInfo(hutsList);
            courseService.getLocalStorage().saveMenuItems(menuItems);
            return null;
        }

        @Override
        protected void succeeded(Object result) {
            hutInfoPanel.setModified(false);
            menuItemsList.setModified(false);
            
            courseService.getInstantSyncService().syncFnb();
            //exitThisScreen();
        }

        @Override
        protected void failed(Throwable ex) {
//            TaskDialogs.showException(ex);
            NotificationPopup.showErrorPopup("Menu Items");
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        iconPopup = new javax.swing.JPopupMenu();
        chooseFile = new javax.swing.JMenuItem();
        clear = new javax.swing.JMenuItem();
        hutTabbedPane = new javax.swing.JTabbedPane();
        menuItemsList = new com.stayprime.basestation2.ui.fnb.MenuItemsList();
        hutInfoPanel = new com.stayprime.basestation2.ui.fnb.HutInfoList();
        buttonsPanel = new javax.swing.JPanel();
        doneItemButton = new javax.swing.JButton();
        fnbLabel = new javax.swing.JLabel();

        iconPopup.setName("iconPopup"); // NOI18N

        chooseFile.setText("Choose Icon...");
        chooseFile.setName("chooseFile"); // NOI18N
        iconPopup.add(chooseFile);

        clear.setText("Clear (use default)");
        clear.setName("clear"); // NOI18N
        iconPopup.add(clear);

        setLayout(new java.awt.BorderLayout());

        hutTabbedPane.setMaximumSize(new java.awt.Dimension(110, 119));
        hutTabbedPane.setName("hutTabbedPane"); // NOI18N
        hutTabbedPane.setPreferredSize(new java.awt.Dimension(110, 119));
        hutTabbedPane.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                hutTabbedPaneStateChanged(evt);
            }
        });

        menuItemsList.setFocusable(false);
        menuItemsList.setMaximumSize(new java.awt.Dimension(102, 53));
        menuItemsList.setName("menuItemsList"); // NOI18N
        menuItemsList.setRequestFocusEnabled(false);
        menuItemsList.setVerifyInputWhenFocusTarget(false);
        menuItemsList.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                menuItemsListPropertyChange(evt);
            }
        });
        hutTabbedPane.addTab("Menu Items", menuItemsList);

        hutInfoPanel.setName("hutInfoPanel"); // NOI18N
        hutTabbedPane.addTab("F&B Huts", hutInfoPanel);

        add(hutTabbedPane, java.awt.BorderLayout.CENTER);

        buttonsPanel.setName("buttonsPanel"); // NOI18N
        buttonsPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance().getContext().getActionMap(MenuScreen.class, this);
        doneItemButton.setAction(actionMap.get("saveAndExit")); // NOI18N
        doneItemButton.setName("doneItemButton"); // NOI18N
        doneItemButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                doneItemButtonActionPerformed(evt);
            }
        });
        buttonsPanel.add(doneItemButton);

        add(buttonsPanel, java.awt.BorderLayout.SOUTH);

        fnbLabel.setFont(fnbLabel.getFont().deriveFont(fnbLabel.getFont().getStyle() | java.awt.Font.BOLD, fnbLabel.getFont().getSize()+5));
        fnbLabel.setText("Food & Beverage");
        fnbLabel.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        fnbLabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(12, 12, 6, 12));
        fnbLabel.setName("fnbLabel"); // NOI18N
        add(fnbLabel, java.awt.BorderLayout.NORTH);
    }// </editor-fold>//GEN-END:initComponents

    private void hutTabbedPaneStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_hutTabbedPaneStateChanged
    }//GEN-LAST:event_hutTabbedPaneStateChanged

    private void doneItemButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_doneItemButtonActionPerformed

    }//GEN-LAST:event_doneItemButtonActionPerformed

    private void menuItemsListPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_menuItemsListPropertyChange
        // TODO add your handling code here:
        System.out.println("hello");


    }//GEN-LAST:event_menuItemsListPropertyChange

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel buttonsPanel;
    private javax.swing.JMenuItem chooseFile;
    private javax.swing.JMenuItem clear;
    private javax.swing.JButton doneItemButton;
    private javax.swing.JLabel fnbLabel;
    private com.stayprime.basestation2.ui.fnb.HutInfoList hutInfoPanel;
    private javax.swing.JTabbedPane hutTabbedPane;
    private javax.swing.JPopupMenu iconPopup;
    private com.stayprime.basestation2.ui.fnb.MenuItemsList menuItemsList;
    // End of variables declaration//GEN-END:variables
    @Override
    public boolean exitScreen() {
        this.screenParent = null;
        return askSaveChanges();
    }

    private boolean askSaveChanges() {
        if (menuItemsList.isModified() || hutInfoPanel.isModified()) {
            int result = JOptionPane.showConfirmDialog(this, "Do you want to save the changes?", "Question", JOptionPane.YES_NO_CANCEL_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                saveAndExit();
                return true;
            } else if (result == JOptionPane.NO_OPTION) {
                return true;
            } else if (result == JOptionPane.CANCEL_OPTION) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void enterScreen(ScreenParent screenParent) {
        this.screenParent = screenParent;
        Application.getInstance().getContext().getActionMap(this).get("refreshData").
                actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "refreshData"));
    }

    @Override
    public Component getToolbarComponent() {
        return null;
    }

    private void exitThisScreen() {
        if (screenParent != null) {
            screenParent.exitScreen(this);
        }
    }

}
