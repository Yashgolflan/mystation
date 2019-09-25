/*
 * 
 */
package com.stayprime.ui.editor;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.ListSelection;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.event.ListEvent;
import ca.odell.glazedlists.event.ListEventListener;
import ca.odell.glazedlists.gui.TableFormat;
import ca.odell.glazedlists.matchers.Matcher;
import ca.odell.glazedlists.swing.AdvancedTableModel;
import ca.odell.glazedlists.swing.DefaultEventSelectionModel;
import ca.odell.glazedlists.swing.GlazedListsSwing;
import com.stayprime.cartapp.menu.MenuItem;

import com.stayprime.ui.swing.TableActionAdapter;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.util.Comparator;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.JTableHeader;
import org.apache.commons.lang.StringUtils;
import org.jdesktop.swingx.JXTable;

/**
 *
 * @author benjamin
 * @param <T> Type of the item list
 */
public class ListEditor<T> extends EditorPanel {

    private Factory<T> itemFactory;

    private EventList<T> sourceList;

    private Comparator sortComparator;
    private SortedList<T> sortedList;

    private Matcher<T> filterMatcher;
    private FilterList<T> filteredList;

    private TableFormat<T> tableFormat;

    private AdvancedTableModel<T> tableModel;

    private DefaultEventSelectionModel<T> selectionModel;

    private int selectionMode = ListSelection.MULTIPLE_INTERVAL_SELECTION_DEFENSIVE;

    private boolean columnReorderingAllowed = false;

    private TableActionAdapter<T> tableActionAdapter;

    private boolean listChanged = Boolean.FALSE;

    /*
     * Initialization
     */
    public ListEditor() {
        initComponents();
        setupJXTableForGlazedLists();
        setupTableListeners();
    }

    private void setupJXTableForGlazedLists() {
        table.setFillsViewportHeight(true);
        table.setSortable(false);
        table.getTableHeader().setDefaultRenderer(new JTableHeader().getDefaultRenderer());
        table.setAutoCreateRowSorter(false);
        table.setRowSorter(null);
    }

    private void setupTableListeners() {
        tableActionAdapter = new TableActionAdapter<T>();
        table.addMouseListener(tableActionAdapter);
    }

    /**
     * Gets the empty container for NetBeans to use this form as a container
     * bean.
     *
     * @return the north container of this layout for netbeans form editor.
     */
    public JPanel getContainerDelegate() {
        return topPanel;
    }

    /*
     * Setup and dependencies
     */
    public void setTableFormat(TableFormat<T> tableFormat) {
        this.tableFormat = tableFormat;
    }

    public void setSortComparator(Comparator sortComparator) {
        this.sortComparator = sortComparator;
    }

    public void setFilterMatcher(Matcher<T> filterMatcher) {
        this.filterMatcher = filterMatcher;
        if (filteredList != null) {
            filteredList.setMatcher(filterMatcher);
        }
    }

    public void setItemFactory(Factory<T> itemFactory) {
        this.itemFactory = itemFactory;
        addItemButton.setEnabled(itemFactory != null);
    }

    public void setList(List<T> list) {
        setEventList(GlazedLists.eventList(list));
    }

    public void setEventList(EventList<T> eventList) {
        this.sourceList = eventList;
        setEditingObject(eventList);
        init();
        setModified(false);
    }

    public void init() {
        initLists();
        setTableModel();
        initTableListeners();
    }

    private void initLists() {
        if (sourceList == null) {
            sourceList = new BasicEventList<T>();
        }
        if (sortComparator != null) {
            sortedList = new SortedList<T>(sourceList, sortComparator);
            filteredList = new FilterList<T>(sortedList, filterMatcher);
        } else {
            filteredList = new FilterList<T>(sourceList, filterMatcher);
        }
        sourceList.addListEventListener(new ListChangeListener());
    }

    private void setTableModel() {
        if (tableFormat == null) {
            throw new IllegalStateException("Please initialize tableFormat first.");
        }

        //https://java.net/jira/browse/GLAZEDLISTS-563
        //TableModelEventAdapter.Factory<T> eventAdapterFactory = GlazedListsSwing.manyToOneEventAdapterFactory();
        //AdvancedTableModel<E> tableModel = GlazedListsSwing.eventTableModelWithThreadProxyList(source, tableFormat, eventAdapterFactory);
        tableModel = GlazedListsSwing.eventTableModelWithThreadProxyList(filteredList, tableFormat);
        table.setModel(tableModel);

        tableActionAdapter.setEventList(filteredList);
        selectionModel = new DefaultEventSelectionModel<T>(filteredList);
        selectionModel.setSelectionMode(selectionMode);
        table.setSelectionModel(selectionModel);

        table.getTableHeader().setReorderingAllowed(columnReorderingAllowed);
        setupTableColumns();
    }

    protected void setupTableColumns() {
    }

    private void initTableListeners() {
        scrollPane.getVerticalScrollBar().addAdjustmentListener(new AdjustMentListener());
        table.getSelectionModel().addListSelectionListener(new RowSelectionListener());
        addItemButton.setEnabled(itemFactory != null);
    }

    public boolean isButtonsPaneVisible() {
        return buttonsPanel.getParent() == this;
    }

    public void setButtonsPanelVisible(boolean visible) {
        remove(buttonsPanel);
        if (visible) {
            add(buttonsPanel, "South");
        }
    }

    public TableActionAdapter<T> getTableActionAdapter() {
        return tableActionAdapter;
    }

    public void setSelectionMode(int selectionMode) {
        this.selectionMode = selectionMode;
        if (selectionModel != null) {
            selectionModel.setSelectionMode(selectionMode);
        }
    }

    /*
     * Functional methods
     */
    public JTable getTable() {
        return table;
    }

    public EventList<T> getSourceList() {
        return sourceList;
    }

    public EventList<T> getViewList() {
        return filteredList;
    }

    public DefaultEventSelectionModel<T> getSelectionModel() {
        return selectionModel;
    }

    @Override
    public boolean applyChanges() {
        boolean changes = super.applyChanges();
        changes |= applyListChanges();
        changes |= isModified();
        return changes;
    }

    protected boolean applyListChanges() {
        return false;
    }

    protected void addItem(T item) {
        if (item == null) {
            return;
        }
        //  addItemButton.setEnabled(false);
        addItem(sourceList.size(), item);
    }

    protected void addItem(int i, T item) {
        if (item == null) {
            return;
        }
        sourceList.add(i, item);
        listChanged = Boolean.TRUE;
        setModified(true);
    }

    protected void deleteItem(int i) {
        filteredList.remove(i);
        setModified(true);
    }

    /*
     * Bound properties
     */
    public boolean isColumnReorderingAllowed() {
        return columnReorderingAllowed;
    }

    public void setColumnReorderingAllowed(boolean columnReorderingAllowed) {
        this.columnReorderingAllowed = columnReorderingAllowed;
    }

    public static final String PROP_ITEMSELECTED = "itemSelected";

    public boolean isItemSelected() {
        return selectedItem != null;
    }

    private T selectedItem;
    public static final String PROP_SELECTEDITEM = "selectedItem";

    public T getSelectedItem() {
        return selectedItem;
    }

    protected void setSelectedItems(EventList<T> selected) {
    }

    protected void setSelectedItem(T selectedItem) {
        T oldSelectedItem = this.selectedItem;
        boolean oldItemSelected = isItemSelected();

        this.selectedItem = selectedItem;
        boolean itemSelected = isItemSelected();

        firePropertyChange(PROP_SELECTEDITEM, oldSelectedItem, selectedItem);
        firePropertyChange(PROP_ITEMSELECTED, oldItemSelected, itemSelected);
    }

    public void updateSelectedItem() {
        EventList<T> selected = getSelectionModel().getSelected();
        if (selected.size() == 1) {
            selected.set(0, selected.get(0));
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
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        topPanel = new javax.swing.JPanel();
        scrollPane = new javax.swing.JScrollPane();
        table = new FillsViewportJXTable();
        buttonsPanel = new org.jdesktop.swingx.JXPanel();
        addItemButton = new javax.swing.JButton();
        deleteItemButton = new javax.swing.JButton();

        setLayout(new java.awt.BorderLayout());

        topPanel.setName("topPanel"); // NOI18N
        topPanel.setLayout(new java.awt.BorderLayout());
        add(topPanel, java.awt.BorderLayout.NORTH);

        scrollPane.setName("scrollPane"); // NOI18N

        table.setName("table"); // NOI18N
        table.setPreferredScrollableViewportSize(new java.awt.Dimension(0, 100));
        scrollPane.setViewportView(table);

        add(scrollPane, java.awt.BorderLayout.CENTER);

        buttonsPanel.setName("buttonsPanel"); // NOI18N
        org.jdesktop.swingx.HorizontalLayout horizontalLayout1 = new org.jdesktop.swingx.HorizontalLayout();
        horizontalLayout1.setGap(5);
        buttonsPanel.setLayout(horizontalLayout1);

        addItemButton.setText("Add");
        addItemButton.setEnabled(false);
        addItemButton.setName("addItemButton"); // NOI18N
        addItemButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addItemButtonActionPerformed(evt);
            }
        });
        buttonsPanel.add(addItemButton);

        deleteItemButton.setText("Delete");
        deleteItemButton.setName("deleteItemButton"); // NOI18N

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${itemSelected}"), deleteItemButton, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        deleteItemButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteItemButtonActionPerformed(evt);
            }
        });
        buttonsPanel.add(deleteItemButton);

        add(buttonsPanel, java.awt.BorderLayout.SOUTH);

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

    private void addItemButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addItemButtonActionPerformed

        addItem(itemFactory.create());

    }//GEN-LAST:event_addItemButtonActionPerformed

    private void deleteItemButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteItemButtonActionPerformed
       int result = JOptionPane.showConfirmDialog(null,
                   "Do you want to delete the Item",
                    "Delete Confirmation",
                    JOptionPane.YES_NO_OPTION);
       if(result==JOptionPane.YES_OPTION){
        deleteItem(table.getSelectedRow());
       }
    }//GEN-LAST:event_deleteItemButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addItemButton;
    private org.jdesktop.swingx.JXPanel buttonsPanel;
    private javax.swing.JButton deleteItemButton;
    protected javax.swing.JScrollPane scrollPane;
    protected org.jdesktop.swingx.JXTable table;
    private javax.swing.JPanel topPanel;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    private class RowSelectionListener implements ListSelectionListener {

        @Override
        public void valueChanged(ListSelectionEvent e) {
            EventList<T> selected = getSelectionModel().getSelected();
            setSelectedItems(selected);
            setSelectedItem(selected.size() == 1 ? selected.get(0) : null);
        }
    }

    private class ListChangeListener implements ListEventListener {

        @Override
        public void listChanged(ListEvent listChanges) {
            setModified(true);
//            JScrollBar vertical = scrollPane.getVerticalScrollBar();
//            vertical.setValue( vertical.getMaximum() );
            // scrollPane.getVerticalScrollBar().setValue(sourceList.size()-1);
            System.out.println("---------------------28056--------------" + listChanges);

//            scrollPane.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
//                @Override
//                public void adjustmentValueChanged(AdjustmentEvent ae) {
//                    System.out.println(test+"--28056--");
//                    ae.getAdjustable().setValue(ae.getAdjustable().getMaximum());
//                   
//                    //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//                }
//            });
        }
    }

    private class AdjustMentListener implements AdjustmentListener {

        @Override
        public void adjustmentValueChanged(AdjustmentEvent ae) {
            if (listChanged) {
                ae.getAdjustable().setValue(ae.getAdjustable().getMaximum());
            }

            listChanged = Boolean.FALSE;

            //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

    }

    //Bug Fix: WebLaf set JTable.fillsViewportHeight to false on show
    private static class FillsViewportJXTable extends JXTable {

        @Override
        public void setFillsViewportHeight(boolean fillsViewportHeight) {
            super.setFillsViewportHeight(true);
        }
    }

}
