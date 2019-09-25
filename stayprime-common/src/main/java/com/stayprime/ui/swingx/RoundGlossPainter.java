/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.stayprime.ui.swingx;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import org.jdesktop.swingx.painter.GlossPainter;

/**
 *
 * @author benjamin
 */
public class RoundGlossPainter extends GlossPainter {
    private int roundX, roundY;

    public RoundGlossPainter(Paint paint, GlossPosition position, int roundX, int roundY) {
        super(paint, position);
        this.roundX = roundX;
        this.roundY = roundY;
    }

    public RoundGlossPainter(GlossPosition position, int roundX, int roundY) {
        super(position);
        this.roundX = roundX;
        this.roundY = roundY;
    }

    public RoundGlossPainter(Paint paint, int roundX, int roundY) {
        super(paint);
        this.roundX = roundX;
        this.roundY = roundY;
    }

    public RoundGlossPainter(int roundX, int roundY) {
        super();
        this.roundX = roundX;
        this.roundY = roundY;
    }


    @Override
    protected void doPaint(Graphics2D g, Object component, int width, int height) {
        if (getPaint() != null) {
            Ellipse2D ellipse = new Ellipse2D.Double(-width / 2.0,
                height / 2.7, width * 2.0,
                height * 2.0);

            Area gloss = new Area(ellipse);
            if (getPosition() == GlossPosition.TOP) {
                Area area = new Area(new Rectangle(0, 0,
                    width, height));
                area.subtract(new Area(ellipse));
                gloss = area;
            }

            Area roundRect = new Area(new RoundRectangle2D.Double(0, 0, width, height, roundX, roundY));
            gloss.intersect(roundRect);
            /*
            if(getClip() != null) {
                gloss.intersect(new Area(getClip()));
            }*/
            g.setPaint(getPaint());
            g.fill(gloss);
        }
    }
}
