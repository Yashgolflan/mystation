/*
 * 
 */
package com.stayprime.golf.config.settings;

/**
 *
 * @author benjamin
 */
public enum Mode {
    disable, enable, auto;

    public static Mode enable(Mode mode) {
        if (mode == auto) {
                return auto;
        }
        else {
            return enable;
        }
    }
}
