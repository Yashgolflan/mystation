/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * CartPacePanel.java
 *
 * Created on 11/04/2011, 06:29:46 PM
 */
package com.stayprime.basestation2.ui.mainview;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.gui.TableFormat;
import ca.odell.glazedlists.swing.DefaultEventSelectionModel;
import ca.odell.glazedlists.swing.DefaultEventTableModel;
import com.aeben.golfclub.GolfClub;
import com.stayprime.basestation2.BaseStation2App;
import com.stayprime.basestation2.CourseSettingsManager;
import com.aeben.golfclub.view.Dashboard;
import com.stayprime.basestation2.services.CartService;
import com.stayprime.basestation2.ui.custom.JTableMenuListener;
import com.stayprime.basestation2.services.SendMessageControl;
import com.stayprime.basestation2.ui.dialog.SendMessageDialog;
import com.stayprime.basestation2.ui.modules.CartInfoComparator;
import com.stayprime.comm.gprs.request.Request;
import com.stayprime.comm.gprs.request.RequestObserver;
import com.stayprime.golf.message.Message;
import com.stayprime.hibernate.entities.CartInfo;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import javax.swing.JFrame;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.jdesktop.application.Action;
import org.jdesktop.application.Application;

/**
 *
 * @author benjamin
 */
public class CartPacePanel extends javax.swing.JPanel {

    private EventList<CartInfo> cartsList;
    private Dashboard dashboard;
    protected boolean cartSelected = false;
    public static final String PROP_CARTSELECTED = "cartSelected";

    private final CartComparator cartComparator;
    private final SortedList<CartInfo> sortedCarts;
    private final FilterList<CartInfo> filteredCarts;

    private SendMessageControl messageControl;
    private SendMessageDialog messageDialog;

    private CartStatusCellRenderer cellRenderer;

    /**
     * Creates new form CartPacePanel
     */
    public CartPacePanel() {
        initComponents();

        cartsList = new BasicEventList<CartInfo>();
        cartComparator = new CartComparator();
        sortedCarts = new SortedList<CartInfo>(cartsList, new CartInfoComparator());
        sortedCarts.setComparator(cartComparator);
        filteredCarts = new FilterList<CartInfo>(sortedCarts);
    }

    public void init() {
        DefaultEventTableModel tableModel = new DefaultEventTableModel(filteredCarts, new CartPaceTableFormat());
        cartPaceTable.setModel(tableModel);

        DefaultEventSelectionModel selectionModel = new DefaultEventSelectionModel(filteredCarts);
        RowSelectionListener rowSelectionListener = new RowSelectionListener(selectionModel);
        selectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        selectionModel.addListSelectionListener(rowSelectionListener);
        cartPaceTable.setSelectionModel(selectionModel);

        cellRenderer = new CartStatusCellRenderer();
        cartPaceTable.getColumnModel().getColumn(0).setCellRenderer(cellRenderer);

        JTableMenuListener tableMenuListener = new JTableMenuListener(cartPopupMenu);
        ActionListener centerCart = Application.getInstance().getContext().getActionMap(this).get("centerCart");
        tableMenuListener.setDoubleClickAction(centerCart);
        cartPaceTable.addMouseListener(tableMenuListener);
    }

    public void setCourseSettingsManager(CourseSettingsManager courseSettingsManager) {
        courseSettingsManager.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(CourseSettingsManager.PROP_PACEOFPLAYTHRESHOLD)) {
                    setCartFilter(cartComparator.getPaceFilter());
                }
            }
        });
        setCartFilter(PaceFilter.Active);
    }

    public void setSendMessageControl(SendMessageControl messageControl) {
        this.messageControl = messageControl;
    }

    public void setDashboard(Dashboard dashboard) {
        this.dashboard = dashboard;
    }

    public void setCartInfoFilter(CartInfoFilter cartInfoFilter) {
        filteredCarts.setMatcher(cartInfoFilter);
    }

    private void setCartFilter(PaceFilter paceFilter) {
        CartInfoFilter filter = BaseStation2App.getApplication().getMainView().getCartInfoFilter();
        cartComparator.setPaceFilter(paceFilter);
        sortedCarts.setComparator(cartComparator);
        filter.setFilter(paceFilter);
        filteredCarts.setMatcher(filter);

        paceFilterButton.setText(paceFilter.name());

        if (dashboard != null) {
            dashboard.repaint();
        }
    }

    private CartInfo getCartInfo(int cartNumber) {
        for (CartInfo cart : cartsList) {
            if (cart.getCartNumber() == cartNumber) {
                return cart;
            }
        }
        return null;
    }

    /*
     * Bound properties
     */

    public boolean isCartSelected() {
        return cartSelected;
    }

    public void setCartSelected(boolean cartSelected) {
        boolean oldCartSelected = this.cartSelected;
        this.cartSelected = cartSelected;
        firePropertyChange(PROP_CARTSELECTED, oldCartSelected, cartSelected);
    }

    /*
     * Action methods
     */

    @Action(enabledProperty = PROP_CARTSELECTED)
    public void centerCart() {
        if (cartPaceTable.getSelectedRowCount() == 1) {
            Object o = cartPaceTable.getValueAt(cartPaceTable.getSelectedRow(), 0);
            if (dashboard != null && o instanceof CartInfo) {
                CartInfo cart = (CartInfo) o;
                if (cart.getPosition() != null) {
                    dashboard.zoomInAndCenter(cart.getPosition(), dashboard.getMaxScale() / dashboard.getScale());
                }
            }
        }
    }

    @Action
    public void sendMessageToCart() {
        int row = cartPaceTable.getSelectedRow();
        Object cell = cartPaceTable.getValueAt(row, 0);

        if (cell instanceof CartInfo) {
            CartInfo cartStatus = (CartInfo) cell;
            final int cartNumber = cartStatus.getCartNumber();
            PropertiesConfiguration config = BaseStation2App.getApplication().getConfig();

            //TODO: Change config name and implement:
            //boolean showCommands = config.getBoolean(Constant.PROP_SHOWPRESYSTEMMESSAGES, false);

            createMessageDialog();
            cartStatus.getProperties().put("pendingMessage", "t");
            cartStatus.getProperties().put("failedMessage", "");
            messageControl.sendMessageToCart(cartNumber, new CartPacePanelRequestObserver(cartNumber));
        }
    }

    private void createMessageDialog() {
        if (messageDialog == null) {
            JFrame frame = BaseStation2App.getApplication().getMainFrame();
            messageDialog = new SendMessageDialog(frame);
            messageDialog.setActions(messageControl);
            messageControl.setView(messageDialog);
        }
    }

    /*
     * Implement CartInfoListener
     */

    public void setGolfClub(GolfClub golfClub) {
        cellRenderer.setGolfClub(golfClub);
    }

    public void cartInfoUpdated(final Collection<CartInfo> cartsStatus) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                ArrayList<CartInfo> oldCarts = new ArrayList<CartInfo>(cartsList);
                for (CartInfo cart : cartsStatus) {
                    int index = cartsList.indexOf(cart);
                    if (index < 0) {
                        cartsList.add(cart);
                    }
                    else {
                        CartInfo oldCart = cartsList.get(index);
                        String p = oldCart.getProperties().getProperties();
                        cart.getProperties().setProperties(p);
                        cartsList.set(index, cart);
                    }
                    oldCarts.remove(cart);
                }
                cartsList.removeAll(oldCarts);
            }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        cartPopupMenu = new javax.swing.JPopupMenu();
        sendMessageOption = new javax.swing.JMenuItem();
        northPanel = new javax.swing.JPanel();
        cartPaceStatusLabel = new javax.swing.JLabel();
        paceFilterButton = new javax.swing.JButton();
        cartPaceScrollPane = new javax.swing.JScrollPane();
        cartPaceTable = new javax.swing.JTable();

        cartPopupMenu.setName("cartPopupMenu"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance().getContext().getActionMap(CartPacePanel.class, this);
        sendMessageOption.setAction(actionMap.get("sendMessageToCart")); // NOI18N
        sendMessageOption.setName("sendMessageOption"); // NOI18N
        cartPopupMenu.add(sendMessageOption);

        setName("Form"); // NOI18N
        setLayout(new java.awt.BorderLayout(5, 5));

        northPanel.setName("northPanel"); // NOI18N
        northPanel.setLayout(new java.awt.BorderLayout(5, 5));

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance().getContext().getResourceMap(CartPacePanel.class);
        cartPaceStatusLabel.setText(resourceMap.getString("cartPaceStatusLabel.text")); // NOI18N
        cartPaceStatusLabel.setName("cartPaceStatusLabel"); // NOI18N
        northPanel.add(cartPaceStatusLabel, java.awt.BorderLayout.WEST);

        paceFilterButton.setText(resourceMap.getString("paceFilterButton.text")); // NOI18N
        paceFilterButton.setName("paceFilterButton"); // NOI18N
        paceFilterButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                paceFilterButtonActionPerformed(evt);
            }
        });
        northPanel.add(paceFilterButton, java.awt.BorderLayout.CENTER);

        add(northPanel, java.awt.BorderLayout.NORTH);

        cartPaceScrollPane.setFocusable(false);
        cartPaceScrollPane.setHorizontalScrollBar(null);
        cartPaceScrollPane.setName("cartPaceScrollPane"); // NOI18N
        cartPaceScrollPane.setPreferredSize(new java.awt.Dimension(150, 150));

        cartPaceTable.setName("cartPaceTable"); // NOI18N
        cartPaceScrollPane.setViewportView(cartPaceTable);

        add(cartPaceScrollPane, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void paceFilterButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_paceFilterButtonActionPerformed
        setCartFilter(cartComparator.getPaceFilter().next());
    }//GEN-LAST:event_paceFilterButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane cartPaceScrollPane;
    private javax.swing.JLabel cartPaceStatusLabel;
    private javax.swing.JTable cartPaceTable;
    private javax.swing.JPopupMenu cartPopupMenu;
    private javax.swing.JPanel northPanel;
    private javax.swing.JButton paceFilterButton;
    private javax.swing.JMenuItem sendMessageOption;
    // End of variables declaration//GEN-END:variables

    private class CartPacePanelRequestObserver implements RequestObserver {
        private final int cartNumber;

        public CartPacePanelRequestObserver(int cartNumber) {
            this.cartNumber = cartNumber;
        }

        @Override
        public void requestSent(Request request) {
            CartInfo cartInfo = getCartInfo(cartNumber);
            if (cartInfo != null) {
                cartInfo.getProperties().put("pendingMessage", "t");
                cartInfo.getProperties().put("failedMessage", "");
            }
        }

        @Override
        public void requestComplete(Request request, Message ack) {
            CartInfo cartInfo = getCartInfo(cartNumber);
            if (cartInfo != null) {
                cartInfo.getProperties().put("pendingMessage", "");
                cartInfo.getProperties().put("failedMessage", "");
            }
        }

        @Override
        public void requestCanceled(Request request) {
            CartInfo cartInfo = getCartInfo(cartNumber);
            if (cartInfo != null) {
                cartInfo.getProperties().put("pendingMessage", "");
                cartInfo.getProperties().put("failedMessage", "");
            }
        }

        @Override
        public void requestFailed(Request request) {
            CartInfo cartInfo = getCartInfo(cartNumber);
            if (cartInfo != null) {
                cartInfo.getProperties().put("pendingMessage", "");
                cartInfo.getProperties().put("failedMessage", "t");
            }
        }
    }

    private static class CartComparator implements Comparator {
        private PaceFilter paceFilter = PaceFilter.Active;

        public PaceFilter getPaceFilter() {
            return paceFilter;
        }

        public void setPaceFilter(PaceFilter paceFilter) {
            this.paceFilter = paceFilter;
        }

        @Override
        public int compare(Object o1, Object o2) {
            if (o1 instanceof CartInfo && o2 instanceof CartInfo) {
                CartInfo c1 = (CartInfo) o1;
                CartInfo c2 = (CartInfo) o2;

                if (paceFilter == PaceFilter.All)
                    return c1.getCartNumber() - c2.getCartNumber();
                else {
                    if (c1.getPaceOfPlay() != null && c2.getPaceOfPlay() != null)
                        return c1.getPaceOfPlay() - c2.getPaceOfPlay();
                    else if (c1.getPaceOfPlay() == null && c2.getPaceOfPlay() != null)
                        return Integer.MAX_VALUE - c1.getCartNumber();
                    else if (c1.getPaceOfPlay() != null && c2.getPaceOfPlay() == null)
                        return Integer.MIN_VALUE + c2.getCartNumber();
                }
            }

            return 0;
        }
    }

    private class RowSelectionListener implements ListSelectionListener {
        private final DefaultEventSelectionModel selectionModel;

        private RowSelectionListener(DefaultEventSelectionModel selectionModel) {
            this.selectionModel = selectionModel;
        }

        @Override
        public void valueChanged(ListSelectionEvent e) {
            setCartSelected(selectionModel.getSelected().size() == 1);
//            EventList<CartInfo> selected = selectionModel.getSelected();
//            if (selected.size() == 1) {
//                CartInfo cart = selected.get(0);
//                setSelectedCartDefinition(cart);
//            }
//            else {
//                setSelectedCartDefinition(null);
//            }
        }
    }

    public static class CartPaceTableFormat implements TableFormat<CartInfo> {
        @Override
        public int getColumnCount() {
            return 1;
        }

        @Override
        public String getColumnName(int column) {
            return "Carts";
        }

        @Override
        public Object getColumnValue(CartInfo cartInfo, int column) {
            return cartInfo;
        }
    }

}
