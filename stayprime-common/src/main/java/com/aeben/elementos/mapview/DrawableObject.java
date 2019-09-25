/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.aeben.elementos.mapview;

import java.awt.Shape;

/**
 *
 * @author benjamin
 */
public interface DrawableObject {
    public Shape getLastDrawnShape();
    public void setLastDrawnShape(Shape s);
}
