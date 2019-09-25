/*
 * 
 */
package com.stayprime.golfapp.round;

import com.stayprime.device.Time;
import com.stayprime.golf.course.GolfHole;

/*
 * Internal utility classes and interfaces
 */
/**
 * Class to keep track of the time when a hole was entered or activated.
 */
public class HoleStat {
    private final GolfHole hole;

    private long firstEnteredTime;

    private long lastTeeboxTime;

    private long lastHoleTime;

    private boolean holePlayed = false;

    public HoleStat(GolfHole hole) {
        firstEnteredTime = Time.milliTime();
        lastTeeboxTime = firstEnteredTime;
        lastHoleTime = firstEnteredTime;
        this.hole = hole;
    }

    public GolfHole getHole() {
        return hole;
    }

    public void updateLastTeeboxTime() {
        lastTeeboxTime = Time.milliTime();
        lastHoleTime = lastTeeboxTime;
    }

    public void updateLastHoleTime() {
        lastHoleTime = Time.milliTime();
    }

    public long getFirstEnteredTime() {
        return firstEnteredTime;
    }

    public void setFirstEnteredTime(long firstEnteredTime) {
        this.firstEnteredTime = firstEnteredTime;
    }

    public long getLastTeeboxTime() {
        return lastTeeboxTime;
    }

    public void setLastTeeboxTime(long lastTeeboxTime) {
        this.lastTeeboxTime = lastTeeboxTime;
    }

    public long getLastHoleTime() {
        return lastHoleTime;
    }

    public void setLastHoleTime(long lastHoleTime) {
        this.lastHoleTime = lastHoleTime;
    }

    public boolean isHolePlayed() {
        return holePlayed;
    }

    public void setHolePlayed() {
        this.holePlayed = true;
    }

}
