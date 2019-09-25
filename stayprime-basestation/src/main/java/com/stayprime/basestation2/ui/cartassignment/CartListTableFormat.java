/*
 * 
 */
package com.stayprime.basestation2.ui.cartassignment;

import ca.odell.glazedlists.gui.AdvancedTableFormat;
import ca.odell.glazedlists.gui.WritableTableFormat;
import com.stayprime.hibernate.entities.CartAssignment;
import java.util.Comparator;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author benjamin
 */
class CartListTableFormat implements AdvancedTableFormat<CartAssignment>, WritableTableFormat<CartAssignment> {

    @Override
    public int getColumnCount() {
        return 3;
    }

    @Override
    public String getColumnName(int col) {
        int i = 0;
        //            if (col == i++) return "Use";
        if (col == i++) {
            return "Cart";
        }
        if (col == i++) {
            return "Player 1";
        }
        if (col == i++) {
            return "Player 2";
        }
        throw new IllegalArgumentException();
    }

    @Override
    public Object getColumnValue(CartAssignment c, int col) {
        int i = 0;
        //            if (col == i++) return c.isForScoring();
        if (col == i++) {
            return c.getCartNumber();
        }
        if (col == i++) {
            return c.getPlayer(0);
        }
        if (col == i++) {
            return c.getPlayer(1);
        }
        throw new IllegalArgumentException();
    }

    @Override
    public Class getColumnClass(int col) {
        int i = 0;
        if (col == i++) {
            return Integer.class;
        }
        return String.class;
    }

    @Override
    public Comparator getColumnComparator(int col) {
        return null;
    }

    @Override
    public boolean isEditable(CartAssignment ca, int col) {
        return col > 0;
    }

    @Override
    public CartAssignment setColumnValue(CartAssignment ca, Object o, int col) {
        if (col > 0) {
            String name = StringUtils.stripToNull((String) o);
            ca.setPlayer(col - 1, name);
        }
        return ca;
    }
    
}
