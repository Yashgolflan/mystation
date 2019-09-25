/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.stayprime.basestation2.events;

import com.stayprime.hibernate.entities.CartInfo;
import java.util.Collection;

/**
 *
 * @author benjamin
 */
public interface CartInfoListener {
    public void cartInfoUpdated(Collection<CartInfo> status);
}
