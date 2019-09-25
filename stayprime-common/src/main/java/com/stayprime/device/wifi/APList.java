/*
 * 
 */

package com.stayprime.device.wifi;

import com.stayprime.device.wifi.APInfo;
import java.util.ArrayList;
import java.util.Collection;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author benjamin
 */
public class APList<T extends APInfo> extends ArrayList<T> {

    public APList() {
	super();
    }

    public APList(int count) {
	super(count);
    }

    public APList(Collection<T> c) {
	super(c);
    }

    public T getBSSID(String bssid) {
	for(T info: this) {
	    if(StringUtils.equalsIgnoreCase(info.getESSID(), bssid))
		return info;
	}

	return null;
    }

}
