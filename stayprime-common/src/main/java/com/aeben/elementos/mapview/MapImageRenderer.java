/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.aeben.elementos.mapview;

import com.stayprime.geo.BasicMapImage;
import com.stayprime.geo.CoordinateCalculations;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.jhotdraw.geom.Polygon2D;

/**
 *
 * @author benjamin
 */
public class MapImageRenderer implements ObjectRenderer {
    private List<DrawableMapImage> images;
    private Color outlineColor = Color.black;
    private Stroke outlineStroke = new BasicStroke(1f);

    public MapImageRenderer() {
	images = new ArrayList<DrawableMapImage>();
    }

    public void setOutlineColor(Color outlineColor) {
	this.outlineColor = outlineColor;
    }

    public void setOutlineStroke(Stroke outlineStroke) {
	this.outlineStroke = outlineStroke;
    }

    public void renderObjects(Graphics g, MapProjection proj, boolean quickDraw) {
        if(images != null && images.size() > 0) {
            Graphics2D g2d = (Graphics2D) g.create();

            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    quickDraw? RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR :
                        RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    quickDraw? RenderingHints.VALUE_ANTIALIAS_OFF :
                        RenderingHints.VALUE_ANTIALIAS_ON);

            for(DrawableMapImage cachedMapImage: images) {
		    Shape s = renderMapImage(cachedMapImage, g2d, proj, outlineColor, outlineStroke);
		    cachedMapImage.setLastDrawnShape(s);
            }

            g2d.dispose();
        }
    }

    public static Shape renderMapImage(DrawableMapImage image, Graphics2D g2d,
	    Projection proj, Color outlineColor, Stroke outlineStroke) {

	return renderMapImage(image, proj, image.getCachedImage(), g2d,
		image.opacity, outlineColor, outlineStroke);
    }

    public static Shape renderMapImage(BasicMapImage map, Projection conv,
	    BufferedImage image, Graphics2D g2d, Float opacity,
	    Color outlineColor, Stroke outlineStroke) {

	return renderMapImage(
		conv.project(map.getTopLeft()),
		conv.project(map.getBottomRight()),
		image, g2d, opacity, outlineColor, outlineStroke);
    }

    public static Shape renderMapImage(DrawableMapImage image, Graphics2D g2d,
	    BasicMapImage courseMap, Rectangle2D imageRect, Dimension canvasSize,
	    Color outlineColor, Stroke outlineStroke) {
	if(image != null && image.hasFourCornerCoordinates() && image.getCachedImage() != null) {
	    Point2D.Double topLeft =
		CoordinateCalculations.getXYRelativeToMapPixelCoordinates(image.getTopLeft(),
		courseMap, imageRect);
	    Point2D.Double bottomRight =
		CoordinateCalculations.getXYRelativeToMapPixelCoordinates(image.getBottomRight(),
		courseMap, imageRect);

	    topLeft.x += imageRect.getX();
	    topLeft.y += imageRect.getY();
	    bottomRight.x += imageRect.getX();
	    bottomRight.y += imageRect.getY();

	    return renderMapImage(topLeft, bottomRight, image.getCachedImage(), g2d,
		    null, null, null);
	}

	return null;
    }

    public static Shape renderMapImage(Point2D topLeft, Point2D bottomRight,
	    BufferedImage image, Graphics2D g2d, Float opacity,
	    Color outlineColor, Stroke outlineStroke) {

	if(image != null) {
	    double diagonalAngle = Math.atan2(bottomRight.getY() - topLeft.getY(), bottomRight.getX() - topLeft.getX());
	    double topToDiagonalAngle = Math.atan2(image.getHeight(), image.getWidth());
	    double topAngle = diagonalAngle - topToDiagonalAngle;
	    //System.out.println("Top angle: " + Math.toDegrees(topAngle));

	    double imageScale = CoordinateCalculations.pitagoras(topLeft.getX() - bottomRight.getX(), topLeft.getY() - bottomRight.getY())
		    / CoordinateCalculations.pitagoras(image.getWidth(), image.getHeight());
	    
	    AffineTransform transform = AffineTransform.getTranslateInstance(topLeft.getX(), topLeft.getY());
	    transform.concatenate(AffineTransform.getScaleInstance(imageScale, imageScale));
	    transform.concatenate(AffineTransform.getRotateInstance(topAngle));

	    if(opacity != null) {
		Composite composite = g2d.getComposite();
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
		g2d.drawImage(image, transform, null);
		g2d.setComposite(composite);
	    }
	    else {
		g2d.drawImage(image, transform, null);
	    }

	    Point2D.Double topRight = new Point2D.Double(
		    topLeft.getX() + Math.cos(topAngle)*image.getWidth()*imageScale,
		    topLeft.getY() + Math.sin(topAngle)*image.getWidth()*imageScale);
	    Point2D.Double bottomLeft = new Point2D.Double(
		    bottomRight.getX() - Math.cos(topAngle)*image.getWidth()*imageScale,
		    bottomRight.getY() - Math.sin(topAngle)*image.getWidth()*imageScale);

	    Polygon2D.Double poly = new Polygon2D.Double();
	    poly.add(topLeft);
	    poly.add(topRight);
	    poly.add(bottomRight);
	    poly.add(bottomLeft);

	    if(outlineColor != null) {
		g2d.setColor(outlineColor);
		g2d.setStroke(outlineStroke);
		g2d.draw(poly);
	    }

	    return poly;
	}
	
	return null;
    }

    public void setDrawableObjects(List<DrawableObject> courseObjects) {
	images.clear();

	if(courseObjects != null) {
	    for(DrawableObject object: courseObjects) {
		if(object instanceof DrawableMapImage) {
		    DrawableMapImage drawableMapImage = (DrawableMapImage) object;
		    images.add(drawableMapImage);
		}
	    }
	}
    }

    public List getDrawableObjects() {
	return Collections.unmodifiableList(images);
    }

    public void reset() {
    }

}
