/*
 * 
 */

package com.aeben.elementos.mapview;

import com.stayprime.geo.BasicMapImage;

/**
 *
 * @author benjamin
 */
public interface MapProjection extends Projection {
    public void setMap(BasicMapImage map);
    public BasicMapImage getMap();
}
