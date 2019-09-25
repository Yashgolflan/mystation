/*
 * 
 */

package com.aeben.golfcourse.elements;

import com.stayprime.geo.Coordinates;

/**
 *
 * @author benjamin
 */
public interface WarningZone extends Area {
    public Warning getWarning(Coordinates coord);
}
