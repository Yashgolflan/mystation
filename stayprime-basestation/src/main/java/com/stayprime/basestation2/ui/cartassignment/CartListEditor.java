/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stayprime.basestation2.ui.cartassignment;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.swing.DefaultEventSelectionModel;
import com.stayprime.hibernate.entities.CartAssignment;
import com.stayprime.hibernate.entities.CartInfo;
import com.stayprime.ui.editor.ListEditor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.swing.JDialog;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumnModel;

/**
 *
 * @author benjamin
 */
public class CartListEditor extends ListEditor<CartAssignment> {
    private String allCarts = "All";
    private boolean settingSelection = false;

    public CartListEditor() {
        initComponents();
        setTableFormat(new CartListTableFormat());
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    @Override
    protected void setupTableColumns() {
        int i = 0;
        TableColumnModel columnModel = table.getColumnModel();
        columnModel.getColumn(i++).setPreferredWidth(10);
        columnModel.getColumn(i++).setPreferredWidth(40);
        columnModel.getColumn(i++).setPreferredWidth(40);

        ListSelectionModel colSelModel = columnModel.getSelectionModel();
        ListSelectionModel selModel = getSelectionModel();
        ListSelectionListener l = new CartSelectionListener(colSelModel, selModel);

        colSelModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        columnModel.getSelectionModel().addListSelectionListener(l);
        getSelectionModel().addListSelectionListener(l);
    }

    public void setCartCount(int count) {
        BasicEventList<CartAssignment> carts = new BasicEventList<CartAssignment>(count);
        for (int i = 0; i < count; i++) {
            carts.add(new CartAssignment(i + 1));
        }
        setEventList(carts);
    }

    public void setCarts(Collection<CartInfo> carts) {
        BasicEventList<CartAssignment> list = new BasicEventList<CartAssignment>(carts.size());
        for (CartInfo c : carts) {
            list.add(new CartAssignment(c.getCartNumber()));
        }
        setEventList(list);
    }

    public void setCartAssignments(Collection<CartAssignment> assignments) {
        EventList<CartAssignment> carts = getSourceList();
        for (CartAssignment c : assignments) {
            int i = CartAssignment.indexOf(carts, c.getCartNumber());
            if (i >= 0) {
                carts.set(i, c);
            }
        }
    }

    public List<CartAssignment> getCartAssignments() {
        List<CartAssignment> list = new ArrayList<>();
        for (CartAssignment c : getSourceList()) {
            if (c.getPlayerCount() > 0) {
                list.add(c);
            }
        }
        return list;
    }

    public void clearCartPlayers() {
        EventList<CartAssignment> oldCarts = getSourceList();
        EventList<CartAssignment> carts = new BasicEventList<CartAssignment>(oldCarts.size());
        for (int i = 0; i < oldCarts.size(); i++) {
            CartAssignment cart = oldCarts.get(i);
            carts.add(i, new CartAssignment(cart.getCartNumber()));
        }
        setEventList(carts);
    }

    public boolean addCartPlayer(int cart, String p) {
        for (CartAssignment ca : getSourceList()) {
            if (ca.getCartNumber() == cart) {
                ca.addPlayer(p);
                return true;
            }
        }
        return false;
    }

    public void setSelectedForScoring(boolean forScoring) {
        EventList<CartAssignment> selected = getSelectionModel().getSelected();
        for (int i = 0; i < selected.size(); i++) {
            CartAssignment ca = selected.get(i);
            ca.clearPlayers();
            selected.set(i, ca);
        }
    }

    private void selectAll(boolean select) {
        selectCarts(true, !select, 0, 0);
    }

    private void selectCarts(boolean all, boolean clear, int min, int max) {
        List<CartAssignment> list = getViewList();
        int i0 = -1, i1 = -1;

        if (all == false && clear == false) {
            for (int i = 0; i < list.size(); i++) {
                CartAssignment a = list.get(i);
                int cart = a.getCartNumber();

                if (all || (cart >= min && cart <= max)) {
                    if (i0 == -1) {
                        i0 = i1 = i;
                    }
                    else {
                        i1 = i;
                    }
                }
            }
        }

        settingSelection = true;
        table.getColumnModel().getSelectionModel().setSelectionInterval(1, 1);
        DefaultEventSelectionModel model = getSelectionModel();
        model.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        if (clear) {
            model.clearSelection();
        }
        else if (all) {
            model.setSelectionInterval(0, getViewList().size() - 1);
        }
        else {
            model.setSelectionInterval(i0, i1);
        }
        settingSelection = false;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        selectAllButton = new javax.swing.JButton();
        clearButton = new javax.swing.JButton();

        getContainerDelegate().setLayout(new javax.swing.BoxLayout(getContainerDelegate(), javax.swing.BoxLayout.LINE_AXIS));

        filler1.setName("filler1"); // NOI18N
        getContainerDelegate().add(filler1);

        selectAllButton.setText("Select all");
        selectAllButton.setName("selectAllButton"); // NOI18N
        selectAllButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectAllButtonActionPerformed(evt);
            }
        });
        getContainerDelegate().add(selectAllButton);

        clearButton.setText("Clear");
        clearButton.setName("clearButton"); // NOI18N
        clearButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearButtonActionPerformed(evt);
            }
        });
        getContainerDelegate().add(clearButton);
    }// </editor-fold>//GEN-END:initComponents

    private void selectAllButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectAllButtonActionPerformed
        selectAll(true);
    }//GEN-LAST:event_selectAllButtonActionPerformed

    private void clearButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearButtonActionPerformed
        EventList<CartAssignment> selected = getSelectionModel().getSelected();
        for (int i = 0; i < selected.size(); i++) {
            CartAssignment ca = selected.get(i);
            ca.clearPlayers();
            selected.set(i, ca);
        }
    }//GEN-LAST:event_clearButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton clearButton;
    private javax.swing.Box.Filler filler1;
    private javax.swing.JButton selectAllButton;
    // End of variables declaration//GEN-END:variables

    private class CartSelectionListener implements ListSelectionListener {
        private final ListSelectionModel columnSelectionModel;
        private final ListSelectionModel selectionModel;

        public CartSelectionListener(ListSelectionModel columnSelectionModel, ListSelectionModel selectionModel) {
            this.columnSelectionModel = columnSelectionModel;
            this.selectionModel = selectionModel;
        }

        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (settingSelection) {
                return;
            }

            int col = columnSelectionModel.getMinSelectionIndex();
            table.setDragEnabled(col >= 1);
            if (col == 0) {
                selectionModel.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
            }
            else if (selectionModel.isSelectionEmpty() == false) {
                selectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            }
        }

    }

    public static void main(String args[]) {
        JDialog d = new JDialog();
        CartListEditor cartListEditor = new CartListEditor();
        cartListEditor.setCartCount(10);
        d.setContentPane(cartListEditor);
        d.pack();
        d.setVisible(true);
    }

}
