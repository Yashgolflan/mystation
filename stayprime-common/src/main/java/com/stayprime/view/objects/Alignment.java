/*
 * 
 */
package com.stayprime.view.objects;

/**
 *
 * @author benjamin
 */
public enum Alignment {
    TOP_LEFT(-1, -1), TOP_CENTER(-1, 0), TOP_RIGHT(-1, 1),
    MID_LEFT(0, -1), MID_CENTER(0, 0), MID_RIGHT(0, 1),
    BOT_LEFT(1, -1), BOT_CENTER(1, 0), BOT_RIGHT(1, 1);

    public final int vertical;
    public final int horizontal;

    private Alignment(int vertical, int horizontal) {
        this.vertical = vertical;
        this.horizontal = horizontal;
    }

    public static Alignment get(int v, int h) {
        if (v < 0) {
            if (h < 0) return TOP_LEFT;
            if (h > 0) return TOP_RIGHT;
            return TOP_CENTER;
        }
        else if (v > 0) {
            if (h < 0) return BOT_LEFT;
            if (h > 0) return BOT_RIGHT;
            return BOT_CENTER;
        }
        else {
            if (h < 0) return MID_LEFT;
            if (h > 0) return MID_RIGHT;
            return MID_CENTER;
        }
    }
}
