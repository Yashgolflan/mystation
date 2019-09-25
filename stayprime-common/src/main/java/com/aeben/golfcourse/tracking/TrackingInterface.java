/*
 * 
 */

package com.aeben.golfcourse.tracking;

/**
 *
 * @author benjamin
 */
public interface TrackingInterface {
    void setTrackingEnabled(boolean selected);
    void markLocation();
    void save();
}
