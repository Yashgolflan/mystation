/*
 * 
 */

package com.stayprime.basestation2.ui.mainview;

import org.apache.commons.lang.ArrayUtils;

/**
 *
 * @author benjamin
 */
public enum PaceFilter {
    Active,
    Caution,
    Warning,
    Marshall,
    All;

    public PaceFilter next() {
        PaceFilter[] values = PaceFilter.values();
        int index = ArrayUtils.indexOf(values, this);
        return values[(index + 1) % values.length];
    }
}
