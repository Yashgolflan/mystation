/*
 * 
 */
package com.stayprime.demo;

import com.stayprime.geo.Coordinates;

/**
 *
 * @author benjamin
 */
public interface Callback {
    public void updated(int cartNumber, Coordinates c, float heading, int holeNumber, int pace);
}
