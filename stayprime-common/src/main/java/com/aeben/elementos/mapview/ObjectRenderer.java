/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.aeben.elementos.mapview;

import java.awt.Graphics;
import java.util.List;

/**
 * Defines an interface for object renderers for MapProjection based maps.
 * @author benjamin
 */

public interface ObjectRenderer {
    /**
     * Render the objects for this ObjectRenderer.
     * @param g Graphics object for rendering.
     * @param projection 
     * @param quickDraw 
     */
    public void renderObjects(Graphics g, MapProjection projection, boolean quickDraw);

    public void setDrawableObjects(List<DrawableObject> courseObjects);

    public List<DrawableObject> getDrawableObjects();

    public void reset();

}
