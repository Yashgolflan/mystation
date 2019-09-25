/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jhotdraw.geom;

import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.io.Serializable;

/**
 *
 * @author benjamin
 */
public abstract class PolyLine2D extends Polygon2D {
    public static class Double extends Polygon2D.Double implements Serializable {
        @Override
        public PathIterator getPathIterator(AffineTransform at) {
            return new PolyLinePathIteratorDouble(this, at);
        }

    }

    public static class Float extends Polygon2D.Float implements Serializable {
        @Override
        public PathIterator getPathIterator(AffineTransform at) {
            return new PolyLinePathIteratorFloat(this, at);
        }
    }

    static class PolyLinePathIteratorDouble extends PolygonPathIteratorDouble {
        public PolyLinePathIteratorDouble(Double pg, AffineTransform at) {
            super(pg, at);
        }

        @Override
        public boolean isDone() {
            return index >= poly.npoints;
        }
    }

    private static class PolyLinePathIteratorFloat extends PolygonPathIteratorFloat {
        public PolyLinePathIteratorFloat(Float pg, AffineTransform at) {
            super(pg, at);
        }

        @Override
        public boolean isDone() {
            return index >= poly.npoints;
        }
    }
}
