/*
 * 
 */
package com.stayprime.device;

import java.util.concurrent.TimeUnit;

/**
 *
 * @author benjamin
 */
public class Time {
    private static final long startNanoTime = System.nanoTime();

    public static long milliTime() {
        return TimeUnit.NANOSECONDS.toMillis(nanoTime());
    }

    public static long nanoTime() {
        return System.nanoTime() - startNanoTime;
    }

    public static long toLocalTime(long millis) {
        return System.currentTimeMillis() - (milliTime() - millis);
    }

}
