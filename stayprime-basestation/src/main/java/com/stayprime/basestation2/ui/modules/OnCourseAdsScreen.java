/*
 * OnCourseAdsScreen.java
 *
 * Created on 16/05/2011, 11:20:32 AM
 */
package com.stayprime.basestation2.ui.modules;

import com.stayprime.basestation.ui.FileChooser;
import com.stayprime.basestation2.BaseStation2App;
import com.stayprime.basestation2.services.CourseService;
import com.stayprime.basestation2.ui.modules.OnCourseAdsClientsPanel.ClientFilter;
import com.stayprime.basestation2.ui.modules.OnCourseAdsDetailPanel.AdFilter;
import com.stayprime.basestation2.util.NotificationPopup;
import com.stayprime.hibernate.entities.Ads;
import com.stayprime.hibernate.entities.Clients;
import com.stayprime.legacy.screen.Screen;
import com.stayprime.legacy.screen.ScreenParent;
import java.awt.Component;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
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
public class OnCourseAdsScreen extends javax.swing.JPanel implements Screen {

    private static final Logger log = LoggerFactory.getLogger(OnCourseAdsScreen.class);

    private ScreenParent screenParent;
    private OnCourseAdsStorageTasks storageTasks;

    private boolean informationChanged;
    public static final String PROP_INFORMATIONCHANGED = "informationChanged";

    /**
     * Creates new form OnCourseAdsScreen
     */
    public OnCourseAdsScreen() {
        initComponents();
    }

    public void init() {
        initStorageTasks();
        initCategories();
        addListeners();
    }

    private void initStorageTasks() {
        storageTasks = new OnCourseAdsStorageTasks(this);
        onCourseAdsClientsPanel.setStorageTasks(storageTasks);
    }

    public void setCourseService(CourseService courseService) {
        storageTasks.setCourseService(courseService);
    }

    public void setFileChooser(FileChooser fileChooser) {
        onCourseAdsDetailPanel.setFileChooser(fileChooser);
    }

    private void initCategories() {
//        Categories categories[] = new Categories[] {
//            new Categories(0, "Other"),
//            new Categories(1, "Auto"),
//            new Categories(2, "Beverage"),
//            new Categories(3, "Dining"),
//            new Categories(4, "Finance"),
//            new Categories(5, "Food"),
//            new Categories(6, "Health"),
//            new Categories(7, "Hospitality"),
//            new Categories(8, "Real Estate"),
//            new Categories(9, "Restaurant"),
//            new Categories(10, "Services"),
//            new Categories(11, "Telecom"),
//            new Categories(12, "Travel")
//        };

//        onCourseAdsDetailPanel.setCategoriesList(Arrays.asList(categories));
    }

    private void addListeners() {
        UpdateListener updateListener = new UpdateListener();

        onCourseAdsClientsPanel.addPropertyChangeListener(OnCourseAdsClientsPanel.PROP_CLIENTINFORMATIONCHANGED, updateListener);
        onCourseAdsDetailPanel.addPropertyChangeListener(OnCourseAdsDetailPanel.PROP_ADINFORMATIONCHANGED, updateListener);
        onCourseAdsDetailPanel.addContractChangeListener(updateListener);
        onCourseAdsDetailPanel.setForAllContractDeleted(updateListener);
        /*Added by javed on 04-04-2019 for updated the UI when ads deleted*/
        onCourseAdsDetailPanel.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent fe) {
                if (onCourseAdsDetailPanel.isAdInformationChanged() || onCourseAdsClientsPanel.isClientInformationChanged()) {
                    setInformationChanged(true);
                }
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void focusLost(FocusEvent fe) {
                if (onCourseAdsDetailPanel.isAdInformationChanged() || onCourseAdsClientsPanel.isClientInformationChanged()) {
                    setInformationChanged(true);
                }
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        });
        onCourseAdsClientsPanel.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent fe) {
                if (onCourseAdsDetailPanel.isAdInformationChanged() || onCourseAdsClientsPanel.isClientInformationChanged()) {
                    setInformationChanged(true);
                }
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void focusLost(FocusEvent fe) {
                if (onCourseAdsDetailPanel.isAdInformationChanged() || onCourseAdsClientsPanel.isClientInformationChanged()) {
                    setInformationChanged(true);
                }
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        });
                
    }

    /*
     * Public start methods
     */
    public void setClientList(List<Clients> list) {
        onCourseAdsClientsPanel.setClientList(list);
        setInformationChanged(false);
    }

    public void setCategoriesList(List<Integer> categories) {
        onCourseAdsDetailPanel.setCategoriesList(categories);
    }

    /*
     * Internal utility methods
     */
    private void setUpScreenAndLoadData() {
        if (isActiveScreen()) {
            storageTasks.load();
            onCourseAdsDetailPanel.setGolfClub(BaseStation2App.getApplication().getGolfClub());
        }
    }

    private boolean askSaveChanges() {
        if (isInformationChanged()) {
            int result = JOptionPane.showConfirmDialog(this, "Do you want to save the changes?", "Question", JOptionPane.YES_NO_CANCEL_OPTION);

            if (result == JOptionPane.YES_OPTION) {

                saveChanges();
                return false;
            } else if (result == JOptionPane.NO_OPTION) {
                onCourseAdsDetailPanel.discardChanges();
                onCourseAdsClientsPanel.discardChanges();
                return true;
            } else if (result == JOptionPane.CANCEL_OPTION) {
                return false;
            }
        }

        return true;
    }

    /*
     * Bound properties
     */
    public boolean isInformationChanged() {
        return informationChanged;
    }

    public void setInformationChanged(boolean informationChanged) {
        boolean oldInformationChanged = this.informationChanged;
        this.informationChanged = informationChanged;
        firePropertyChange(PROP_INFORMATIONCHANGED, oldInformationChanged, informationChanged);
    }

    /*
     * Screen implementation
     */
    public boolean isActiveScreen() {
        return screenParent != null;
    }

    @Override
    public void enterScreen(ScreenParent parent) {
        screenParent = parent;
        setUpScreenAndLoadData();
    }

    @Override
    public boolean exitScreen() {
        if (askSaveChanges()) {
            screenParent = null;
            return true;
        }
        if (BaseStation2App.getApplication().getMainView().getMainPanel().getAdminMode() == false) {
            // BaseStation2App.getApplication().getMainView().getMainPanel().showDashboardPanel();
            return true;
        }

        return true;
    }

    private void exitThisScreen() {
        if (isActiveScreen()) {
            screenParent.exitScreen(this);
        }
    }

    @Override
    public Component getToolbarComponent() {
        return null;
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
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        toolBarPanel = new javax.swing.JPanel();
        titleLabel = new javax.swing.JLabel();
        showLabel = new javax.swing.JLabel();
        clientsFilterComboBox = new javax.swing.JComboBox();
        adsFilterComboBox = new javax.swing.JComboBox();
        splitPane = new javax.swing.JSplitPane();
        onCourseAdsClientsPanel = new com.stayprime.basestation2.ui.modules.OnCourseAdsClientsPanel();
        onCourseAdsDetailPanel = new com.stayprime.basestation2.ui.modules.OnCourseAdsDetailPanel();
        bottomButtonsPanel = new javax.swing.JPanel();
        saveClientButton = new javax.swing.JButton();

        setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 5, 5, 5));
        setName("Form"); // NOI18N
        setLayout(new java.awt.BorderLayout(0, 5));

        toolBarPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 0, 0, 0));
        toolBarPanel.setName("toolBarPanel"); // NOI18N
        toolBarPanel.setLayout(new java.awt.GridBagLayout());

        titleLabel.setFont(titleLabel.getFont().deriveFont(titleLabel.getFont().getStyle() | java.awt.Font.BOLD, titleLabel.getFont().getSize()+5));
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance().getContext().getResourceMap(OnCourseAdsScreen.class);
        titleLabel.setText(resourceMap.getString("titleLabel.text")); // NOI18N
        titleLabel.setName("titleLabel"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        toolBarPanel.add(titleLabel, gridBagConstraints);

        showLabel.setText(resourceMap.getString("showLabel.text")); // NOI18N
        showLabel.setName("showLabel"); // NOI18N
        toolBarPanel.add(showLabel, new java.awt.GridBagConstraints());

        clientsFilterComboBox.setModel(new javax.swing.DefaultComboBoxModel(OnCourseAdsClientsPanel.ClientFilter.values()));
        clientsFilterComboBox.setName("clientsFilterComboBox"); // NOI18N
        toolBarPanel.add(clientsFilterComboBox, new java.awt.GridBagConstraints());

        adsFilterComboBox.setModel(new javax.swing.DefaultComboBoxModel(OnCourseAdsDetailPanel.AdFilter.values()));
        adsFilterComboBox.setName("adsFilterComboBox"); // NOI18N
        toolBarPanel.add(adsFilterComboBox, new java.awt.GridBagConstraints());

        add(toolBarPanel, java.awt.BorderLayout.NORTH);

        splitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        splitPane.setContinuousLayout(true);
        splitPane.setName("splitPane"); // NOI18N

        onCourseAdsClientsPanel.setName("onCourseAdsClientsPanel"); // NOI18N

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ, clientsFilterComboBox, org.jdesktop.beansbinding.ELProperty.create("${selectedItem}"), onCourseAdsClientsPanel, org.jdesktop.beansbinding.BeanProperty.create("clientFilter"));
        bindingGroup.addBinding(binding);

        splitPane.setTopComponent(onCourseAdsClientsPanel);

        onCourseAdsDetailPanel.setName("onCourseAdsDetailPanel"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ, adsFilterComboBox, org.jdesktop.beansbinding.ELProperty.create("${selectedItem}"), onCourseAdsDetailPanel, org.jdesktop.beansbinding.BeanProperty.create("adFilter"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ, onCourseAdsClientsPanel, org.jdesktop.beansbinding.ELProperty.create("${selectedClient}"), onCourseAdsDetailPanel, org.jdesktop.beansbinding.BeanProperty.create("selectedClient"));
        bindingGroup.addBinding(binding);

        splitPane.setRightComponent(onCourseAdsDetailPanel);

        add(splitPane, java.awt.BorderLayout.CENTER);

        bottomButtonsPanel.setName("bottomButtonsPanel"); // NOI18N
        bottomButtonsPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 0, 0));

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance().getContext().getActionMap(OnCourseAdsScreen.class, this);
        saveClientButton.setAction(actionMap.get("saveChanges")); // NOI18N
        saveClientButton.setName("saveClientButton"); // NOI18N
        bottomButtonsPanel.add(saveClientButton);

        add(bottomButtonsPanel, java.awt.BorderLayout.SOUTH);

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

    @Action(block = Task.BlockingScope.APPLICATION, enabledProperty = PROP_INFORMATIONCHANGED)
    public Task saveChanges() {
//	storageTasks.saveClients(onCourseAdsClientsPanel.getClientList());
        boolean save = true;
        for (Clients client : onCourseAdsClientsPanel.getClientList()) {
            if (client.getName().equals("") || StringUtils.isEmpty(client.getName()) || client.getName() == null) {
                NotificationPopup.showErrorDialog("Client Name cannot be left Empty");
                save = false;
                break;
            }
            for (Ads ad : client.getAdses()) {
                if (ad.getName().equals("") || StringUtils.isEmpty(ad.getName()) || ad.getName() == null) {
                    NotificationPopup.showErrorDialog("Client -" + client.getName() + "\nAd Name cannot be left Empty");
                    save = false;
                    break;
                }
            }
        }
        if (save) {
            TaskService ts = Application.getInstance().getContext().getTaskService();
            BaseStation2App.getApplication().runTask(storageTasks.getSaveClientsTask(onCourseAdsClientsPanel.getClientList()));
           // ts.execute(storageTasks.getSaveClientsTask(onCourseAdsClientsPanel.getClientList()));
            //return storageTasks.getSaveClientsTask(onCourseAdsClientsPanel.getClientList());
        }
        return null;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox adsFilterComboBox;
    private javax.swing.JPanel bottomButtonsPanel;
    private javax.swing.JComboBox clientsFilterComboBox;
    private com.stayprime.basestation2.ui.modules.OnCourseAdsClientsPanel onCourseAdsClientsPanel;
    private com.stayprime.basestation2.ui.modules.OnCourseAdsDetailPanel onCourseAdsDetailPanel;
    private javax.swing.JButton saveClientButton;
    private javax.swing.JLabel showLabel;
    private javax.swing.JSplitPane splitPane;
    private javax.swing.JLabel titleLabel;
    private javax.swing.JPanel toolBarPanel;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    private class UpdateListener implements PropertyChangeListener, ChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (onCourseAdsDetailPanel.isAdInformationChanged() || onCourseAdsClientsPanel.isClientInformationChanged()) {
                setInformationChanged(true);
            }
        }

        @Override
        public void stateChanged(ChangeEvent e) {
            onCourseAdsClientsPanel.contractChanged();
        }
    }
}
