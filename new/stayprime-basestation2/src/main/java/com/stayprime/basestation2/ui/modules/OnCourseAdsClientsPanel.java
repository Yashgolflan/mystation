/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * OnCourseAdsClientPanel.java
 *
 * Created on 28/05/2011, 05:27:03 PM
 */

package com.stayprime.basestation2.ui.modules;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.event.ListEvent;
import ca.odell.glazedlists.event.ListEventListener;
import ca.odell.glazedlists.gui.AdvancedTableFormat;
import ca.odell.glazedlists.gui.WritableTableFormat;
import ca.odell.glazedlists.impl.sort.ComparableComparator;
import ca.odell.glazedlists.matchers.Matcher;
import ca.odell.glazedlists.swing.DefaultEventTableModel;
import com.stayprime.hibernate.entities.Clients;
import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.DateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.apache.commons.lang.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jdesktop.application.Action;
import org.jdesktop.application.Task;

/**
 *
 * @author benjamin
 */
public class OnCourseAdsClientsPanel extends javax.swing.JPanel {
    private static final Logger log = LoggerFactory.getLogger(OnCourseAdsClientsPanel.class);

    /* Date format settings */

    private DateFormat dateFormat = DateFormat.getDateInstance();

    /* Table and lists */

    private BasicEventList<Clients> clientsList;
    private FilterList<Clients> filterList;
    private DefaultEventTableModel<Clients> clientsTableModel;

    /* Storage helper object */

    private OnCourseAdsStorageTasks storageTasks;

    /* Bound properties */

    private Clients selectedClient;
    public static final String PROP_SELECTEDCLIENT = "selectedClient";
    public static final String PROP_CLIENTSELECTED = "clientSelected";
    
    private boolean clientInformationChanged = false;
    public static final String PROP_CLIENTINFORMATIONCHANGED = "clientInformationChanged";

    private ClientFilter clientFilter = ClientFilter.SHOW_ALL;
    public static final String PROP_CLIENTFILTER = "clientFilter";

    /**
     * Initializes UI form, clients table and listeners.
     */
    public OnCourseAdsClientsPanel() {
        initComponents();
        initClientsTable();
	initListeners();
    }

    private void initClientsTable() {
	clientsList = new BasicEventList<Clients>();
        filterList = new FilterList<Clients>(clientsList, clientFilter);
        clientsTableModel = new DefaultEventTableModel<Clients>(filterList, new ClientsTableFormat());
        clientsTable.setModel(clientsTableModel);
	clientsTable.getColumnModel().getColumn(1).setCellEditor(new ClientActiveEditor());
    }

    private void initListeners() {
	UpdateModelListener updateListener = new UpdateModelListener();
	contactInfoArea.getDocument().addDocumentListener(updateListener);
	emailField.getDocument().addDocumentListener(updateListener);
	reportIntervalCombo.addItemListener(updateListener);
        clientsTable.getSelectionModel().addListSelectionListener(updateListener);
        clientsList.addListEventListener(updateListener);
    }

    /*
     * Public setup methods
     */

    public void setStorageTasks(OnCourseAdsStorageTasks storageTasks) {
        this.storageTasks = storageTasks;
    }

    public List<Clients> getClientList() {
        return clientsList;
    }

    public void setClientList(List<Clients> clientList) {
	clientsList.clear();
        if (clientList != null) {
            clientsList.addAll(clientList);
        }
	setClientInformationChanged(false);
    }

    public void contractChanged() {
	if(isClientSelected()) {
            Boolean active = selectedClient.getActive();
            if(BooleanUtils.isFalse(active) && selectedClient.getAdses().size() > 0) {
		selectedClient.setActive(true);
            }
	}

	clientsTable.repaint();
    }

    /*
     * Bound properties
     */

    public boolean isClientSelected() {
	return selectedClient != null;
    }

    public Clients getSelectedClient() {
        return selectedClient;
    }

    private void setSelectedClient(Clients client) {
        Clients oldSelectedClient = selectedClient;
        boolean clientInfoChanged = isClientInformationChanged();

        //Set to null to avoid the transient document updates
        selectedClient = null;

        if(client != null) {
	    contactInfoArea.setText(client.getContactInfo());
	    emailField.setText(client.getEmail());
	    reportIntervalCombo.setSelectedIndex(client.getReportPrefs());
	    setDetailedClientInfoEnabled(true);
	}
	else {
	    contactInfoArea.setText("");
	    emailField.setText("");
	    reportIntervalCombo.setSelectedIndex(0);
	    setDetailedClientInfoEnabled(false);
	}

        this.selectedClient = client;
        firePropertyChange(PROP_SELECTEDCLIENT, oldSelectedClient, selectedClient);
        firePropertyChange(PROP_CLIENTSELECTED, oldSelectedClient != null, selectedClient != null);

        setClientInformationChanged(clientInfoChanged);
    }

    public void discardChanges() {
        setClientInformationChanged(false);
    }

    public boolean isClientInformationChanged() {
	return clientInformationChanged;
    }

    public void setClientInformationChanged(boolean clientInformationChanged) {
	boolean oldClientInformationChanged = this.clientInformationChanged;
	this.clientInformationChanged = clientInformationChanged;
	firePropertyChange(PROP_CLIENTINFORMATIONCHANGED, oldClientInformationChanged, clientInformationChanged);
    }

    public ClientFilter getClientFilter() {
	return clientFilter;
    }

    public void setClientFilter(ClientFilter clientFilter) {
	ClientFilter oldClientFilter = this.clientFilter;
	this.clientFilter = clientFilter;
	firePropertyChange(PROP_CLIENTFILTER, oldClientFilter, clientFilter);
	applyFilter();
    }

    private void setDetailedClientInfoEnabled(boolean b) {
	contactInfoSeparator.setEnabled(b);
	contactInfoArea.setEnabled(b);
	emailLabel.setEnabled(b);
	emailField.setEnabled(b);
	sendReportsLabel.setEnabled(b);
	reportIntervalCombo.setEnabled(b);
    }

    /**Set the selected client fields from the table and components.
     * Avoid multiple calling of this method from the table changed events.
     */
    private void updateSelectedClient() {
	if(selectedClient != null) {
	    selectedClient.setContactInfo(contactInfoArea.getText());
	    selectedClient.setEmail(emailField.getText());
	    selectedClient.setReportPrefs(reportIntervalCombo.getSelectedIndex());
	    setClientInformationChanged(true);
	}
    }

    private void applyFilter() {
	clientsTable.clearSelection();
	filterList.setMatcher(clientFilter);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        clientDetailsPanel = new org.jdesktop.swingx.JXPanel();
        emailField = new javax.swing.JTextField();
        sendReportsLabel = new javax.swing.JLabel();
        emailLabel = new javax.swing.JLabel();
        reportIntervalCombo = new javax.swing.JComboBox();
        contactInfoSeparator = new com.stayprime.basestation2.ui.custom.JXTitledSeparator();
        jScrollPane2 = new javax.swing.JScrollPane();
        contactInfoArea = new javax.swing.JTextArea();
        clientsSeparator = new com.stayprime.basestation2.ui.custom.JXTitledSeparator();
        clientsScrollPane = new javax.swing.JScrollPane();
        clientsTable = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        deleteClientButton = new javax.swing.JButton();
        newClientButton = new javax.swing.JButton();

        clientDetailsPanel.setName("clientDetailsPanel"); // NOI18N

        emailField.setColumns(10);
        emailField.setEnabled(false);
        emailField.setName("emailField"); // NOI18N

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance().getContext().getResourceMap(OnCourseAdsClientsPanel.class);
        sendReportsLabel.setText(resourceMap.getString("sendReportsLabel.text")); // NOI18N
        sendReportsLabel.setEnabled(false);
        sendReportsLabel.setName("sendReportsLabel"); // NOI18N

        emailLabel.setText(resourceMap.getString("emailLabel.text")); // NOI18N
        emailLabel.setEnabled(false);
        emailLabel.setName("emailLabel"); // NOI18N

        reportIntervalCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Never", "Weekly", "Monthly" }));
        reportIntervalCombo.setEnabled(false);
        reportIntervalCombo.setName("reportIntervalCombo"); // NOI18N

        contactInfoSeparator.setEnabled(false);
        contactInfoSeparator.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        contactInfoSeparator.setName("contactInfoSeparator"); // NOI18N
        contactInfoSeparator.setTitle(resourceMap.getString("contactInfoSeparator.title")); // NOI18N

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        contactInfoArea.setColumns(22);
        contactInfoArea.setLineWrap(true);
        contactInfoArea.setRows(2);
        contactInfoArea.setWrapStyleWord(true);
        contactInfoArea.setEnabled(false);
        contactInfoArea.setName("contactInfoArea"); // NOI18N
        jScrollPane2.setViewportView(contactInfoArea);

        javax.swing.GroupLayout clientDetailsPanelLayout = new javax.swing.GroupLayout(clientDetailsPanel);
        clientDetailsPanel.setLayout(clientDetailsPanelLayout);
        clientDetailsPanelLayout.setHorizontalGroup(
            clientDetailsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(clientDetailsPanelLayout.createSequentialGroup()
                .addComponent(sendReportsLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(reportIntervalCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(clientDetailsPanelLayout.createSequentialGroup()
                .addComponent(emailLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(emailField, javax.swing.GroupLayout.DEFAULT_SIZE, 232, Short.MAX_VALUE))
            .addComponent(jScrollPane2)
            .addComponent(contactInfoSeparator, javax.swing.GroupLayout.DEFAULT_SIZE, 288, Short.MAX_VALUE)
        );
        clientDetailsPanelLayout.setVerticalGroup(
            clientDetailsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(clientDetailsPanelLayout.createSequentialGroup()
                .addComponent(contactInfoSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(clientDetailsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(emailLabel)
                    .addComponent(emailField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(clientDetailsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(sendReportsLabel)
                    .addComponent(reportIntervalCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(60, Short.MAX_VALUE))
        );

        setName("Form"); // NOI18N
        setLayout(new java.awt.BorderLayout());

        clientsSeparator.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        clientsSeparator.setTitle(resourceMap.getString("clientsSeparator.title")); // NOI18N
        clientsSeparator.setMaximumSize(new java.awt.Dimension(2147483647, 15));
        clientsSeparator.setName("clientsSeparator"); // NOI18N
        add(clientsSeparator, java.awt.BorderLayout.NORTH);

        clientsScrollPane.setName("clientsScrollPane"); // NOI18N
        clientsScrollPane.setPreferredSize(new java.awt.Dimension(300, 150));

        clientsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Client Name", "Active", "Active/Total Ads", "Client Since"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Boolean.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                true, true, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        clientsTable.setName("clientsTable"); // NOI18N
        clientsTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        clientsScrollPane.setViewportView(clientsTable);
        clientsTable.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);

        add(clientsScrollPane, java.awt.BorderLayout.CENTER);

        jPanel1.setName("jPanel1"); // NOI18N
        jPanel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance().getContext().getActionMap(OnCourseAdsClientsPanel.class, this);
        deleteClientButton.setAction(actionMap.get("deleteClient")); // NOI18N
        deleteClientButton.setName("deleteClientButton"); // NOI18N
        jPanel1.add(deleteClientButton);

        newClientButton.setAction(actionMap.get("createClient")); // NOI18N
        newClientButton.setName("newClientButton"); // NOI18N
        jPanel1.add(newClientButton);

        add(jPanel1, java.awt.BorderLayout.SOUTH);
    }// </editor-fold>//GEN-END:initComponents

    @Action
    public void createClient() {
        Clients client = new Clients("", new Date(), new Date());
        filterList.add(client);
    }

    @Action(block=Task.BlockingScope.APPLICATION, enabledProperty="clientSelected")
//    @Action(enabledProperty="clientSelected")
    public Task deleteClient() {
	int answer = JOptionPane.showConfirmDialog(this, 
		"Do you really want to delete this client definition, ads and contracts?",
		"Question", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if(answer == JOptionPane.YES_OPTION)
//	    storageTasks.deleteClient(selectedClient);
            return storageTasks.getDeleteClientTask(selectedClient);
        return null;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.jdesktop.swingx.JXPanel clientDetailsPanel;
    private javax.swing.JScrollPane clientsScrollPane;
    private org.jdesktop.swingx.JXTitledSeparator clientsSeparator;
    private javax.swing.JTable clientsTable;
    private javax.swing.JTextArea contactInfoArea;
    private org.jdesktop.swingx.JXTitledSeparator contactInfoSeparator;
    private javax.swing.JButton deleteClientButton;
    private javax.swing.JTextField emailField;
    private javax.swing.JLabel emailLabel;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JButton newClientButton;
    private javax.swing.JComboBox reportIntervalCombo;
    private javax.swing.JLabel sendReportsLabel;
    // End of variables declaration//GEN-END:variables

    private class ClientActiveEditor extends DefaultCellEditor {
	public ClientActiveEditor() {
	    super(new JCheckBox());
	    ((JCheckBox)getComponent()).setHorizontalAlignment(SwingConstants.CENTER);
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
	    JCheckBox checkBox = (JCheckBox) super.getTableCellEditorComponent(table, value, isSelected, row, column);
	    int modelIndex = clientsTable.convertRowIndexToModel(row);
	    Clients client = clientsList.get(modelIndex);
	    boolean activeAds = client.getActiveAdsCount() > 0;
	    checkBox.setSelected(client.getActive() || activeAds);
	    checkBox.setEnabled(activeAds == false);

	    return checkBox;
	}

    }

    public enum ClientFilter implements Matcher<Clients> {
	SHOW_ALL("All clients"),
	SHOW_ACTIVE("Active clients"),
	SHOW_INACTIVE("Inactive clients");

	public final String title;

	private ClientFilter(String title) {
	    this.title = title;
	}

        @Override
	public boolean matches(Clients client) {
	    return this == SHOW_ALL ||
		    (this == SHOW_ACTIVE && client.getActive()) ||
		    (this == SHOW_INACTIVE && !client.getActive());
	}

	@Override
	public String toString() {
	    return title;
	}
    }

    private static class ClientsTableFormat implements AdvancedTableFormat<Clients>, WritableTableFormat<Clients> {
        private static final ComparableComparator comparableComparator = new ComparableComparator();

        public ClientsTableFormat() {
        }

        @Override
        public Class getColumnClass(int column) {
            int i = 0;
            if(column == i++) return String.class; //cartNumber
            if(column == i++) return Boolean.class; //active
            if(column == i++) return String.class; //ads
            if(column == i++) return Date.class; //client since
            throw new IllegalArgumentException();
        }

        @Override
        public Comparator getColumnComparator(int column) {
            return comparableComparator;
        }

        @Override
        public int getColumnCount() {
            return 4;
        }

        @Override
        public String getColumnName(int column) {
            int i = 0;
            if(column == i++) return "Client";
            if(column == i++) return "Active";
            if(column == i++) return "Active/total ads";
            if(column == i++) return "Client since";
            throw new IllegalArgumentException();
        }

        @Override
        public Object getColumnValue(Clients client, int column) {
            int i = 0;
            if(column == i++) return client.getName();
            if(column == i++) return client.getActive();
            if(column == i++) return client.getActiveAdsCount() + "/" + client.getAdsCount();
            if(column == i++) return client.getCreated();
            throw new IllegalArgumentException();
        }

        @Override
        public boolean isEditable(Clients baseObject, int column) {
            return column == 0;
        }

        @Override
        public Clients setColumnValue(Clients client, Object editedValue, int column) {
            if(column == 0) client.setName((String) editedValue);
            return client;
        }
    }

    private class UpdateModelListener implements DocumentListener, ItemListener, ListSelectionListener, ListEventListener<Clients> {
        @Override
        public void insertUpdate(DocumentEvent e) {
            updateSelectedClient();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            updateSelectedClient();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            updateSelectedClient();
        }

        @Override
        public void itemStateChanged(ItemEvent e) {
            updateSelectedClient();
        }

        /** Update selectedClient when the list selection changes. */
        @Override
        public void valueChanged(ListSelectionEvent e) {
            Clients client = null;

            if(clientsTable.getSelectedRowCount() == 1) {
                int modelIndex = clientsTable.convertRowIndexToModel(clientsTable.getSelectedRow());
                if(filterList.size() > modelIndex)
                    client = filterList.get(modelIndex);
            }

            setSelectedClient(client);
        }

        /** Record changes to the model when the table is edited. */
        @Override
        public void listChanged(ListEvent<Clients> listChanges) {
            setClientInformationChanged(true);
        }
    }

}
