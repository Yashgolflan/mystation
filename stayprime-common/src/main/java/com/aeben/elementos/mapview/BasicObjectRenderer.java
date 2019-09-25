/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.aeben.elementos.mapview;

import com.stayprime.geo.CoordinateCalculations;
import com.stayprime.imageview.ImageConverter;
import com.stayprime.geo.Coordinates;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.List;
import org.jhotdraw.geom.PolyLine2D;
import org.jhotdraw.geom.Polygon2D;

/**
 *
 * @author benjamin
 */
public class BasicObjectRenderer implements ObjectRenderer {
    protected List<DrawableObject> courseObjects;
    protected ImageConverter imageConverter;
    public static Stroke defaultStroke = new BasicStroke(2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);

    public BasicObjectRenderer() {
        this(null);
    }

    public BasicObjectRenderer(ImageConverter imageConverter) {
        this.imageConverter = imageConverter;
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
                Shape s = renderCourseObject(object, g2d, p);
                object.setLastDrawnShape(s);
            }

            g2d.dispose();
        }
    }

    public Shape renderCourseObject(DrawableObject object, Graphics2D g, Projection p) {
        if(object instanceof DrawableShapeObject) {
            return renderShapeObject((DrawableShapeObject) object, g, p);
        }
        else  if(object instanceof DrawableImageObject) {
            return renderImageObject((DrawableImageObject) object, g, p);
        }
        else  if(object instanceof DrawablePointObject) {
            return renderPointObject((DrawablePointObject) object, g, p);
        }
        else
            return null;
    }

    public Polygon2D.Double renderShapePoints(DrawableShapeObject object, Graphics2D g2d, Projection p) {
	return renderShape(object.getShapeCoordinates(), getFillColor(object),
		getStrokeColor(object), null,
		object.isClosedShape(), g2d, p);
    }

    public Polygon2D.Double renderShapeObject(DrawableShapeObject object, Graphics2D g2d, Projection p) {
	return renderShape(object.getShapeCoordinates(), getFillColor(object),
		getStrokeColor(object), getStroke(object, 1),
		object.isClosedShape(), g2d, p);
    }

    public Polygon2D.Double renderShape(List<Coordinates> shape, Color fill, Color line,
	    Stroke stroke, boolean closedShape, Graphics2D g2d, Projection p) {
        if(shape != null && shape.size() > 0) {
            Polygon2D.Double poly;

            if(closedShape)
                poly = new Polygon2D.Double();
            else
                poly = new PolyLine2D.Double();

            for(int i = 0; i < shape.size(); i++) {
                Point2D point = p.project(shape.get(i));
		poly.addPoint(point.getX(), point.getY());
            }

            Color color;

            if(closedShape) {
                color = fill;
                if(color != null) {
                    g2d.setColor(color);
                    g2d.fill(poly);
                }
            }

	    color = line;
	    if(color != null) {
		if(stroke != null) {
		    g2d.setStroke(stroke);
		    g2d.setColor(color);
		    g2d.draw(poly);
		}
		else {
		    g2d.setColor(color);
		    for(int i = 0; i < poly.npoints; i++) {
                        g2d.setColor(Color.white);
			g2d.fillOval(Math.round((float) (poly.xpoints[i]-1)), Math.round((float) (poly.ypoints[i]-1)), 5, 5);
                        g2d.setColor(color);
			g2d.drawOval(Math.round((float) (poly.xpoints[i]-1)), Math.round((float) (poly.ypoints[i]-1)), 5, 5);
		    }
		}
	    }

            return poly;
        }

        return null;
    }

    public Shape renderImageObject(DrawableImageObject object, Graphics2D g2d, Projection p) {
        if(imageConverter != null && object.getCoordinates() != null && object.getImagePath() != null) {
            Point2D pos = p.project(object.getCoordinates());
	    //CoordinateCalculations.getXYRelativeToMapPixelCoordinates(object.getCoordinates(), map, imageRect);

            BufferedImage image = (BufferedImage) imageConverter.convertImage(object.getImagePath());

            if(image != null) {
                double imgDiagonal = CoordinateCalculations.pitagoras(image.getWidth(), image.getHeight());

                if(object.getDiagonalSizeInMeters() != null
                        && object.getMinDiagonalPixelSize() != null) {
                    double imgScale = object.getDiagonalSizeInMeters() / imgDiagonal
                            * p.getScale();

                    if(imgDiagonal*imgScale < object.getMinDiagonalPixelSize())
                        imgScale = object.getMinDiagonalPixelSize()/imgDiagonal;

                    g2d.drawImage(image,
                            (int) (pos.getX() - image.getWidth()*object.getRelativeImageCenter().getX()*imgScale),
                            (int) (pos.getY() - image.getHeight()*object.getRelativeImageCenter().getY()*imgScale),
                            (int) (image.getWidth()*imgScale), (int) (image.getHeight()*imgScale), null);
                }
                else {
                    g2d.drawImage(image,
                            (int) (pos.getX() - image.getWidth()*object.getRelativeImageCenter().getX()),
                            (int) (pos.getY() - image.getHeight()*object.getRelativeImageCenter().getY()),
                            null);
                }
            }
        }

        return null;
    }

    public Shape renderPointObject(DrawablePointObject object, Graphics2D g2d, Projection p) {
        if(object.getCoordinates() != null) {
            return renderPoint(object.getCoordinates(), object.getColor(), g2d, p);
        }

        return null;
    }

    public Shape renderPoint(Coordinates coord, Color color, Graphics2D g2d, Projection p) {
	return renderPoint(coord, color, 2.5f, g2d, p);
    }

    public Shape renderPoint(Coordinates coord, Color color, float radius, Graphics2D g2d, Projection p) {
        Point2D point = p.project(coord);
            //CoordinateCalculations.getXYRelativeToMapPixelCoordinates(coord, map, imageRect);

        g2d.setColor(color);
	float diam = 2*radius;
	Ellipse2D e = new Ellipse2D.Float((float) (point.getX() - radius), (float) (point.getY() - radius), diam, diam);
        g2d.fill(e);
        g2d.setColor(Color.black);
        g2d.setStroke(new BasicStroke(1f));
        g2d.draw(e);

        return null;
    }

    public Shape renderNumberedPoint(Coordinates coord, int n, Color background, Color strokeColor, Color textColor, Font font, Stroke stroke, Graphics2D g2d, Projection p) {
        Point2D pos = p.project(coord);
	//CoordinateCalculations.getXYRelativeToMapPixelCoordinates(coord, map, imageRect);


        g2d.setFont(font);
        String label = Integer.toString(n);
        FontMetrics fm = g2d.getFontMetrics();
        Rectangle2D bounds = fm.getStringBounds(label, g2d);

        double height = bounds.getHeight() + 2, width = bounds.getWidth()+4 < height? height : bounds.getWidth()+6;
        RoundRectangle2D shape = new RoundRectangle2D.Double(
                pos.getX() - width/2,
                pos.getY() - height/2,
                width, height, height, height);

        if(background != null) {
            g2d.setPaint(background);
            g2d.fill(shape);
            g2d.setColor(Color.black);
        }

        if(strokeColor != null && stroke != null) {
            g2d.setPaint(strokeColor);
            g2d.setStroke(stroke);
            g2d.draw(shape);
        }

        if(textColor != null) {
            g2d.setPaint(textColor);
            g2d.drawString(label,
                    (float) (pos.getX() - bounds.getWidth() / 2),
                    (float) (pos.getY() + bounds.getHeight()/2 - (bounds.getHeight() + bounds.getY())));
        }

        return shape;
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

    protected Stroke getStroke(DrawableShapeObject object) {
        return getStroke(object, 1);
    }

    protected Stroke getStroke(DrawableShapeObject object, float scale) {
	return getStroke(object.getStrokeWidth(), scale);
    }

    protected Stroke getStroke(float strokeWidth, float scale) {
        return new BasicStroke(strokeWidth * scale, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    }

    protected Color getStrokeColor(DrawableShapeObject object) {
        return new Color(object.getStrokeColor().getRGB());
    }

    protected Color getFillColor(DrawableShapeObject object) {
        return object.getFillColor();
    }

    public void reset() {
    }

    public static class DrawableObjectContainer {
        public DrawableObject object;
        public Rectangle box;

        public DrawableObjectContainer(DrawableObject object) {
            this.object = object;
        }
    }
}