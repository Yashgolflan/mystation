/*
 * 
 */

package com.stayprime.basestation2.ui.modules;

import ca.odell.glazedlists.gui.AdvancedTableFormat;
import ca.odell.glazedlists.impl.sort.ComparableComparator;
import com.stayprime.hibernate.entities.CartInfo;
import com.stayprime.hibernate.entities.CartUnit;
import java.util.Comparator;
import java.util.Map;

/**
 *
 * @author benjamin
 */
public class CartTableFormat implements AdvancedTableFormat<CartInfo> {
    private static ComparableComparator comparableComparator = new ComparableComparator();
    private Map<String, CartUpdateStatus> updateMap;

    public int getColumnCount() {
        return 9;
    }

    public String getColumnName(int column) {
	int i = 0;
        if(column == i++)      return "Cart";
        else if(column == i++) return "MAC";
        else if(column == i++) return "IP";
        else if(column == i++) return "WiFi time";
        else if(column == i++) return "Position time";
        else if(column == i++) return "Software";
        else if(column == i++) return "Course";
        else if(column == i++) return "Ads";
        else if(column == i++) return "Pins";
        else if(column == i++) return "Menu";
//        else if(column == i++) return "Settings updated";
        throw new IllegalStateException();
    }

    @Override
    public Object getColumnValue(CartInfo cart, int column) {
	int i = 0;
        if(column == i++)
	    return cart.getCartNumber();
        else if(column == i++)
	    return cart.getMacAddress();
        else if(column == i++)
	    return cart.getCartUnit() == null? null : cart.getCartUnit().getIpAddress();
        else if(column == i++)
	    return cart.getCartUnit() == null? null : cart.getCartUnit().getIpUpdated();
        else if(column == i++)
	    return cart == null? null : cart.getLocationLastUpdated();
        else if(column == i++)
	    return getSoftware(cart);
        else if(column == i++)
	    return cart.getCartUnit() == null? null : cart.getCartUnit().getCourseUpdated();
        else if(column == i++)
	    return cart.getCartUnit() == null? null : cart.getCartUnit().getAdsUpdated();
        else if(column == i++)
	    return cart.getCartUnit() == null? null : cart.getCartUnit().getPinlocationUpdated();
        else if(column == i++)
	    return cart.getCartUnit() == null? null : cart.getCartUnit().getMenuUpdated();
//        else if(column == i++)
//	    return cart.getDefinition() == null? null : cart.getDefinition().settingsUpdated;
        
        throw new IllegalStateException();
    }

    public Class getColumnClass(int column) {
	int i = 0;
        if(column == i++)
	    return Integer.class; //cartNumber
        else if(column == i++)
	    return String.class; //macAddress
        else if(column == i++)
	    return String.class; //ipAddress
        else if(column == i++)
	    return Object.class; //ipUpdated
        else if(column == i++)
	    return Object.class; //positionUpdated
        else if(column == i++)
	    return String.class; //updateStatus
        else if(column == i++)
	    return Object.class; //courseUpdated
        else if(column == i++)
	    return Object.class; //adsUpdated
        else if(column == i++)
	    return Object.class; //pinLocationUpdated
//        else if(column == i++)
//	    return Object.class; //settingsUpdated
        else if(column == i++)
	    return Object.class; //menuUpdated

        throw new IllegalStateException();
    }

    public Comparator getColumnComparator(int column) {
	return comparableComparator;
    }

    public void setUpdateStatusMap(Map<String, CartUpdateStatus> updateStatus) {
        this.updateMap = updateStatus;
    }

    private Object getSoftware(CartInfo cart) {
        CartUnit unit = cart.getCartUnit();
        if (unit == null) {
            return null;
        }

        if (updateMap != null && updateMap.containsKey(cart.getMacAddress())) {
            CartUpdateStatus status = updateMap.get(cart.getMacAddress());
            if (status != null & status.getInProgressUpdate() != null) {
                return status.toString();
            }
        }

        return cart.getCartUnit().getSoftwareVersion();
    }

}