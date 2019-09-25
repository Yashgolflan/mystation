/*
 * 
 */

package com.aeben.golfcourse.view;

import com.aeben.elementos.mapview.MapProjection;
import com.aeben.elementos.mapview.Projection;
import com.stayprime.util.MathUtil;
import com.stayprime.geo.Coordinates;
import com.aeben.golfcourse.cart.PositionInfo;
import com.aeben.golfcourse.elements.Area;
import com.aeben.golfcourse.elements.Point;
import com.stayprime.device.UnitInfo;
import com.stayprime.geo.BasicMapImage;
import com.stayprime.geo.CoordinateCalculations;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.util.List;
import org.jhotdraw.geom.PolyLine2D;
import org.jhotdraw.geom.Polygon2D;

/**
 *
 * @author benjamin
 */
public class BasicCourseObjectRenderer implements CourseObjectRenderer {
    private Graphics2D g2d;
    private MapProjection proj;
    private boolean quickDraw;

    private DrawableImageSpec cartSpec, cartAheadSpec;
    private BufferedImage cartAheadImages[], cartImages[];

    private DrawableImageSpec pinFlagSpec;
    private BufferedImage pinFlag;

    private Stroke areaStroke;
    private boolean fillAreas;

    public BasicCourseObjectRenderer() {
	areaStroke = new BasicStroke(2f);
	fillAreas = false;
    }

    public void setCartImages(BufferedImage cartImages[], DrawableImageSpec cartSpec) {
	this.cartImages = cartImages;
	this.cartSpec = cartSpec;
    }


    public void setCartAheadImages(BufferedImage cartAheadImages[], DrawableImageSpec cartAheadSpec) {
	this.cartAheadImages = cartAheadImages;
	this.cartAheadSpec = cartAheadSpec;
    }

    public void setPinFlagImage(BufferedImage pinFlag, DrawableImageSpec center) {
	this.pinFlag = pinFlag;
	this.pinFlagSpec = center;
    }

    public void setAreaStroke(Stroke areaStroke) {
	this.areaStroke = areaStroke;
    }

    public void setFillAreas(boolean fillAreas) {
	this.fillAreas = fillAreas;
    }

    public void start(Graphics g, MapProjection p, boolean quickDraw) {
	this.g2d = (Graphics2D) g.create();
	this.proj = p;
	this.quickDraw = quickDraw;

	g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
		quickDraw? RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR :
		    RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
		quickDraw? RenderingHints.VALUE_ANTIALIAS_OFF :
		    RenderingHints.VALUE_ANTIALIAS_ON);

    }

    public void done() {
	g2d.dispose();
    }

    public void renderCartGraphic(PositionInfo positionInformation) {
	renderCartGraphic(positionInformation, cartImages, cartSpec);
    }

    public void renderCartAheadGraphic(PositionInfo positionInformation) {
	renderCartGraphic(positionInformation, cartAheadImages, cartAheadSpec);
    }

    public void renderCartAheadGraphic(Coordinates coordinates) {
	renderCartGraphic(coordinates, 90f, cartAheadImages, cartAheadSpec);
    }

    public void renderCartAheadGraphic(Point2D location) {
	renderCartGraphic(location, 90f, cartAheadImages, cartAheadSpec);
    }

    private void renderCartGraphic(PositionInfo positionInformation, BufferedImage[] images, DrawableImageSpec spec) {
	Coordinates coordinates = positionInformation.getCoordinates();
	Float heading = positionInformation.getHeading() - (float) Math.toDegrees(proj.getAngle());

	renderCartGraphic(coordinates, heading, images, spec);
    }

    private void renderCartGraphic(Coordinates coordinates, Float heading, BufferedImage[] images, DrawableImageSpec spec) {
	if(coordinates != null) {
	    Point2D location = getPoint(coordinates);
	    renderCartGraphic(location, heading, images, spec);
	}
    }

    private void renderCartGraphic(Point2D location, Float heading, BufferedImage[] images, DrawableImageSpec spec) {
	BufferedImage image = getCartImage(images,heading);
	renderImage(image, spec, location, g2d, proj);
    }

    public void renderPinFlag(Point2D location) {
	if(location != null) {
//	    double s;
//
//	    if(proj.getScale()*0.1f*pinFlag.getWidth() < 20f)
//		s = 20f / pinFlag.getWidth();
//	    else
//		s = proj.getScale() * 0.1f;

	    renderImage(pinFlag, pinFlagSpec, location, g2d, proj);
	}
    }

    public void renderCartBall(UnitInfo unitInformation) {
	
    }

    public void renderArea(Area area) {
	renderArea(area, g2d, fillAreas, areaStroke, proj);
    }

    public void renderPoint(Point p) {
	renderPoint(p.getLocation(), p.getColor());
    }

    public void renderPoint(Coordinates location, Color color) {
        if(location != null) {
            renderPoint(location, color, g2d, proj);
        }
    }
    
    private void renderPoint(Coordinates coord, Color color, Graphics2D g2d, Projection p) {
        Point2D point = p.project(coord);
	//CoordinateCalculations.getXYRelativeToMapPixelCoordinates(coord, map, imageRect);


        Ellipse2D.Float ellipse = new Ellipse2D.Float((float) (point.getX() - 3), (float) (point.getY() - 3), 5, 5);
        g2d.setColor(color);
        g2d.fill(ellipse);
        g2d.setColor(Color.black);
        g2d.setStroke(new BasicStroke(1f));
        g2d.draw(ellipse);

    }

    public void renderLineAndText(Coordinates point1, Coordinates point2, Stroke stroke, 
	    Color lineColor, String text, Font font, Color textColor, Color textBackground, float position) {
        Point2D p1 = proj.project(point1);
	//CoordinateCalculations.getXYRelativeToMapPixelCoordinates(point1, map, imageRect);
        
        Point2D p2 = proj.project(point2);
	//CoordinateCalculations.getXYRelativeToMapPixelCoordinates(point2, map, imageRect);

	Point2D.Double p3 = new Point2D.Double(
		(p2.getX()-p1.getX())*position,
		(p2.getY()-p1.getY())*position);

	g2d.setFont(font);
	FontMetrics fm = g2d.getFontMetrics();
	int w = fm.stringWidth(text);
	int h = fm.getHeight();
	float textX = (float) (p1.getX() + p3.getX() - w / 2f);
	float textY = (float) (p1.getY() + p3.getY() - h / 2f);
	
	Rectangle2D.Float textBox = new Rectangle2D.Float(textX-2, textY-2, w+4, h+4);
	
	Shape clip = g2d.getClip();
	java.awt.geom.Area a = new java.awt.geom.Area(clip);
	java.awt.geom.Area b = new java.awt.geom.Area(textBox);
	a.exclusiveOr(b);
	g2d.setClip(a);

	Line2D.Double line = new Line2D.Double(p1, p2);
	g2d.setColor(lineColor);
	g2d.setStroke(stroke);
	g2d.draw(line);
	g2d.setClip(clip);

	if(textBackground != null) {
	    g2d.setColor(textBackground);
	    g2d.fill(new RoundRectangle2D.Double(textBox.x - 2, textBox.y - 2, textBox.width+4, textBox.height+4, 4, 4));
	}

	g2d.setColor(textColor);
	g2d.drawString(text, textX, (float) (p1.getY() + p3.getY() + fm.getAscent() - (fm.getDescent() + fm.getAscent()) / 2f));

//	double propLen = 0;
//	if(Math.abs(p2.x-p1.x) < textBox.width) {
//	    //Box limit could be vertical
//	    if(Math.abs(p2.y-p1.y) > textBox.height) {
//		//Vertical limit
//		propLen = (Math.abs(p2.y - p1.y) - textBox.height) / Math.abs(p2.y - p1.y);
//	    }
//	}
//	else if(Math.abs(p2.y-p1.y) < textBox.height) {
//	    //Box limit is horizontal
//	    propLen = (Math.abs(p2.x - p1.x) - textBox.width) / Math.abs(p2.x - p1.x);
//	}
//	else {
//	    //Slope of the measurement line and the textbox corners
//	    double m = Math.abs((p2.y-p1.y)/(p2.x/p1.x));
//	    double t = Math.abs(textBox.height/textBox.width);
//
//	    if(t < m) //Vertical limit
//		propLen = (Math.abs(p2.y - p1.y) - textBox.height) / Math.abs(p2.y - p1.y);
//	    else //Horizontal limit
//		propLen = (Math.abs(p2.x - p1.x) - textBox.width) / Math.abs(p2.x - p1.x);
//	}
//
//	Point2D.Double p3a = new Point2D.Double(p1.x+(p2.x - p1.x)*propLen/2, p1.y+(p2.y + p1.y)*propLen/2);
//	g2d.draw(new Line2D.Double(p1, p3a));

    }

    public void renderHazard(Point2D p1, Stroke stroke, Color lineColor, String text, 
	    Font font, Color textColor, Color textBackground, float pos) {
	g2d.setFont(font);
	FontMetrics fontMetrics = g2d.getFontMetrics();
	int w = fontMetrics.stringWidth(text);
	int h = fontMetrics.getAscent();

	int vOffset = 2;
	Rectangle2D.Double rect = new Rectangle2D.Double(p1.getX(), p1.getY() - h - vOffset,
		w, h + vOffset);

	boolean right = pos >= 0;
	float offset = right? 0 : (float) -rect.getWidth();
	rect.x += pos + offset;

//	boolean right = false;
//	if(rect.getX() + rect.getWidth() > imageRect.getX() + imageRect.getWidth())
//	    rect.x -= 2*pos + rect.getWidth();
//	else
//	    right = true;

//	if(proj.getBounds().contains(rect)) {
	    if(textBackground != null) {
		g2d.setColor(textBackground);
		g2d.fill(new RoundRectangle2D.Double(rect.x - 2, rect.y - 2, rect.width+4, rect.height+6, 4, 4));
	    }
	    g2d.setColor(textColor);
	    g2d.drawString(text, (float) rect.x, (float) (rect.y + rect.height - vOffset));
	    g2d.setColor(lineColor);
	    g2d.setStroke(stroke);
	    //Line2D.Double l = new Line2D.Double(p1, new Point2D.Double(p1.getX() + pos*(right?1:-1), p1.getY() - pos));
	    //g2d.draw(l);
	    //l.setLine(l.getP2(), new Point2D.Double(p1.getX() + (pos+rect.getWidth())*(right?1:-1), p1.getY() - pos));
	    Line2D.Double l = new Line2D.Double(p1, new Point2D.Double(p1.getX() + pos, p1.getY()));
	    g2d.draw(l);
//	}
    }

    public void renderLine(Coordinates point1, Coordinates point2, Stroke stroke, Color color) {
        Point2D p1 = proj.project(point1);
//	CoordinateCalculations.getXYRelativeToMapPixelCoordinates(point1, map, imageRect);

	Point2D p2 = proj.project(point2);
//	CoordinateCalculations.getXYRelativeToMapPixelCoordinates(point2, map, imageRect);
    }

    /*
     * Implementation of drawing methods
     */
    public void renderCircle(Coordinates c, int radius, Color color, boolean fill, Stroke stroke) {
	renderCircle(c, radius, color, g2d, fill, stroke, proj);
    }

    public static void renderCircle(Coordinates c, int radius, Color color, 
	    Graphics2D g2d, boolean fill, Stroke stroke, Projection p) {
	Point2D point = p.project(c);

	if(color != null) {
	    Ellipse2D e = new Ellipse2D.Double(point.getX() - radius, point.getY() - radius,
		    radius*2, radius*2);
	    g2d.setColor(color);

	    if(fill)
		g2d.fill(e);

	    if(stroke != null) {
		g2d.setStroke(stroke);
		g2d.draw(e);
	    }
	}
    }

    public static Polygon2D.Float getAreaShape(Area area, Projection p) {
	Polygon2D.Float shape = new Polygon2D.Float();

	for(int i = 0; i < area.getShape().size(); i++) {
	    Point2D point = p.project(area.getShape().get(i));
	    shape.addPoint(point.getX(), point.getY());
	}
	
	return shape;
    }
    
    public static PolyLine2D.Float getShape(List<Coordinates> outline, Projection p) {
	PolyLine2D.Float shape = new PolyLine2D.Float();

	for(int i = 0; i < outline.size(); i++) {
	    Point2D point = p.project(outline.get(i));
	    shape.addPoint(point.getX(), point.getY());
	}
	
	return shape;
    }

    public static void renderArea(Area area, Graphics2D g2d, boolean fill, Stroke stroke, Projection p) {
	Polygon2D.Float shape = getAreaShape(area, p);

	Color color = area.getColor();

	if(color != null) {
	    g2d.setColor(color);

	    if(fill)
		g2d.fill(shape);

	    if(stroke != null) {
		g2d.setStroke(stroke);
		g2d.draw(shape);
	    }
	}
    }

     public static void renderImage(BufferedImage image, DrawableImageSpec spec,
	     Point2D pos, Graphics2D g2d, Projection p) {
	//double mapMeters = CoordinateCalculations.fastDistanceInMeters(map.getTopLeftCoordinates(), map.getTopRightCoordinates());
	double imgPix = spec.meterWidth * p.getScale();
		///mapMeters*imageRect.getWidth();
	double s;

	if(imgPix < spec.minPixelWidth)
	    s = spec.minPixelWidth/image.getWidth();
	else if(imgPix > spec.maxPixelWidth)
	    s = spec.maxPixelWidth/image.getWidth();
	else
	    s = imgPix/image.getWidth();

	double w = image.getWidth()*s, h = image.getHeight()*s;

	g2d.drawImage(image,
		(int) (pos.getX() - w*spec.centerX),
		(int) (pos.getY() - h*spec.centerY),
		(int) w, (int) h, null);
    }

    public Shape renderNumberedPoint(Coordinates coord, int n, Color background, Color strokeColor, Color textColor, Font font, Stroke stroke, Graphics2D g2d, BasicMapImage map, double scale, Rectangle2D imageRect, Dimension canvasSize) {
        Point2D pos = CoordinateCalculations.getXYRelativeToMapPixelCoordinates(coord, map, imageRect);

        g2d.setFont(font);
        String label = Integer.toString(n);
        FontMetrics fm = g2d.getFontMetrics();
        Rectangle2D bounds = fm.getStringBounds(label, g2d);

        double height = bounds.getHeight() + 2, width = bounds.getWidth()+4 < height? height : bounds.getWidth()+6;
        RoundRectangle2D shape = new RoundRectangle2D.Double(
                imageRect.getX() + pos.getX() - width/2,
                imageRect.getY() + pos.getY() - height/2,
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
                    (float) (imageRect.getX() + pos.getX() - bounds.getWidth() / 2),
                    (float) (imageRect.getY() + pos.getY() + bounds.getHeight()/2 - (bounds.getHeight() + bounds.getY())));
        }

        return shape;
    }

    /*
     * Utility methods
     */
    private BufferedImage getCartImage(BufferedImage images[], float heading) {
        double topEdgeAngle = proj.getAngle();
//		CoordinateCalculations.getTopEdgeAngle(
//                map.getTopLeftCoordinates(), map.getBottomRightCoordinates(),
//                imageRect.getWidth(), imageRect.getHeight());

        int imgAngle = (int) MathUtil.normalizeAngle(Math.round(heading - topEdgeAngle)) / 15;

        return images[imgAngle + 12];
    }

    public Point2D getPoint(Coordinates coord) {
	if(coord == null)
	    return null;
	
	Point2D pos = proj.project(coord);
	
	return pos;
    }

}
