/*
 * 
 */
package com.stayprime.golf.config.settings;

import com.stayprime.model.golf.CartAppMode;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author benjamin
 */
public class CartAppModesSetting extends Setting {
    private List<CartAppMode> cartAppModeList;

    public CartAppModesSetting(String key) {
        super(key);
    }

    public CartAppModesSetting(String key, String value) {
        super(key);
        set(value);
    }

    @Override
    public final void set(String cartAppModes) {
        try {
            String[] modesSplit = StringUtils.split(cartAppModes, ',');
            CartAppMode[] modes = new CartAppMode[modesSplit.length];
            for (int i = 0; i < modesSplit.length; i++) {
                modes[i] = CartAppMode.valueOf(modesSplit[i].trim());
            }
            cartAppModeList = Collections.unmodifiableList(Arrays.asList(modes));
            super.set(cartAppModes);
        }
        catch (Exception ex) {
            //Do nothing and keep the old values
        }
    }

    public List<CartAppMode> getCartAppModeList() {
        return cartAppModeList;
    }

}
