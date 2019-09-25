/*
 * 
 */
package com.stayprime.basestation2.comm.model;

import com.stayprime.model.golf.GolfCart;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author benjamin
 */
public class CartList {
    private final List<GolfCart> carts;
    private final List<GolfCart> readOnlyCarts;

    public CartList() {
        carts = new ArrayList<GolfCart>();
        readOnlyCarts = Collections.unmodifiableList(carts);
    }

    public int findCartNumber(int cartNumber) {
        for (int i = 0; i < carts.size(); i++) {
            GolfCart c = carts.get(i);
            if (c.getNumber() == cartNumber) {
                return i;
            }
        }
        return -1;
    }

    public boolean put(GolfCart cart) {
        if (cart == null) {
            return false;
        }

        int index = findCartNumber(cart.getNumber());

        if (index >= 0) {
            carts.remove(index);
        }

        carts.add(0, cart);
        return true;
    }

    public boolean remove(GolfCart cart) {
        int index = findCartNumber(cart.getNumber());

        if (index >= 0) {
            carts.remove(index);
            return true;
        }
        else {
            return false;
        }
    }

    public List<GolfCart> getList() {
        return readOnlyCarts;
    }

}
