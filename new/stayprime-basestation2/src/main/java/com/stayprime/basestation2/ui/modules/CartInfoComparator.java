/*
 * 
 */
package com.stayprime.basestation2.ui.modules;

import com.stayprime.hibernate.entities.CartInfo;
import java.util.Comparator;

/**
 *
 * @author benjamin
 */
public class CartInfoComparator implements Comparator<CartInfo> {
    @Override
    public int compare(CartInfo o1, CartInfo o2) {
        if (o1 == null && o2 == null) {
            return 0;
        }
        if (o1 == null) {
            return Integer.MIN_VALUE;
        }
        if (o2 == null) {
            return Integer.MAX_VALUE;
        }
        return o1.getCartNumber() - o2.getCartNumber();
    }
}
