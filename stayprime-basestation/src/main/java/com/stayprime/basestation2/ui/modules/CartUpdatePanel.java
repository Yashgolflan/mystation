/*
 * CartUpdatePanel.java
 *
 * Created on 13/02/2012, 07:42:10 AM
 */

package com.stayprime.basestation2.ui.modules;

import ca.odell.glazedlists.BasicEventList;
import com.stayprime.updatesystem.BasicUpdateDescription;
import com.stayprime.updatesystem.BasicUpdateProgress;
import com.stayprime.updatesystem.CopyFiles;
import com.stayprime.updatesystem.CopyThenRunFactory;
import com.stayprime.updatesystem.RunCommands;
import com.stayprime.updatesystem.UpdateDescription;
import com.stayprime.updatesystem.UpdateProgress;
import com.stayprime.updatesystem.ssh.BasicSSHTarget;
import com.stayprime.basestation2.BaseStation2App;
import com.stayprime.basestation2.services.CourseService;
import com.stayprime.basestation2.services.Services;
import com.stayprime.hibernate.entities.CartInfo;
import com.stayprime.hibernate.entities.CartUnit;
import com.stayprime.util.ConfigUtil;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.ListModel;
import javax.swing.SwingUtilities;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.Task;
import org.jdesktop.application.TaskEvent;
import org.jdesktop.application.TaskListener;

/**
 *
 * @author benjamin
 */
public class CartUpdatePanel extends javax.swing.JPanel {
    private static final Logger log = LoggerFactory.getLogger(CartUpdatePanel.class);

    public static final String PROP_CANAPPLYUPDATE = "canApplyUpdate";

    private JFileChooser fileChooser;
    private PropertiesConfiguration props;

    private BasicEventList<CartInfo> cartsEventList;
    private ApplyUpdatesTask updateTask;
    private CartTableFormat cartTableFormat;

    private Services services;

    /** Creates new form CartUpdatePanel */
    public CartUpdatePanel() {
        initComponents();
    }

    public void setServices(Services services) {
        this.services = services;
    }

    public void setCartsEventList(BasicEventList<CartInfo> cartsEventList) {
        this.cartsEventList = cartsEventList;
    }

    public void setCartTableFormat(CartTableFormat cartTableFormat) {
        this.cartTableFormat = cartTableFormat;
    }

    public void loadConfig() {
        String basePath = BaseStation2App.getApplication().getBasePath();
        File configFile = new File(basePath, "update.properties");
        this.props = ConfigUtil.load(configFile, log);

        updateField.setText(props.getString("updateName", ""));
	
	String files = props.getString("updateFiles", "");
	DefaultListModel model  = new DefaultListModel();
	if(files.isEmpty() == false) {
	    for(String f: files.split("\n"))
		model.addElement(f);
	}
	filesList.setModel(model);

	String commands = props.getString("updateCommands", "");
	commandsTextArea.setText(commands);
	remoteDirTextField.setText(props.getString("updateDir", "/"));
    }

    public void save() {
	if(props != null) {
	    props.setProperty("updateName", updateField.getText());

	    props.setProperty("updateFiles", StringUtils.join(getFiles(), "\n"));
	    props.setProperty("updateCommands", commandsTextArea.getText());
	    props.setProperty("updateDir", remoteDirTextField.getText());

	    try {
		props.save();
	    }
	    catch(Exception ex) {}
	}
    }

    @Action
    public void removeFiles() {
	int indices[] = filesList.getSelectedIndices();
	if(indices.length > 0) {
	    DefaultListModel filesModel = (DefaultListModel) filesList.getModel();

	    for(int i = indices.length - 1; i >= 0; i--) {
		filesModel.remove(indices[i]);
	    }
	}
    }

    @Action
    public void addFiles() {
	if(fileChooser == null) {
	    fileChooser = new JFileChooser();
	    fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
	    fileChooser.setMultiSelectionEnabled(true);
	}

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
	    DefaultListModel filesModel = (DefaultListModel) filesList.getModel();
            File files[] = fileChooser.getSelectedFiles();

	    for(File f: files) {
		filesModel.addElement(f.getAbsolutePath());
	    }
        }
    }

    @Action(block = Task.BlockingScope.COMPONENT, enabledProperty = "canApplyUpdate")
    public Task applyUpdates() {
        Application app = Application.getInstance();
        BasicUpdateDescription desc = createBasicUpdateDescription();
        ApplyUpdatesTask task = new ApplyUpdatesTask(app,
                services, cartsEventList, cartTableFormat,
                desc, getUser(), getPass());

        task.addTaskListener(new TaskListenerImpl());

        setUpdateTask(task);
        return task;
    }

    @Action
    public void stopUpdates() {
        if(updateTask != null) {
            updateTask.stopUpdating();
        }
    }
    
    public boolean isCanApplyUpdate() {
        return updateTask == null;
    }

    public void setUpdateTask(ApplyUpdatesTask task) {
        boolean oldCanApplyUpdate = isCanApplyUpdate();
        this.updateTask = task;
        boolean canApplyUpdate = isCanApplyUpdate();
        setEnabled(canApplyUpdate);
        firePropertyChange(PROP_CANAPPLYUPDATE, oldCanApplyUpdate, canApplyUpdate);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        settingsPanel = new org.jdesktop.swingx.JXPanel();
        updateLabel = new javax.swing.JLabel();
        updateField = new javax.swing.JTextField();
        glue2 = new com.stayprime.ui.Glue();
        userLabel = new javax.swing.JLabel();
        userField = new javax.swing.JTextField();
        glue3 = new com.stayprime.ui.Glue();
        passLabel = new javax.swing.JLabel();
        passField = new javax.swing.JPasswordField();
        filesCommandsPanel = new org.jdesktop.swingx.JXPanel();
        jXPanel1 = new org.jdesktop.swingx.JXPanel();
        jLabel1 = new javax.swing.JLabel();
        removeFilesButton = new javax.swing.JButton();
        addFilesButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        filesList = new javax.swing.JList();
        jXPanel2 = new org.jdesktop.swingx.JXPanel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        commandsTextArea = new javax.swing.JTextArea();
        removeFilesButton1 = new javax.swing.JButton();
        remoteDirPanel = new org.jdesktop.swingx.JXPanel();
        jLabel3 = new javax.swing.JLabel();
        remoteDirTextField = new javax.swing.JTextField();
        buttonsPanel = new org.jdesktop.swingx.JXPanel();
        saveButton = new javax.swing.JButton();
        reloadButton = new javax.swing.JButton();
        glue4 = new com.stayprime.ui.Glue();
        applyUpdateButton = new javax.swing.JButton();
        stopUpdateButton = new javax.swing.JButton();

        setName("Form"); // NOI18N
        com.stayprime.ui.VerticalLayout2 verticalLayout22 = new com.stayprime.ui.VerticalLayout2();
        verticalLayout22.setAlignment(3);
        setLayout(verticalLayout22);

        settingsPanel.setName("settingsPanel"); // NOI18N
        settingsPanel.setLayout(new javax.swing.BoxLayout(settingsPanel, javax.swing.BoxLayout.LINE_AXIS));

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance().getContext().getResourceMap(CartUpdatePanel.class);
        updateLabel.setText(resourceMap.getString("updateLabel.text")); // NOI18N
        updateLabel.setName("updateLabel"); // NOI18N

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${enabled}"), updateLabel, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        settingsPanel.add(updateLabel);

        updateField.setColumns(10);
        updateField.setMaximumSize(new java.awt.Dimension(160, 40));
        updateField.setName("updateField"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${enabled}"), updateField, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        settingsPanel.add(updateField);

        glue2.setName("glue2"); // NOI18N

        javax.swing.GroupLayout glue2Layout = new javax.swing.GroupLayout(glue2);
        glue2.setLayout(glue2Layout);
        glue2Layout.setHorizontalGroup(
            glue2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 101, Short.MAX_VALUE)
        );
        glue2Layout.setVerticalGroup(
            glue2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        settingsPanel.add(glue2);

        userLabel.setText(resourceMap.getString("userLabel.text")); // NOI18N
        userLabel.setName("userLabel"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${enabled}"), userLabel, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        settingsPanel.add(userLabel);

        userField.setColumns(6);
        userField.setMaximumSize(new java.awt.Dimension(120, 40));
        userField.setName("userField"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${enabled}"), userField, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        settingsPanel.add(userField);

        glue3.setMaximumSize(new java.awt.Dimension(5, 5));
        glue3.setName("glue3"); // NOI18N
        glue3.setPreferredSize(new java.awt.Dimension(5, 5));

        javax.swing.GroupLayout glue3Layout = new javax.swing.GroupLayout(glue3);
        glue3.setLayout(glue3Layout);
        glue3Layout.setHorizontalGroup(
            glue3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 5, Short.MAX_VALUE)
        );
        glue3Layout.setVerticalGroup(
            glue3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 5, Short.MAX_VALUE)
        );

        settingsPanel.add(glue3);

        passLabel.setText(resourceMap.getString("passLabel.text")); // NOI18N
        passLabel.setName("passLabel"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${enabled}"), passLabel, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        settingsPanel.add(passLabel);

        passField.setColumns(4);
        passField.setMaximumSize(new java.awt.Dimension(100, 40));
        passField.setName("passField"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${enabled}"), passField, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        settingsPanel.add(passField);

        add(settingsPanel);

        filesCommandsPanel.setName("filesCommandsPanel"); // NOI18N
        filesCommandsPanel.setLayout(new java.awt.GridLayout(1, 0, 5, 0));

        jXPanel1.setName("jXPanel1"); // NOI18N
        jXPanel1.setLayout(new java.awt.GridBagLayout());

        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${enabled}"), jLabel1, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        jXPanel1.add(jLabel1, gridBagConstraints);

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance().getContext().getActionMap(CartUpdatePanel.class, this);
        removeFilesButton.setAction(actionMap.get("removeFiles")); // NOI18N
        removeFilesButton.setName("removeFilesButton"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${enabled}"), removeFilesButton, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        jXPanel1.add(removeFilesButton, gridBagConstraints);

        addFilesButton.setAction(actionMap.get("addFiles")); // NOI18N
        addFilesButton.setName("addFilesButton"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${enabled}"), addFilesButton, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        jXPanel1.add(addFilesButton, gridBagConstraints);

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        filesList.setModel(new DefaultListModel());
        filesList.setName("filesList"); // NOI18N
        filesList.setPreferredSize(new java.awt.Dimension(300, 200));
        filesList.setVisibleRowCount(4);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${enabled}"), filesList, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jScrollPane1.setViewportView(filesList);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jXPanel1.add(jScrollPane1, gridBagConstraints);

        filesCommandsPanel.add(jXPanel1);

        jXPanel2.setName("jXPanel2"); // NOI18N
        jXPanel2.setLayout(new java.awt.GridBagLayout());

        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${enabled}"), jLabel2, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        jXPanel2.add(jLabel2, gridBagConstraints);

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        commandsTextArea.setColumns(10);
        commandsTextArea.setRows(4);
        commandsTextArea.setName("commandsTextArea"); // NOI18N
        commandsTextArea.setPreferredSize(new java.awt.Dimension(300, 200));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${enabled}"), commandsTextArea, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jScrollPane2.setViewportView(commandsTextArea);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jXPanel2.add(jScrollPane2, gridBagConstraints);

        removeFilesButton1.setAction(actionMap.get("clearCommands")); // NOI18N
        removeFilesButton1.setText(resourceMap.getString("removeFilesButton1.text")); // NOI18N
        removeFilesButton1.setName("removeFilesButton1"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${enabled}"), removeFilesButton1, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        jXPanel2.add(removeFilesButton1, gridBagConstraints);

        filesCommandsPanel.add(jXPanel2);

        add(filesCommandsPanel);

        remoteDirPanel.setName("remoteDirPanel"); // NOI18N
        remoteDirPanel.setLayout(new javax.swing.BoxLayout(remoteDirPanel, javax.swing.BoxLayout.LINE_AXIS));

        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${enabled}"), jLabel3, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        remoteDirPanel.add(jLabel3);

        remoteDirTextField.setName("remoteDirTextField"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${enabled}"), remoteDirTextField, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        remoteDirPanel.add(remoteDirTextField);

        add(remoteDirPanel);

        buttonsPanel.setName("buttonsPanel"); // NOI18N
        buttonsPanel.setLayout(new javax.swing.BoxLayout(buttonsPanel, javax.swing.BoxLayout.LINE_AXIS));

        saveButton.setText(resourceMap.getString("saveButton.text")); // NOI18N
        saveButton.setName("saveButton"); // NOI18N
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });
        buttonsPanel.add(saveButton);

        reloadButton.setText(resourceMap.getString("reloadButton.text")); // NOI18N
        reloadButton.setName("reloadButton"); // NOI18N
        reloadButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reloadButtonActionPerformed(evt);
            }
        });
        buttonsPanel.add(reloadButton);

        glue4.setName("glue4"); // NOI18N

        javax.swing.GroupLayout glue4Layout = new javax.swing.GroupLayout(glue4);
        glue4.setLayout(glue4Layout);
        glue4Layout.setHorizontalGroup(
            glue4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        glue4Layout.setVerticalGroup(
            glue4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        buttonsPanel.add(glue4);

        applyUpdateButton.setAction(actionMap.get("applyUpdates")); // NOI18N
        applyUpdateButton.setName("applyUpdateButton"); // NOI18N
        buttonsPanel.add(applyUpdateButton);

        stopUpdateButton.setAction(actionMap.get("stopUpdates")); // NOI18N
        stopUpdateButton.setName("stopUpdateButton"); // NOI18N
        buttonsPanel.add(stopUpdateButton);

        add(buttonsPanel);

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveButtonActionPerformed
        save();
    }//GEN-LAST:event_saveButtonActionPerformed

    private void reloadButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reloadButtonActionPerformed
        loadConfig();
    }//GEN-LAST:event_reloadButtonActionPerformed

    public String getUser() {
	return userField.getText();
    }

    public String getPass() {
	char[] password = passField.getPassword();
	String pass = new String(password);
	return pass;
    }

    public BasicUpdateDescription createBasicUpdateDescription() {
	return new BasicUpdateDescription(null, updateField.getText(), "",
		createCopyThenRunFactory());
    }

    public String[] getFiles() {
	ListModel filesModel = filesList.getModel();
	String files[] = new String[filesModel.getSize()];
	for(int i = 0; i < filesModel.getSize(); i++)
	    files[i] = filesModel.getElementAt(i).toString();

	return files;
    }

    public String[] getCommands() {
	String commandsText = commandsTextArea.getText().trim();
	String[] commands = commandsText.isEmpty()?
	    new String[0] : commandsText.split("\n");

	return commands;
    }

    public CopyThenRunFactory createCopyThenRunFactory() {
	CopyFiles copy = new CopyFiles(remoteDirTextField.getText(), getFiles());
	RunCommands run = new RunCommands(getCommands());

	return new CopyThenRunFactory(copy, run);
    }

    @Action
    public void clearCommands() {
	commandsTextArea.setText("");
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addFilesButton;
    private javax.swing.JButton applyUpdateButton;
    private org.jdesktop.swingx.JXPanel buttonsPanel;
    private javax.swing.JTextArea commandsTextArea;
    private org.jdesktop.swingx.JXPanel filesCommandsPanel;
    private javax.swing.JList filesList;
    private com.stayprime.ui.Glue glue2;
    private com.stayprime.ui.Glue glue3;
    private com.stayprime.ui.Glue glue4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private org.jdesktop.swingx.JXPanel jXPanel1;
    private org.jdesktop.swingx.JXPanel jXPanel2;
    private javax.swing.JPasswordField passField;
    private javax.swing.JLabel passLabel;
    private javax.swing.JButton reloadButton;
    private org.jdesktop.swingx.JXPanel remoteDirPanel;
    private javax.swing.JTextField remoteDirTextField;
    private javax.swing.JButton removeFilesButton;
    private javax.swing.JButton removeFilesButton1;
    private javax.swing.JButton saveButton;
    private org.jdesktop.swingx.JXPanel settingsPanel;
    private javax.swing.JButton stopUpdateButton;
    private javax.swing.JTextField updateField;
    private javax.swing.JLabel updateLabel;
    private javax.swing.JTextField userField;
    private javax.swing.JLabel userLabel;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    private static class ApplyUpdatesTask extends org.jdesktop.application.Task<Object, Void> {
        private boolean running;
        private BasicSSHTarget target;

        private UpdateDescription updateDescription;
        private String user, pass;
        private final UpdateProgress.Observer progressObserver;
        private CartInfo currentCart;
        private Map<String, CartUpdateStatus> updateStatus;

        private BasicEventList<CartInfo> cartsEventList;
        private Services services;
        private final CartTableFormat cartTableFormat;

        ApplyUpdatesTask(Application app, Services services,
                BasicEventList<CartInfo> cartsEventList, CartTableFormat cartTableFormat,
                UpdateDescription description, String user, String pass) {
            super(app);
            updateStatus = new HashMap<String, CartUpdateStatus>();
            this.services = services;
            this.cartsEventList = cartsEventList;
            this.cartTableFormat = cartTableFormat;
            this.updateDescription = description;
            this.user = user;
            this.pass = pass;
            running = true;

            cartTableFormat.setUpdateStatusMap(updateStatus);

            progressObserver = new UpdateProgress.Observer() {
                @Override
                public void progressUpdated(UpdateProgress progress) {
                    SwingUtilities.invokeLater(new ListUpdater(currentCart,
                            getUpdateStatus(currentCart).getLastUpdate(),
                            updateDescription.getUpdateName(),
                            progress.getProgress(), null));
                }
            };
        }

        private CartUpdateStatus getUpdateStatus(CartInfo cart) {
            if (updateStatus.containsKey(cart.getMacAddress())) {
                return updateStatus.get(cart.getMacAddress());
            }
            else if (cart.getMacAddress() != null & cart.getCartUnit() != null) {
                CartUpdateStatus status = new CartUpdateStatus(cart.getMacAddress(), cart.getCartUnit().getSoftwareVersion());
                updateStatus.put(cart.getMacAddress(), status);
                return status;
            }
            return null;
        }

        @Override
        protected Object doInBackground() {
            long started = System.currentTimeMillis();
            try {
                CourseService courseService = services.getCourseService();
                courseService.saveSetting("StayConnected", "update");
                courseService.saveSetting("SoftwareVersion", updateDescription.getUpdateName());
            } catch (Exception ex) {
                setMessage("Error updating database settings for update: " + ex.toString());
            }

            while (running) {
                target = null;
                CartUnit cartUnit = null;
                boolean allUpdated = true;
                //Look for a cart to update
                cartsEventList.getReadWriteLock().readLock().lock();
                try {
                    if (cartsEventList.isEmpty() == false) {
                        int size = cartsEventList.size();
                        int startIndex = 0, index = 0;

                        if (currentCart != null) {
                            int currentCartIndex = cartsEventList.indexOf(currentCart);
                            startIndex = (currentCartIndex + 1) % size;
                            index = startIndex;
                        }

                        do {
                            CartInfo cart = cartsEventList.get(index);
                            if (isNotUpdated(cart)) {
                                allUpdated = false;
                                cartUnit = cart.getCartUnit();
                                if (cartUnit != null && cartUnit.getIpAddress() != null && cartUnit.getIpUpdated().getTime() > started) {
                                    currentCart = cart;
                                    target = new BasicSSHTarget(cartUnit.getIpAddress(), user, pass);
                                    CartUpdateStatus status = getUpdateStatus(cart);
                                    status.setError(null);
                                    status.setInProgressUpdate(updateDescription.getUpdateName());
                                    break;
                                }
                            }
                            index = (index + 1) % size;
                        } while (index != startIndex);
                    }
                } finally {
                    cartsEventList.getReadWriteLock().readLock().unlock();
                }

                Exception exception = null;
                if (target != null && cartUnit != null) {
                    BasicUpdateProgress updateProgress = target.getUpdateProgress(updateDescription);
                    if (updateProgress != null) {
                        updateProgress.addProgressObserver(progressObserver);
                    }

                    try {
                        target.startUpdating(updateDescription);
                        if (target.isUpdateApplied(updateDescription)) {
                            cartUnit.setSoftwareVersion(updateDescription.getUpdateName());
                            services.getCartService().saveUnit(cartUnit);
                        }
                    } catch (Exception ex) {
                        log.warn(ex.toString());
                        log.debug(ex.toString(), ex);
                        exception = ex;
                    } finally {
                        if (updateProgress != null)//Target had update progress? Remove observer
                        {
                            updateProgress.removeProgressObserver(progressObserver);
                        }

                        try {
                            if (target.isUpdateApplied(updateDescription)) {
                                log.info("Update applied to: " + cartUnit.getIpAddress());
                                SwingUtilities.invokeAndWait(new ListUpdater(currentCart,
                                        updateDescription.getUpdateName(), null, 0f,
                                        exception == null ? null : exception.getMessage()));
                            } else {
                                SwingUtilities.invokeAndWait(new ListUpdater(currentCart,
                                        null, updateDescription.getUpdateName(), 0f,
                                        exception == null ? null : exception.getMessage()));

                            }
                        } catch (Exception ex) {
                            log.warn(ex.toString());
                        }
                    }
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                }
            }

            try {
                services.getCourseService().saveSetting("StayConnected", "false");
            } catch (Exception ex) {
                setMessage("Error updating database settings for update: " + ex.toString());
            }
            return null;
        }

        @Override
        protected void succeeded(Object result) {
            cartsEventList.getReadWriteLock().readLock().lock();
            try {
                for (CartInfo cart : cartsEventList) {
                    CartUpdateStatus status = getUpdateStatus(cart);
                    if (status != null) {
                        status.setLastUpdate(cart.getCartUnit().getSoftwareVersion());
                    }
                }
            } catch (Exception ex) {
                log.warn(ex.toString());
            } finally {
                cartsEventList.getReadWriteLock().readLock().unlock();
                cartTableFormat.setUpdateStatusMap(null);
            }

        }

        private boolean isNotUpdated(CartInfo cart) {
            return cart.getCartUnit() == null
                    || ObjectUtils.notEqual(cart.getCartUnit().getSoftwareVersion(),
                            updateDescription.getUpdateName());
        }

        public void stopUpdating() {
            running = false;
            BasicSSHTarget t = target;
            if (t != null) {
                t.stopUpdating();
            }
        }

        private class ListUpdater implements Runnable {

            private final CartInfo cart;
            private final String last, prog, error;
            private final float progress;

            public ListUpdater(CartInfo cart, String last, String prog, float progress, String error) {
                this.cart = cart;
                this.last = last;
                this.prog = prog;
                this.progress = progress;
                this.error = error;
            }

            @Override
            public void run() {
                cartsEventList.getReadWriteLock().writeLock().lock();
                try {
                    CartUpdateStatus status = getUpdateStatus(cart);
                    status.setLastUpdate(last);
                    status.setInProgressUpdate(prog);
                    status.setProgress(progress);
                    status.setError(error);
                    int index = cartsEventList.indexOf(cart);

                    if (index >= 0) {
                        cartsEventList.set(index, cart);
                    }
                } catch (Exception ex) {
                    log.warn(ex.toString());
                } finally {
                    cartsEventList.getReadWriteLock().writeLock().unlock();
                }
            }
        }

    }

    private class TaskListenerImpl implements TaskListener<Object, Void> {
        @Override
        public void doInBackground(TaskEvent<Void> event) {}

        @Override
        public void process(TaskEvent<List<Void>> event) {}
        
        @Override
        public void succeeded(TaskEvent<Object> event) {}
        
        @Override
        public void failed(TaskEvent<Throwable> event) {}
        
        @Override
        public void cancelled(TaskEvent<Void> event) {}
        
        @Override
        public void interrupted(TaskEvent<InterruptedException> event) {}
        
        @Override
        public void finished(TaskEvent<Void> event) {
            updateTask.removeTaskListener(this);
            setUpdateTask(null);
        }
    }

}
