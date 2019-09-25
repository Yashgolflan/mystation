/*
 *
 */
package com.stayprime.view.objects;

import com.stayprime.model.golf.Position;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import java.awt.geom.RoundRectangle2D;

/**
 *
 * @author benjamin
 */
public class BasicObjectRenderer {
    public static RectangularShape renderTextCircle(Graphics2D g2d, Point2D pos,
            String text, Font font, Color textColor, Color fillColor,
            Color strokeColor, Stroke stroke) {
        return renderTextRoundRect(g2d, pos.getX(), pos.getY(), text, font, textColor, fillColor, strokeColor, stroke, true, Alignment.MID_CENTER);
    }

    public static RectangularShape renderTextRect(Graphics2D g2d, double x, double y,
            String text, Font font, Color textColor, Color fillColor,
            Color strokeColor, Stroke stroke) {
        return renderTextRect(g2d, x, y, text, font, textColor, fillColor, strokeColor, stroke, Alignment.MID_CENTER);
    }

    public static RectangularShape renderTextRect(Graphics2D g2d, double x, double y,
            String text, Font font, Color textColor, Color fillColor,
            Color strokeColor, Stroke stroke, Alignment align) {
        return renderTextRoundRect(g2d, x, y, text, font, textColor, fillColor, strokeColor, stroke, false, align);
    }

    public static RectangularShape renderTextRoundRect(Graphics2D g2d, double px, double py,
            String text, Font font, Color textColor, Color fillColor,
            Color strokeColor, Stroke stroke, boolean circle, Alignment align) {

        Rectangle2D textBounds = getTextBounds(g2d, text, font);
        Point2D rectDimension = getTextRectDimension(textBounds);
        Point2D offset = getTextRectOffset(rectDimension, align);

        RoundRectangle2D shape = getTextRect(textBounds, rectDimension, px, py, circle, align);
        renderShape(g2d, shape, fillColor, strokeColor, stroke);

        if(textColor != null) {
            g2d.setPaint(textColor);
            g2d.drawString(text,
                    (float) (px - textBounds.getWidth() / 2 + offset.getX()),
                    (float) (py + textBounds.getHeight()/2 - (textBounds.getHeight() + textBounds.getY()) + offset.getY()));
        }

        return shape;
    }

    public static RoundRectangle2D getTextRect(Rectangle2D textBounds, Point2D d,
            double px, double py, boolean circle, Alignment align) {

        double x = px - d.getX()/2;
        double y = py - d.getY()/2;
        double hOffset = align.horizontal * d.getX()/2;
        double vOffset = align.vertical * d.getY()/2;
        double corner = circle? d.getY() : 4;
        return new RoundRectangle2D.Double(x + hOffset, y + vOffset, d.getX(), d.getY(), corner, corner);
    }

    public static Rectangle2D getTextBounds(Graphics2D g2d, String text, Font font) {
        g2d.setFont(font);
        FontMetrics fm = g2d.getFontMetrics();
        return fm.getStringBounds(text, g2d);
    }

    /**
     * Return the width and height of a rectangle with margins for text display.
     * The text height is increased by 2.
     * The width is at least the same as height, or text width + 6
     * @param textBounds
     * @return the width and height of the rectangle as Point2D
     */
    public static Point2D getTextRectDimension(Rectangle2D textBounds) {
        double h = textBounds.getHeight() + 2;
        double w = (textBounds.getWidth() + 6 < h)? h : (textBounds.getWidth() + 6);
        return new Point2D.Double(w, h);
    }

    public static Point2D getTextRectOffset(Point2D dim, Alignment align) {
        return new Point2D.Double(align.horizontal * dim.getX()/2, align.vertical * dim.getY()/2);
    }

    public static void renderPolygon(Graphics2D g2d, PolygonObject poly, Stroke stroke) {
        renderShape(g2d, poly.getPolygon(), poly.getFillColor(), poly.getLineColor(), stroke);
    }

    public static void renderCircle(Graphics2D g2d, Position center, float radius,
        Color fillColor, Color lineColor, Stroke stroke) {
        renderCircle(g2d, center.getX(), center.getY(), radius, fillColor, lineColor, stroke);
    }

    public static void renderCircle(Graphics2D g2d, double cx, double cy, float radius,
        Color fillColor, Color lineColor, Stroke stroke) {
        double originX = cx - radius;
        double originY = cy - radius;
        Ellipse2D e = new Ellipse2D.Double(originX, originY, radius*2, radius*2);
        renderShape(g2d, e, fillColor, lineColor, stroke);
    }

    public static void renderShape(Graphics2D g2d, Shape shape,Color fillColor,
            Color lineColor, Stroke stroke) {

        fillShape(g2d, shape, fillColor);
        strokeShape(g2d, shape, lineColor, stroke);
    }

    public static void fillShape(Graphics2D g2d, Shape shape, Color fillColor) {
        if(fillColor != null) {
            g2d.setColor(fillColor);
            g2d.fill(shape);
        }
    }

    public static void strokeShape(Graphics2D g2d, Shape shape, Color lineColor, Stroke stroke) {
        if(lineColor != null && stroke != null) {
            g2d.setColor(lineColor);
            g2d.setStroke(stroke);
            g2d.draw(shape);
        }
    }

    public static void renderBalloon(Graphics2D g2d, double px, double py, String text, Font font,
            Color textColor, Color fillColor, Color lineColor, Stroke lineStroke, float xoffset) {
        renderBalloon(g2d, px, py, text, font, textColor, fillColor, lineColor, lineStroke, xoffset, 0);
    }

    public static void renderBalloon(Graphics2D g2d, double px, double py, String text, Font font,
            Color textColor, Color fillColor, Color lineColor, Stroke lineStroke, float xoffset, float yoffset) {
        Alignment align = xoffset >= 0? Alignment.TOP_RIGHT : Alignment.TOP_LEFT;
        renderBalloon(g2d, px, py, text, font, align, textColor,
                fillColor, lineColor, lineStroke, xoffset, yoffset);
    }

    /**
     *
     * @param g2d
     * @param px
     * @param py
     * @param text
     * @param font
     * @param align
     * @param textColor
     * @param fillColor
     * @param lineColor
     * @param lineStroke
     * @param xoffset
     * @param yoffset
     */
    public static void renderBalloon(Graphics2D g2d, double px, double py,
            String text, Font font, Alignment align,
            Color textColor, Color fillColor,
            Color lineColor, Stroke lineStroke,
            float xoffset, float yoffset) {

        Shape l = new Line2D.Double(new Point2D.Double(px, py), new Point2D.Double(px + xoffset, py + yoffset));

        Rectangle2D textBounds = getTextBounds(g2d, text, font);
        Point2D rectDimension = getTextRectDimension(textBounds);
        Point2D offset = getTextRectOffset(rectDimension, align);
        double rectX = px + xoffset - (rectDimension.getX())*align.horizontal;
        double rectY = py + yoffset;
        Shape shape = getTextRect(textBounds, rectDimension, rectX, rectY, false, align);
        renderShape(g2d, shape, fillColor, null, null);

        if(textColor != null) {
            g2d.setPaint(textColor);
            g2d.drawString(text,
                    (float) (rectX - textBounds.getWidth() / 2 + offset.getX()),
                    (float) (rectY + textBounds.getHeight()/2 - (textBounds.getHeight() + textBounds.getY()) + offset.getY()));
        }

        strokeShape(g2d, l, lineColor, lineStroke);
    }

}
