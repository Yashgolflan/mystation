/*
 *
 */
package com.stayprime.comm.gprs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author benjamin
 */
public class Countdown {
    private static final Logger log = LoggerFactory.getLogger(Countdown.class);

    private int initialCountdown;
    private int interval;
    private int countdown;
    private int actionCounter = 0;

    public Countdown(int initialCountdown, int interval) {
        setInitialCountdown(initialCountdown);
        setInterval(interval);
        countdown = initialCountdown;
    }

    public final void setInitialCountdown(int initialCountdown) {
        if (initialCountdown < 0) {
            throw new IllegalArgumentException("initialCountdown must be >= 0");
        }

        this.initialCountdown = initialCountdown;
    }

    public final void setInterval(int interval) {
        if (interval < 0) {
            throw new IllegalArgumentException("resetCount must be >= 0");
        }

        this.interval = interval;
    }

    public int getActionCounter() {
        return actionCounter;
    }

    public void start() {
        countdown = initialCountdown;
    }

    //TODO rename to clear
    public void clearCountdown() {
        countdown = interval;
    }

    public boolean countdown() {
        if (interval > 0) {
            countdown--;
            if (countdown <= 0) {
                clearCountdown();
                actionCounter++;
                return true;
            }
            else {
                log.trace("Countdown: " + countdown);
            }
        }

        return false;
    }

}
