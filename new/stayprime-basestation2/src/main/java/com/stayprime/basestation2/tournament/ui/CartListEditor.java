/*
 *
 */
package com.stayprime.basestation2.tournament.ui;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.gui.AdvancedTableFormat;
import ca.odell.glazedlists.gui.WritableTableFormat;
import ca.odell.glazedlists.swing.DefaultEventSelectionModel;
import com.stayprime.hibernate.entities.CartInfo;
import com.stayprime.tournament.model.CartAssignment;
import com.stayprime.tournament.model.CartAssignments;
import com.stayprime.tournament.model.Player;
import com.stayprime.ui.DocumentChangeListener;
import com.stayprime.ui.editor.ListEditor;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumnModel;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

/**
 *
 * @author benjamin
 */
public class CartListEditor extends ListEditor<CartAssignment> {
    private String allCarts = "All";
    private boolean settingSelection = false;
    private boolean settingRangeField = false;

    public CartListEditor() {
        initComponents();
        initRangeSelector();
        setTableFormat(new CartAssignmentTableFormat());
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    private void initRangeSelector() {
        rangeField.getDocument().addDocumentListener(new DocumentChangeListener() {
            @Override
            public void documentChanged(DocumentEvent e) {
                useCheckBox.setSelected(false);
                if (settingRangeField == false) {
                    selectCartsRange(StringUtils.trim(rangeField.getText()));
                }
            }
        });
    }

    private void selectCartsRange(String range) {
        boolean all = range.equalsIgnoreCase(allCarts);
        if (all) {
            selectAll(true);
            return;
        }

        String[] ranges = StringUtils.split(range, '-');
        boolean valid = false;
        if (ranges != null && ranges.length > 0) {
            int n1 = NumberUtils.toInt(ranges[0]);
            int n2 = ranges.length > 1? NumberUtils.toInt(ranges[1]) : n1;
            int min = Math.min(n1, n2);
            int max = Math.max(n1, n2);
            if (max > 0) {
                valid = true;
                if (n1 <= 0 || n2 <= 0) {
                    selectCarts(max, max);
                }
                else {
                    selectCarts(min, max);
                }
            }
        }

        if (valid == false) {
            selectAll(false);
        }
    }

    @Override
    protected void setupTableColumns() {
        TableColumnModel columnModel = table.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(10);
        columnModel.getColumn(1).setPreferredWidth(10);
        columnModel.getColumn(2).setPreferredWidth(40);
        columnModel.getColumn(3).setPreferredWidth(40);

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

    public void clearCartPlayers() {
        EventList<CartAssignment> oldCarts = getSourceList();
        EventList<CartAssignment> carts = new BasicEventList<CartAssignment>(oldCarts.size());
        for (int i = 0; i < oldCarts.size(); i++) {
            CartAssignment cart = oldCarts.get(i);
            carts.add(i, new CartAssignment(cart.getCartNumber()));
        }
        setEventList(carts);
    }

    public boolean addCartPlayer(int cart, Player p) {
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
            ca.setForScoring(forScoring);
            selected.set(i, ca);
        }
    }

    private void selectAll(boolean select) {
        selectCarts(true, !select, 0, 0);
    }

    private void selectCarts(int min, int max) {
        selectCarts(false, false, min, max);
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

    public void takeCartsForScoring(CartAssignments ca) {
        if (ca != null && ca.isEmpty() == false) {
            for (CartAssignment a : getSourceList()) {
                if (ca.isForScoring(a.getCartNumber())) {
                    a.setForScoring(true);
                }
            }
        }
    }

    private class CartAssignmentTableFormat implements AdvancedTableFormat<CartAssignment>, WritableTableFormat<CartAssignment> {
        private final int useCol = 0;

        @Override
        public int getColumnCount() {
            return 4;
        }

        @Override
        public String getColumnName(int col) {
            int i = 0;
            if (col == i++) return "Use";
            if (col == i++) return "Cart";
            if (col == i++) return "Player 1";
            if (col == i++) return "Player 2";
            throw new IllegalArgumentException();
        }

        @Override
        public Object getColumnValue(CartAssignment c, int col) {
            int i = 0;
            if (col == i++) return c.isForScoring();
            if (col == i++) return c.getCartNumber();
            if (col == i++) return c.getPlayers().size() > 0? c.getPlayers().get(0) : null;
            if (col == i++) return c.getPlayers().size() > 1? c.getPlayers().get(1) : null;
            throw new IllegalArgumentException();
        }

        @Override
        public Class getColumnClass(int col) {
            if (col == useCol) return Boolean.class;
            return Object.class;
        }

        @Override
        public Comparator getColumnComparator(int col) {
            return null;
        }

        @Override
        public boolean isEditable(CartAssignment ca, int col) {
            return col == useCol;
        }

        @Override
        public CartAssignment setColumnValue(CartAssignment ca, Object o, int col) {
            if (col == useCol && o instanceof Boolean) {
                ca.setForScoring((Boolean) o);
                useCheckBox.setSelected(false);
            }
            return ca;
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

        titleLabel = new javax.swing.JLabel();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        rangeLabel = new javax.swing.JLabel();
        allCartsButton = new javax.swing.JButton();
        rangeField = new com.stayprime.ui.PlaceholderTextField();
        useCheckBox = new javax.swing.JCheckBox();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(1, 32), new java.awt.Dimension(1, 32), new java.awt.Dimension(1, 32));

        setButtonsPanelVisible(false);
        setName("Form"); // NOI18N
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance().getContext().getResourceMap(CartListEditor.class);
        getContainerDelegate().setLayout(new javax.swing.BoxLayout(getContainerDelegate(), javax.swing.BoxLayout.LINE_AXIS));

        titleLabel.setFont(titleLabel.getFont().deriveFont(titleLabel.getFont().getStyle() | java.awt.Font.BOLD, titleLabel.getFont().getSize()+2));
        titleLabel.setText(resourceMap.getString("titleLabel.text")); // NOI18N
        titleLabel.setName("titleLabel"); // NOI18N
        getContainerDelegate().add(titleLabel);

        filler1.setName("filler1"); // NOI18N
        getContainerDelegate().add(filler1);

        rangeLabel.setText(resourceMap.getString("rangeLabel.text")); // NOI18N
        rangeLabel.setName("rangeLabel"); // NOI18N
        getContainerDelegate().add(rangeLabel);

        allCartsButton.setText(resourceMap.getString("allCartsButton.text")); // NOI18N
        allCartsButton.setName("allCartsButton"); // NOI18N
        allCartsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                allCartsButtonActionPerformed(evt);
            }
        });
        getContainerDelegate().add(allCartsButton);

        rangeField.setColumns(5);
        rangeField.setText(resourceMap.getString("rangeField.text")); // NOI18N
        rangeField.setMaximumSize(new java.awt.Dimension(80, 24));
        rangeField.setMinimumSize(new java.awt.Dimension(80, 24));
        rangeField.setName("rangeField"); // NOI18N
        rangeField.setPlaceholder(resourceMap.getString("rangeField.placeholder")); // NOI18N
        getContainerDelegate().add(rangeField);

        useCheckBox.setText(resourceMap.getString("useCheckBox.text")); // NOI18N
        useCheckBox.setName("useCheckBox"); // NOI18N
        useCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                useCheckBoxActionPerformed(evt);
            }
        });
        getContainerDelegate().add(useCheckBox);

        filler2.setName("filler2"); // NOI18N
        getContainerDelegate().add(filler2);
    }// </editor-fold>//GEN-END:initComponents

    private void useCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_useCheckBoxActionPerformed
        setSelectedForScoring(useCheckBox.isSelected());
    }//GEN-LAST:event_useCheckBoxActionPerformed

    private void allCartsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_allCartsButtonActionPerformed
        rangeField.setText(allCarts);
    }//GEN-LAST:event_allCartsButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton allCartsButton;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private com.stayprime.ui.PlaceholderTextField rangeField;
    private javax.swing.JLabel rangeLabel;
    private javax.swing.JLabel titleLabel;
    private javax.swing.JCheckBox useCheckBox;
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
            table.setDragEnabled(col >= 2);
            if (col == 1) {
                selectionModel.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
                setRangeField();
            }
            else if (selectionModel.isSelectionEmpty() == false) {
                selectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                setRangeField();
            }
        }

        private void setRangeField() {
            if (selectionModel.isSelectionEmpty()) {
                settingRangeField = true;
                rangeField.setText(null);
                settingRangeField = false;
            }
            else {
                List<CartAssignment> list = getSourceList();
                int min = list.get(selectionModel.getMinSelectionIndex()).getCartNumber();
                int max = list.get(selectionModel.getMaxSelectionIndex()).getCartNumber();
                settingRangeField = true;
                rangeField.setText(min == max ? Integer.toString(min) : min + "-" + max);
                settingRangeField = false;
            }
        }
    }

}
