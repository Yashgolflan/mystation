/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.stayprime.geo;

/**
 *
 * @author benjamin
 */
public interface MapImage {
    public String getImageAddress();
    public Coordinates getTopLeft();
    public Coordinates getTopRight();
    public Coordinates getBottomLeft();
    public Coordinates getBottomRight();
    public boolean contains(Coordinates coord);
    public String getMapCoordinates();
}
