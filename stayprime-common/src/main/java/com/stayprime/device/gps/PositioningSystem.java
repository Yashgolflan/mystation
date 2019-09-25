/*
 * 
 */
package com.stayprime.device.gps;

import com.aeben.golfcourse.cart.ObservablePositionInfo;

/**
 *
 * @author benjamin
 */
public interface PositioningSystem extends ObservablePositionInfo {
    public void start();
    public void stop();
    public void reset();
}
