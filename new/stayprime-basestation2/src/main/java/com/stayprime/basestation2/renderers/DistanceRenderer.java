/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.stayprime.basestation2.renderers;

import com.aeben.golfclub.GolfClub;
import com.aeben.golfclub.utils.Formatters;
import com.aeben.elementos.mapview.DrawableObject;
import com.aeben.elementos.mapview.MapProjection;
import com.aeben.elementos.mapview.ObjectRenderer;
import com.aeben.golfcourse.util.DistanceUnits;
import com.stayprime.geo.BasicMapImage;
import com.stayprime.geo.CoordinateCalculations;
import com.stayprime.geo.Coordinates;
import com.stayprime.geo.MapImage;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author benjamin
 */
public class DistanceRenderer implements ObjectRenderer {
    protected List<DrawableObject> courseObjects;

    private Banner measureBanner = new Banner();
    private Stroke stroke1px = new BasicStroke(1),
            stroke4px = new BasicStroke(4f),
            stroke1pxDash = new BasicStroke(1, BasicStroke.CAP_ROUND,
            BasicStroke.JOIN_ROUND, 0f, new float[] {10f, 10f}, 10f);
    private Color outlineColor = Color.WHITE;
    private Color fillColor = Color.BLACK;

    public DistanceRenderer() {
    }

    public void setOutlineColor(Color outlineColor) {
	this.outlineColor = outlineColor;
    }

    public void setFillColor(Color fillColor) {
	this.fillColor = fillColor;
    }

    public void renderObjects(Graphics g, MapProjection p, boolean quickDraw) {
        if(courseObjects != null) {
            Graphics2D g2d = (Graphics2D) g.create();

            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    quickDraw? RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR :
                        RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    quickDraw? RenderingHints.VALUE_ANTIALIAS_OFF :
                        RenderingHints.VALUE_ANTIALIAS_ON);

            for(DrawableObject object: courseObjects) {
                if(object instanceof DrawableDistance) {
                    DrawableDistance distance = (DrawableDistance) object;
                    paintMeasurement(distance, g2d, p, quickDraw);
                }
            }

            g2d.dispose();
        }
    }

    private void paintMeasurement(DrawableDistance distance, Graphics2D g2d, MapProjection p, boolean quickDraw) {
        Point2D p1 = p.project(distance.point1);
		//toViewPosition(distance.point1, map, imageRect);
        Point2D p2 = p.project(distance.point2);
		//toViewPosition(distance.point2, map, imageRect);

        //g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(outlineColor);

        g2d.setStroke(stroke1px);

	Ellipse2D ellipse = new Ellipse2D.Double(p1.getX() - 5, p1.getY() - 5, 10, 10);
        g2d.draw(ellipse);
	ellipse = new Ellipse2D.Double(p2.getX() - 5, p2.getY() - 5, 10, 10);
        g2d.draw(ellipse);
        //g2.drawLine(p2.x, p2.y - 12, p2.x, p2.y + 12);
        //g2.drawLine(p2.x - 12, p2.y, p2.x + 12, p2.y);

        g2d.setStroke(stroke1pxDash);

        if (true) { //Removed noPaint when out of the area
	    //p1.getX() > 0 && p1.getY() > 0
            //&& p1.getX() < getImageWidth() && p1.getY() < getImageHeight()) {

            g2d.draw(new Line2D.Double(p2, p1));
            measureBanner.borderColor = outlineColor;
            measureBanner.textColor = outlineColor;
            measureBanner.backgroundColor = fillColor;
            measureBanner.text = Formatters.distance1DecimalPlace(CoordinateCalculations.distanceInMeters(
                distance.point1, distance.point2), distance.units)+" "+ distance.units.getShortName();
            measureBanner.font = distance.font;

	    double pointX = 0;
	    double pointY = 0;

	    if(distance.position == DistancePosition.CENTER) {
		pointX = (p1.getX() + p2.getX())/2;
		pointY = (p1.getY() + p2.getY())/2;
	    }
	    else if(distance.position == DistancePosition.START_POINT) {
		pointX = p1.getX();
		pointY = p1.getY();
	    }
	    else if(distance.position == DistancePosition.END_POINT) {
		pointX = p2.getX();
		pointY = p2.getY();
	    }

	    paintBanner(g2d, (int) pointX, (int) pointY, BannerOrientation.CENTER, measureBanner);
        }
    }

    public void paintBanner(Graphics2D g2d, int x, int y, BannerOrientation orientation, Banner banner) {
        //Composite alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f);

        g2d.setFont(banner.font);
        FontMetrics fm = g2d.getFontMetrics(g2d.getFont());

        int width = 0, height = 0;
        int interline = -3, up = 0, down = 0, left = 2, right = 2;

        String lines[] = banner.text.split("\n");
        for (String line : lines) {
            width = fm.stringWidth(line) > width ? fm.stringWidth(line) : width;
            height += interline;
            height += fm.getHeight();
        }

        if (width > 0) {
            Rectangle rect = new Rectangle(x + 10, y - height - 10 - (up + down), width + (left + right), height + (up + down));
            //rect2 = new Rectangle(rect.x-20-rect.width, rect.y, rect.width, rect.height),
            //rect3 = new Rectangle(rect.x-20-rect.width, rect.y+20+rect.height, rect.width, rect.height),
            //rect4 = new Rectangle(rect.x, rect.y+20+rect.height, rect.width, rect.height);

            boolean leftSide = false, bottomSide = false;

            if (orientation == BannerOrientation.CENTER) {
                rect.x -= 10 + rect.width / 2;
                rect.y += 10 + rect.height / 2;
            }

	    //Removed code that calculates the right orientation by looking at the screen and pin flag

            //bannerRects.add(rect);
            AffineTransform original = g2d.getTransform();
            AffineTransform xform = new AffineTransform(original);
            xform.concatenate(AffineTransform.getTranslateInstance(x, y));
            xform.concatenate(AffineTransform.getScaleInstance(banner.scale, banner.scale));
            g2d.setTransform(xform);
            rect.x -= x;
            rect.y -= y;
            x = 0;
            y = 0;

            //g2d.setComposite(alpha);
            g2d.setColor(banner.backgroundColor);
            g2d.fillRoundRect(rect.x, rect.y, rect.width, rect.height, 6, 6);
            g2d.setStroke(stroke1px);
            g2d.setColor(banner.borderColor);
            g2d.drawRoundRect(rect.x, rect.y, rect.width, rect.height, 6, 6);
            //g2d.setComposite(comp);

            //g2d.setComposite(shapeAlpha);
            if (orientation != BannerOrientation.CENTER) {
                g2d.setColor(banner.backgroundColor);
                g2d.fillPolygon(new int[]{x + (leftSide ? -10 : 10), x, x + (leftSide ? -14 : 14)},
                        new int[]{y - (bottomSide ? -14 : 14), y, y - (bottomSide ? -10 : 10)}, 3);

                g2d.setColor(banner.borderColor);
                //g2d.setComposite(comp);
                g2d.drawPolyline(new int[]{x + (leftSide ? -10 : 10), x, x + (leftSide ? -14 : 14)},
                        new int[]{y - (bottomSide ? -14 : 14), y, y - (bottomSide ? -10 : 10)}, 3);
            }

            int origY = rect.y + up + fm.getAscent();

            g2d.setColor(banner.textColor);
            for (String line : lines) {
                g2d.drawString(line, rect.x + left, origY);
                origY += fm.getHeight() + interline;
            }

            g2d.setTransform(original);
        }
    }


      public void setDrawableObjects(List<DrawableObject> courseObjects) {
        if(courseObjects == null)
            this.courseObjects = null;
        else
            this.courseObjects = Collections.unmodifiableList(courseObjects);

        if(this.courseObjects != null) {
            for(DrawableObject object: this.courseObjects) {
                if(object != null)
                    object.setLastDrawnShape(null);
            }
        }

    }

    public List<DrawableObject> getDrawableObjects() {
        return courseObjects;
    }

    private Point toViewPosition(Coordinates point1, BasicMapImage map, Rectangle2D imageRect) {
        Point2D point =
            CoordinateCalculations.getXYRelativeToMapPixelCoordinates(point1, map, imageRect);

        Point p = new Point( (int) (imageRect.getX() + point.getX()), (int) (imageRect.getY() + point.getY()));
        return p;
    }

    public void reset() {
    }

    public enum DistancePosition {CENTER, START_POINT, END_POINT};
    public enum BannerOrientation {TOPRIGHT, TOPLEFT, BOTTOMLEFT, BOTTOMRIGHT, CENTER};

    public static class Banner {
        public String text = "";
        public Color textColor = Color.black, backgroundColor = Color.white, borderColor = Color.black;
        public Font font = defaultFont;
        public Coordinates origin = null;
        public Point screenCoords;
        public double scale = 1.0;
        public boolean show = true;
        public static final Font defaultFont = new Font(Font.SANS_SERIF, Font.PLAIN, 12);
    }
}
