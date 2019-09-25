/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.stayprime.ui.swingx;

import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.geom.GeneralPath;
import javax.swing.UIManager;
import org.jdesktop.swingx.painter.GlossPainter;
import org.jdesktop.swingx.painter.GlossPainter.GlossPosition;

/**
 *
 * @author benjamin
 */
public class CurveShapePainter extends GlossPainter {
    private Color background = Color.gray.darker();
    private Color highlight = Color.lightGray;
    private Color shadow = Color.gray;

    public CurveShapePainter(Paint paint, GlossPosition position) {
        super(paint, position);
    }

    public CurveShapePainter(GlossPosition position) {
        super(position);
    }

    public CurveShapePainter(Paint paint) {
        super(paint);
    }

    public CurveShapePainter() {
        super();
    }


    @Override
    protected void doPaint(Graphics2D g, Object component, int width, int height) {
        Graphics2D g2 = g;

        g2.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
                RenderingHints.VALUE_FRACTIONALMETRICS_ON);

        // draw a big square
        g2.setColor(background);
        g2.fillRect(0, 0, width, height);

        // create the curve shape
        GeneralPath curveShape = new GeneralPath(
                GeneralPath.WIND_NON_ZERO);
        curveShape.moveTo(0, height * .6f);
        curveShape.curveTo(width * .167f, height * 1.2f, width * .667f, height * -.5f, width,
                height * .75f);
        curveShape.lineTo(width, height);
        curveShape.lineTo(0, height);
        curveShape.lineTo(0, height * .8f);
        curveShape.closePath();

        GradientPaint gp = new GradientPaint(0, height, shadow, 0, 0, highlight);
        g2.setPaint(gp);
        g2.fill(curveShape);

    }
}