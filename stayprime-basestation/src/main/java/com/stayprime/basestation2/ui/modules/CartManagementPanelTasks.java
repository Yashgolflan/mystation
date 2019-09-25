/*
 * 
 */
package com.stayprime.basestation2.ui.modules;

import com.ezware.dialog.task.TaskDialogs;
import com.stayprime.basestation2.BaseStation2App;
import com.stayprime.basestation2.services.CartService;
import com.stayprime.hibernate.entities.CartInfo;
import com.stayprime.hibernate.entities.CartUnit;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jdesktop.application.Application;
import org.jdesktop.application.Task;

/**
 *
 * @author benjamin
 */
public class CartManagementPanelTasks {

    private static final Logger log = LoggerFactory.getLogger(CartManagementPanel.class);

    private final CartManagementPanel panel;

    private CartService cartService;

    public CartManagementPanelTasks(CartManagementPanel panel) {
        this.panel = panel;
    }

    public void setCartService(CartService cartService) {
        this.cartService = cartService;
    }

    public Task getLoadTask() {
            BaseStation2App app = Application.getInstance(BaseStation2App.class);
        return new ReadCartsStatusTask(app);
        }

    public Task getDeleteCartTask(CartInfo selectedCart, boolean deleteGps) {
        BaseStation2App app = Application.getInstance(BaseStation2App.class);
        Operation op = deleteGps? Operation.DELETE_CART_AND_GPS : Operation.DELETE_CART;
        return new DeleteActionTask(app, selectedCart, op);
    }

    private void notifyError(Throwable error) {
        TaskDialogs.showException(error);
    }

    private class ReadCartsStatusTask extends org.jdesktop.application.Task<Object, Void> {
        private List<CartInfo> carts = Collections.EMPTY_LIST;

        ReadCartsStatusTask(org.jdesktop.application.Application app) {
            super(app);
        }

        @Override
        protected Object doInBackground() {
             setMessage("loading..");
            try {
                   
                carts = cartService.listCartsAndUnits();
                Thread.sleep(4000);
            } catch (Exception ex) {
                log.error("ReadCartsStatusTask.doInBackground: " + ex);
                log.debug("ReadCartsStatusTask.doInBackground: " + ex, ex);
                return ex;
            }

            return null;
        }

        @Override
        protected void succeeded(Object result) {

            if (result instanceof Exception) {
                Exception exception = (Exception) result;
                setMessage("Error reading carts status: " + exception);
                //notifyError(exception);
            } else if (carts == null) {
                setMessage("Error reading carts status. ");
            } else {

                panel.setCartsList(carts);
            }
        }
    }

    private class DeleteActionTask extends org.jdesktop.application.Task<List<CartInfo>, Void> {

        private CartInfo cartInfo;
        private Operation operation;
        //   private List<CartInfo>carts;

        DeleteActionTask(Application app, CartInfo cartInfo, Operation operation) {
            super(app);
            this.cartInfo = cartInfo;
            this.operation = operation;
        }

        @Override
        protected List<CartInfo> doInBackground() {
            CartUnit delUnit = cartInfo == null ? null : cartInfo.getCartUnit();
            boolean cartDeleted = true;

            switch (operation) {
                case DELETE_CART_AND_GPS:
                case DELETE_CART:
                    setMessage("Deleting cart from database");
                    cartService.deleteCart(cartInfo);
                    cartDeleted = true;
                    if (operation == Operation.DELETE_CART) {
                        break;
                    }

                case DELETE_GPS:
                    if (delUnit != null) {
                        if (!cartDeleted) {
                            clearCartUnit(delUnit.getMacAddress());
                        }
                        setMessage("Deleting device from database");
                        cartService.deleteUnit(delUnit);

                    }
            }

            return cartService.listCartsAndUnits();
        }

        /**
         * Check if there is a cart with the given unit and clear it. Otherwise
         * CartInfo.cartUnit fk will be invalid.
         *
         * @param macAddress the CartUnit's mac address to check and clear
         */
        private void clearCartUnit(String macAddress) {
            CartInfo cart = cartService.getCart(macAddress);
            if (cart != null) {
                cart.setCartUnit(null);
                cartService.saveCart(cart);
            }
        }

        @Override
        protected void failed(Throwable ex) {

            log.error("SaveActionTask.doInBackground: " + ex);
            log.debug("SaveActionTask.doInBackground: " + ex, ex);
            setMessage("Cart definition database operation failed: " + ex);
            notifyError(ex);
        }

        @Override
        protected void succeeded(List<CartInfo> result) {
           
            switch (operation) {
                case DELETE_CART:
                    setMessage("Done deleting Cart");
                    break;
                case DELETE_GPS:
                    setMessage("Done deleting GPS");
                    break;
            }
            System.out.println("------------------////" + result.size());
            if(result.contains(cartInfo))
            {
                result.remove(cartInfo);
                
                
                
            }
            panel.CartDelete=true;
                panel.setCartsList(result);
            //cartService.getLocalStorage().saveCarts(cartService.listCartsAndUnits());
            cartService.getLocalStorage().saveCarts(result);

        }

    }

    private enum Operation {
        DELETE_CART, DELETE_GPS, DELETE_CART_AND_GPS
    };

}
