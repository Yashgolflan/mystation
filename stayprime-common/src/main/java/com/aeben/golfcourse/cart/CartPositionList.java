/*
 * 
 */

package com.aeben.golfcourse.cart;

import java.util.List;

/**
 *
 * @author benjamin
 */
public interface CartPositionList {
    public CartPosition getCartPosition(int cartId);
    public List<CartPosition> getCartPositionList();

    public void addListObserver(ListObserver o);
    public void removeListObserver(ListObserver o);

    public interface ListObserver {
	public void positionsUpdated(CartPositionList list);
	public void positionInformationAdded(CartPositionList list, CartPosition positionInfo);
	public void positionInformationRemoved(CartPositionList list, CartPosition positionInfo);
    }
}
