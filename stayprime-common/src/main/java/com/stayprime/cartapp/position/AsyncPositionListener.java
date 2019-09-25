/*
 * 
 */
package com.stayprime.cartapp.position;

import com.stayprime.geo.Coordinates;

/**
 *
 * @author benjamin
 */
public interface AsyncPositionListener {
    public void positionUpdated(Coordinates position, boolean valid, boolean good, float speed, float heading);
}
