/*
 * 
 */
package com.stayprime.golf.config.settings;

/**
 *
 * @author benjamin
 */
public class ModeSetting extends Setting {
    private Mode mode = Mode.disable;

    public ModeSetting(String key) {
        super(key);
    }

    public ModeSetting(String key, String value) {
        super(key);
        set(value);
    }

    @Override
    public final void set(String fnb) {
        try {
            mode = Mode.valueOf(fnb);
            super.set(fnb);
        }
        catch (Exception ex) {
        }
    }

    public Mode getMode() {
        return mode;
    }

}
