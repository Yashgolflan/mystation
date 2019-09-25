/*
 * 
 */

package com.aeben.elementos.mapview;

import java.awt.geom.AffineTransform;

/**
 *
 * @author benjamin
 */
public interface TransformProjection extends MapProjection {
    public boolean isModified();
    public void clearModified();
    public AffineTransform getMapTransform();
}
