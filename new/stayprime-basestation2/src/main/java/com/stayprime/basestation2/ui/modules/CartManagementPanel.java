/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * CartManagementPanel.java
 *
 * Created on 13/03/2011, 12:42:18 AM
 */
package com.stayprime.basestation2.ui.modules;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.swing.AdvancedTableModel;
import ca.odell.glazedlists.swing.DefaultEventSelectionModel;
import ca.odell.glazedlists.swing.DefaultEventTableModel;
import ca.odell.glazedlists.swing.TableComparatorChooser;
import com.stayprime.basestation2.BaseStation2App;
import com.stayprime.basestation2.services.Services;
import com.stayprime.basestation2.ui.util.ApplicationUtil;
import com.stayprime.basestation2.ui.util.TableUtil;
import com.stayprime.hibernate.entities.CartInfo;
import com.stayprime.legacy.screen.Screen;
import com.stayprime.legacy.screen.ScreenParent;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.List;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.Timer;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.jdesktop.application.Action;
import org.jdesktop.application.Task;

/**
 *
 * @author benjamin
 */
public class CartManagementPanel extends javax.swing.JPanel implements Screen {

    private BasicEventList<CartInfo> carts;
    private SortedList<CartInfo> sortedCarts;
    private AdvancedTableModel tableModel;
    private CartTableFormat cartTableFormat;
    private DefaultEventSelectionModel selectionModel;

    private Timer autoRefreshTimer;

    private ScreenParent screenParent;

    private CartInfo selectedCart;

    private boolean cartDefinitionSelected = false;
    public static final String PROP_CARTDEFINITIONSELECTED = "cartDefinitionSelected";

    private CartListMenu cartMenu;

    private CartManagementPanelTasks tasks;

    /*
     * Constructor and initialization
     */

    public void init() {
        tasks = new CartManagementPanelTasks(this);
        initComponents();
        initCartsTable();
        initCartDetailPanel();
        initAutoRefreshTimer();

        cartUpdatePanel.loadConfig();
        cartUpdatePanel.setCartsEventList(carts);
        cartUpdatePanel.setCartTableFormat(cartTableFormat);
    }

    public void setServices(Services services) {
        cartUpdatePanel.setServices(services);
        tasks.setCartService(services == null ? null : services.getCartService());
    }

    
    private void initCartsTable() {
        carts = new BasicEventList<CartInfo>();
        sortedCarts = new SortedList<CartInfo>(carts, new CartInfoComparator());

        cartTableFormat = new CartTableFormat();
        tableModel = new DefaultEventTableModel(sortedCarts, cartTableFormat);
        cartsTable.setModel(tableModel);

        selectionModel = new DefaultEventSelectionModel(sortedCarts);
        cartsTable.setSelectionModel(selectionModel);

        TableComparatorChooser.install(cartsTable, sortedCarts, TableComparatorChooser.SINGLE_COLUMN);
        setupCartsTableSelectionListener();
        setupCartsTableDoubleClickAction();
        setupCartsTableMenu();

        TableUtil.installColumnChooserMenu(cartsTable);

        cartsTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                BaseStation2App.getApplication().showStatusMessage(cartsTable.getSelectedRowCount() + " selected.");
            }
        });
    }

    private void setupCartsTableSelectionListener() {
        selectionModel.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent lse) {
                EventList<CartInfo> selected = selectionModel.getSelected();
                if (selected.size() == 1) {
                    CartInfo cart = selected.get(0);
                    setSelectedCartDefinition(cart);
                }
                else {
                    setSelectedCartDefinition(null);
                }
            }

        });
    }

    private void setupCartsTableDoubleClickAction() {
        cartsTable.getActionMap().put("editCart", ApplicationUtil.getAction(this, "editCart"));

        int focusedState = JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT;
        KeyStroke enterKey = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
        cartsTable.getInputMap(focusedState).put(enterKey, "editCart");
    }

    private void setupCartsTableMenu() {
        cartMenu = new CartListMenu();
        cartsTable.addMouseListener(cartMenu);
    }

    private void initCartDetailPanel() {
        cartDetailPanel.setCartDetailCallback(new DoneCallback() {
            @Override
            public void done() {
                showDetailPanel(false);
            }
        });
    }

    private void initAutoRefreshTimer() {
        autoRefreshTimer = new Timer(5000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                reloadCartList();
            }
        });
    }

    /*
     * Internal util methods
     */

    private void reloadCartList() {
        ActionEvent event = new ActionEvent(CartManagementPanel.this, ActionEvent.ACTION_PERFORMED, "readCartsStatus");
        ApplicationUtil.getAction(this, "readCartsStatus").actionPerformed(event);
    }

    private void setSelectedCartDefinition(CartInfo cart) {
        this.selectedCart = cart;
        setCartDefinitionSelected(cart != null);
    }

    private void showDetailPanel(boolean showDetailPanel) {
        String card = showDetailPanel? cartDetailPanel.getName() : cartListPanel.getName();
        ((CardLayout) getLayout()).show(this, card);
    }

    private void exitThisScreen() {
        if (screenParent != null) {
            screenParent.exitScreen(this);
        }
    }

    /*
     * Properties
     */

    public void setCartsList(List<CartInfo> carts) {
        this.carts.retainAll(carts);
        for (CartInfo cart : carts) {
            int index = this.carts.indexOf(cart);
            if (index >= 0) {
                this.carts.set(index, cart);
            }
            else {
                this.carts.add(cart);
            }
        }
    }

    public boolean isCartDefinitionSelected() {
        return cartDefinitionSelected;
    }

    public void setCartDefinitionSelected(boolean cartDefinitionSelected) {
        boolean oldCartDefinitionSelected = this.cartDefinitionSelected;
        this.cartDefinitionSelected = cartDefinitionSelected;
        firePropertyChange(PROP_CARTDEFINITIONSELECTED, oldCartDefinitionSelected, cartDefinitionSelected);
    }

    /*
     * Implement Screen interface
     */

    @Override
    public void enterScreen(ScreenParent screenParent) {
        this.screenParent = screenParent;
        showDetailPanel(false);
        reloadCartList();
        if (autoRefreshCheckbox.isSelected() && autoRefreshTimer.isRunning() == false) {
            autoRefreshTimer.start();
        }
    }

    @Override
    public boolean exitScreen() {
        if (cartDetailPanel.exitScreen()) {
            showDetailPanel(false);
            return true;
        }
        else {
            return false;
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

        clubhouseCheckbox = new javax.swing.JCheckBox();
        cartListScrollPane = new javax.swing.JScrollPane();
        cartListPanel = new org.jdesktop.swingx.JXPanel();
        titleLabel = new javax.swing.JLabel();
        titleSeparator = new javax.swing.JSeparator();
        mainListPanel = new org.jdesktop.swingx.JXPanel();
        cartsScrollPane = new javax.swing.JScrollPane();
        cartsTable = new javax.swing.JTable();
        refreshButton = new javax.swing.JButton();
        autoRefreshCheckbox = new javax.swing.JCheckBox();
        deleteCartButton = new javax.swing.JButton();
        editCartButton = new javax.swing.JButton();
        doneButton = new javax.swing.JButton();
        updatePanel = new org.jdesktop.swingx.JXTaskPane();
        cartUpdatePanel = new com.stayprime.basestation2.ui.modules.CartUpdatePanel();
        cartDetailScrollPane = new javax.swing.JScrollPane();
        cartDetailPanel = new com.stayprime.basestation2.ui.modules.CartDetailPanel();

        clubhouseCheckbox.setSelected(true);
        clubhouseCheckbox.setName("clubhouseCheckbox"); // NOI18N

        setLayout(new java.awt.CardLayout());

        cartListScrollPane.setName("cartListScrollPane"); // NOI18N

        cartListPanel.setName("cartListPanel"); // NOI18N
        cartListPanel.setPreferredSize(new java.awt.Dimension(200, 275));

        titleLabel.setFont(titleLabel.getFont().deriveFont(titleLabel.getFont().getStyle() | java.awt.Font.BOLD, titleLabel.getFont().getSize()+5));
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance().getContext().getResourceMap(CartManagementPanel.class);
        titleLabel.setText(resourceMap.getString("titleLabel.text")); // NOI18N
        titleLabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(12, 12, 6, 12));
        titleLabel.setName("titleLabel"); // NOI18N

        titleSeparator.setName("titleSeparator"); // NOI18N

        mainListPanel.setName("mainListPanel"); // NOI18N

        cartsScrollPane.setName("cartsScrollPane"); // NOI18N
        cartsScrollPane.setPreferredSize(new java.awt.Dimension(300, 200));

        cartsTable.setFillsViewportHeight(true);
        cartsTable.setName("cartsTable"); // NOI18N
        cartsScrollPane.setViewportView(cartsTable);

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance().getContext().getActionMap(CartManagementPanel.class, this);
        refreshButton.setAction(actionMap.get("readCartsStatus")); // NOI18N
        refreshButton.setName("refreshButton"); // NOI18N

        autoRefreshCheckbox.setSelected(true);
        autoRefreshCheckbox.setText(resourceMap.getString("autoRefreshCheckbox.text")); // NOI18N
        autoRefreshCheckbox.setName("autoRefreshCheckbox"); // NOI18N
        autoRefreshCheckbox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                autoRefreshCheckboxItemStateChanged(evt);
            }
        });

        deleteCartButton.setAction(actionMap.get("deleteCart")); // NOI18N
        deleteCartButton.setName("deleteCartButton"); // NOI18N

        editCartButton.setAction(actionMap.get("editCart")); // NOI18N
        editCartButton.setEnabled(false);
        editCartButton.setName("editCartButton"); // NOI18N

        javax.swing.GroupLayout mainListPanelLayout = new javax.swing.GroupLayout(mainListPanel);
        mainListPanel.setLayout(mainListPanelLayout);
        mainListPanelLayout.setHorizontalGroup(
            mainListPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(cartsScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 692, Short.MAX_VALUE)
            .addGroup(mainListPanelLayout.createSequentialGroup()
                .addComponent(refreshButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(autoRefreshCheckbox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(deleteCartButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(editCartButton))
        );
        mainListPanelLayout.setVerticalGroup(
            mainListPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainListPanelLayout.createSequentialGroup()
                .addGroup(mainListPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(editCartButton)
                    .addComponent(deleteCartButton)
                    .addComponent(refreshButton)
                    .addComponent(autoRefreshCheckbox))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cartsScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 88, Short.MAX_VALUE))
        );

        doneButton.setAction(actionMap.get("doneEditing")); // NOI18N
        doneButton.setName("doneButton"); // NOI18N

        updatePanel.setTitle(resourceMap.getString("updatePanel.title")); // NOI18N
        updatePanel.setName("updatePanel"); // NOI18N
        updatePanel.getContentPane().setLayout(new org.jdesktop.swingx.VerticalLayout());

        cartUpdatePanel.setName("cartUpdatePanel"); // NOI18N
        updatePanel.getContentPane().add(cartUpdatePanel);

        javax.swing.GroupLayout cartListPanelLayout = new javax.swing.GroupLayout(cartListPanel);
        cartListPanel.setLayout(cartListPanelLayout);
        cartListPanelLayout.setHorizontalGroup(
            cartListPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(titleLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(titleSeparator)
            .addGroup(cartListPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(cartListPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(mainListPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(updatePanel, javax.swing.GroupLayout.DEFAULT_SIZE, 692, Short.MAX_VALUE)
                    .addGroup(cartListPanelLayout.createSequentialGroup()
                        .addComponent(doneButton)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        cartListPanelLayout.setVerticalGroup(
            cartListPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(cartListPanelLayout.createSequentialGroup()
                .addComponent(titleLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(titleSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(mainListPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(updatePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(doneButton)
                .addContainerGap())
        );

        cartListScrollPane.setViewportView(cartListPanel);

        add(cartListScrollPane, "cartListPanel");

        cartDetailScrollPane.setName("cartDetailScrollPane"); // NOI18N

        cartDetailPanel.setName("cartDetailPanel"); // NOI18N
        cartDetailScrollPane.setViewportView(cartDetailPanel);

        add(cartDetailScrollPane, "cartDetailPanel");
    }// </editor-fold>//GEN-END:initComponents

    private void autoRefreshCheckboxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_autoRefreshCheckboxItemStateChanged
        if (autoRefreshCheckbox.isSelected()) {
            autoRefreshTimer.start();
        }
        else {
            autoRefreshTimer.stop();
        }
    }//GEN-LAST:event_autoRefreshCheckboxItemStateChanged

    @Action
    public void doneEditing() {
        exitThisScreen();
    }

    @Action(enabledProperty = "cartDefinitionSelected")
    public Task deleteCart() {
        String title = "Confirmation";
        Object messages[] = {
            "Do you want to delete this cart?",
            new JCheckBox("Also delete associated device")
        };

        int answer = JOptionPane.showConfirmDialog(this, messages, title,
                JOptionPane.YES_NO_OPTION);

        if (answer == JOptionPane.YES_OPTION) {
            JCheckBox checkBox = (JCheckBox) messages[1];
            return tasks.getDeleteCartTask(selectedCart, checkBox.isSelected());
        }
        else {
            return null;
        }
    }

    @Action(enabledProperty = "cartDefinitionSelected")
    public void editCart() {
        if (isCartDefinitionSelected()) {
            cartDetailPanel.setCartStatus(selectedCart);
            showDetailPanel(true);
        }
    }

    @Action
    public Task readCartsStatus(ActionEvent event) {
        return tasks.getLoadTask();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox autoRefreshCheckbox;
    private com.stayprime.basestation2.ui.modules.CartDetailPanel cartDetailPanel;
    private javax.swing.JScrollPane cartDetailScrollPane;
    private org.jdesktop.swingx.JXPanel cartListPanel;
    private javax.swing.JScrollPane cartListScrollPane;
    private com.stayprime.basestation2.ui.modules.CartUpdatePanel cartUpdatePanel;
    private javax.swing.JScrollPane cartsScrollPane;
    private javax.swing.JTable cartsTable;
    private javax.swing.JCheckBox clubhouseCheckbox;
    private javax.swing.JButton deleteCartButton;
    private javax.swing.JButton doneButton;
    private javax.swing.JButton editCartButton;
    private org.jdesktop.swingx.JXPanel mainListPanel;
    private javax.swing.JButton refreshButton;
    private javax.swing.JLabel titleLabel;
    private javax.swing.JSeparator titleSeparator;
    private org.jdesktop.swingx.JXTaskPane updatePanel;
    // End of variables declaration//GEN-END:variables

}
