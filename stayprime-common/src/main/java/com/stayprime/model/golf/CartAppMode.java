/*
 *
 */
package com.stayprime.model.golf;

/**
 *
 * @author benjamin
 */
public enum CartAppMode {
    GOLF(1),
    RANGER(2),
    GOLF_HANDICAP(3);

    public final int id;

    CartAppMode(int id) {
        this.id = id;
    }

    public int getCartMode() {
        return this.id;
    }
}
