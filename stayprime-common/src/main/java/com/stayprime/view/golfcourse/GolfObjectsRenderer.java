/*
 * 
 */

package com.stayprime.view.golfcourse;

import com.stayprime.model.golf.Position;
import com.stayprime.view.objects.DrawableImageSpec;
import com.stayprime.util.MathUtil;
import com.stayprime.view.objects.Alignment;
import com.stayprime.view.objects.BasicObjectRenderer;
import com.stayprime.view.objects.MeasureObject;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.net.URL;
import javax.imageio.ImageIO;

/**
 *
 * @author benjamin
 */
public class GolfObjectsRenderer {

    private DrawableImageSpec cartSpec;
    private BufferedImage cartAheadImages[];

    private DrawableImageSpec cartAheadSpec;
    private BufferedImage cartImages[];
    private BufferedImage cartImageBuffer;
    private BufferedImage walkerImage;
    private BufferedImage walkerImageBuffer;

    private DrawableImageSpec pinFlagSpec;
    private BufferedImage pinFlag;

    private DrawableImageSpec overlayIconSpec;
    private BufferedImage unlockedIcon;
    private BufferedImage rangerIcon;
    private BufferedImage atacIcon;

    private Font measurementFont = new Font("Sans", Font.BOLD, 20);
    private Font cartNumberFont = new Font("Sans", Font.BOLD, 14);
    private final Color measurementColor = Color.white;
    private final Color textBackground = new Color(0xaa000000, true);
    private final Stroke measurementStroke;
    public static final Stroke doubleStroke = new BasicStroke(2f);
    public static final Stroke basicStroke = new BasicStroke(1f);

    private final ColorFilter colorFilter = new ColorFilter();

    private final Color normalPaint = Color.white;
    private final Color okPaint = Color.green;
    private final Color cautionPaint = Color.orange;
    private final Color warningPaint = Color.red;
    private final Color commLostPaint = Color.gray;

    public GolfObjectsRenderer() {
        measurementStroke = new BasicStroke(2f, BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND, 0, new float[] {5, 5}, 5);
    }

    public Stroke getDashedStroke() {
        return measurementStroke;
    }

    public void setMeasurementFont(Font measurementFont) {
        this.measurementFont = measurementFont;
    }

    public void setCartNumberFont(Font cartNumberFont) {
        this.cartNumberFont = cartNumberFont;
    }

    public void loadCartImages() {
        cartImages = new BufferedImage[25];
        cartSpec = new DrawableImageSpec(0.5f, 0.5f, 15f, 40f, 60f);
        cartImageBuffer = loadImage("com/stayprime/golfcourse/resources/cart/0.png");
        walkerImage = loadImage("com/stayprime/golfcourse/resources/cart/walker.png");
        walkerImageBuffer = loadImage("com/stayprime/golfcourse/resources/cart/walker.png");
        loadCart360Images("com/stayprime/golfcourse/resources/cart/", cartImages);
    }

    public void loadCartAheadImages() {
        cartAheadImages = new BufferedImage[25];
        cartAheadSpec = new DrawableImageSpec(0.5f, 0.5f, 12f, 34f, 55f);
        loadCart360Images("com/stayprime/golfcourse/resources/cartahead/", cartAheadImages);
    }

    public static void loadCart360Images(String resourcePath, BufferedImage images[]) {
        ClassLoader ccl = Thread.currentThread().getContextClassLoader();
        try {
            int angleStep = 360 / (images.length - 1);
	    for (int i = 0; i < images.length; i++) {
		int angle = -180 + i * angleStep;
		URL url = ccl.getResource(resourcePath + angle + ".png");
		images[i] = ImageIO.read(url);
	    }
	}
	catch (Exception ex) {
	    //log.error("Error loading images: " + ex);
	}
    }

    public void loadPinFlagImage() {
        pinFlag = loadImage("com/stayprime/golfcourse/resources/flag.png");
        pinFlagSpec = new DrawableImageSpec(0.11f, 0.98f, 6f, 30f, 40f);
    }

    public void loadOverlayIcons() {
        unlockedIcon = loadImage("com/stayprime/golfcourse/resources/unlocked_16.png");
        rangerIcon = loadImage("com/stayprime/golfcourse/resources/security_16.png");
        atacIcon = loadImage("com/stayprime/golfcourse/resources/security_16.png");
        overlayIconSpec = new DrawableImageSpec(0.5f, 0.5f, 8f, 16f, 16f);
    }

    public static BufferedImage loadImage(String resourceUrl) {
        ClassLoader ccl = Thread.currentThread().getContextClassLoader();
	try {
	    URL url = ccl.getResource(resourceUrl);
	    return ImageIO.read(url);
	}
	catch (Exception ex) {
	    //log.error("Error loading images: " + ex);
            return null;
	}
    }

    public void renderCartAheadGraphic(Graphics2D g2d, Position p, double heading, double scale) {
        renderCartAheadGraphic(g2d, p.getX(), p.getY(), heading, scale);
    }

    public Rectangle2D renderCartAheadGraphic(Graphics2D g2d, double x, double y, double heading, double scale) {
	BufferedImage image = getCartImage(cartAheadImages, heading);
	return renderImage(g2d, x, y, image, cartAheadSpec, scale);
    }

    public Rectangle2D renderCartGraphic(Graphics2D g2d, Position p, double heading, double scale) {
	BufferedImage image = getCartImage(cartImages, heading);
	return renderImage(g2d, p.getX(), p.getY(), image, cartSpec, scale);
    }

    public Rectangle2D renderCartGraphic(Graphics2D g2d, Point2D p, double heading, double scale) {
	BufferedImage image = getCartImage(cartImages, heading);
	return renderImage(g2d, p.getX(), p.getY(), image, cartSpec, scale);
    }

    public Rectangle2D renderUnlockedIcon(Graphics2D g2d, Point2D p, double scale) {
	return renderImage(g2d, p, unlockedIcon, overlayIconSpec, scale);
    }

    public Rectangle2D renderRangerIcon(Graphics2D g2d, Point2D p, double scale) {
	return renderImage(g2d, p, rangerIcon, overlayIconSpec, scale);
    }
    
    public Rectangle2D renderResortIcon(Graphics2D g2d, Point2D p, double scale) {
	return renderImage(g2d, p, atacIcon, overlayIconSpec, scale);
    }

    public void renderCartCircle(Graphics2D g2d, Point2D p, String label, Color color) {
        BasicObjectRenderer.renderTextCircle(g2d, p, label, cartNumberFont, Color.black, color, Color.black, basicStroke);
    }

    public Rectangle2D renderPinFlag(Graphics2D g2d, Position p, double scale) {
        return renderImage(g2d, p.getX(), p.getY(), pinFlag, pinFlagSpec, scale);
    }

    public Rectangle2D renderColoredCartGraphic(Graphics2D g2d, Position p, double heading, Color color, double scale) {
        return renderColoredCartGraphic(g2d, p.getX(), p.getY(), heading, color, scale);
//        return renderColoredWalkerGraphic(g2d, p.getX(), p.getY(), heading, color, scale);
    }

    public Rectangle2D renderColoredCartGraphic(Graphics2D g2d, Point2D p, double heading, Color color, double scale) {
        return renderColoredCartGraphic(g2d, p.getX(), p.getY(), heading, color, scale);
//        return renderColoredWalkerGraphic(g2d, p.getX(), p.getY(), heading, color, scale);
    }

    public Rectangle2D renderColoredCartGraphic(Graphics2D g2d, double x, double y, double heading, Color color, double scale) {
        colorFilter.setColor(color);
	colorFilter.filter(getCartImage(cartImages, heading), cartImageBuffer);
	return renderImage(g2d, x, y, cartImageBuffer, cartSpec, scale);
//	BufferedImage image = cartPaceIcons[paceIndex];
//	renderImage(g2d, p, image, cartPaceSpec, scale);
    }
    
    public Rectangle2D renderColoredWalkerGraphic(Graphics2D g2d, double x, double y, double heading, Color color, double scale) {
        colorFilter.setColor(color);
	colorFilter.filter(walkerImage, walkerImageBuffer);
	return renderImage(g2d, x, y, walkerImageBuffer, cartSpec, scale);
    }

    public static Rectangle2D renderImage(Graphics2D g2d, Point2D pos, BufferedImage image, DrawableImageSpec spec, double scale) {
        return renderImage(g2d, pos.getX(), pos.getY(), image, spec, scale);
    }

    public static Rectangle2D renderImage(Graphics2D g2d, double x, double y, BufferedImage image, DrawableImageSpec spec, double scale) {
	//double mapMeters = CoordinateCalculations.fastDistanceInMeters(map.getTopLeftCoordinates(), map.getTopRightCoordinates());
	double imgPix = spec.meterWidth * scale;
		///mapMeters*imageRect.getWidth();
	double s;

	if(imgPix < spec.minPixelWidth)
	    s = spec.minPixelWidth/image.getWidth();
	else if(imgPix > spec.maxPixelWidth)
	    s = spec.maxPixelWidth/image.getWidth();
	else
	    s = imgPix/image.getWidth();

	double w = image.getWidth()*s, h = image.getHeight()*s;
        double ix = x - w*spec.centerX;
        double iy = y - h*spec.centerY;

        Rectangle2D.Double bounds = new Rectangle2D.Double(ix, iy, w, h);
	g2d.drawImage(image, (int) ix, (int) iy, (int) w, (int) h, null);
        return bounds;
    }

    private BufferedImage getCartImage(BufferedImage images[], double angle) {
        int imgAngle = (int) MathUtil.normalizeAngle(Math.round(angle)) / 15;
        return images[imgAngle + 12];
    }
    
    private BufferedImage getWalkerImage() {
        return walkerImage;
    }

    public void renderBalloon(Graphics2D g2d, MeasureObject m, float offset) {
        renderBalloon(g2d, m, offset, 0);
    }

    public void renderBalloon(Graphics2D g2d, MeasureObject m, float xoffset, float yoffset) {
        Position p1 = m.getPoint1();
        String text = m.getText();
        renderBalloon(g2d, p1, text, xoffset, yoffset);
    }

    public void renderBalloon(Graphics2D g2d, Position p, String text,
            float xoffset, float yoffset) {
        BasicObjectRenderer.renderBalloon(g2d, p.getX(), p.getY(), text,
                measurementFont, measurementColor, textBackground,
                measurementColor, doubleStroke, xoffset, yoffset);
    }

    public void renderBalloon(Graphics2D g2d, Point2D p, String text,
            float xoffset, float yoffset) {
        BasicObjectRenderer.renderBalloon(g2d, p.getX(), p.getY(), text,
                measurementFont, measurementColor, textBackground,
                measurementColor, doubleStroke, xoffset, yoffset);
    }

    public void renderBalloon(Graphics2D g2d, Position p, String text, Font font,
            Alignment align, Color lineColor, Stroke lineStroke, float xoffset, float yoffset) {
        renderBalloon(g2d, p.getX(), p.getY(), text, font, align, lineColor, lineStroke, xoffset, yoffset);
    }

    public void renderBalloon(Graphics2D g2d, Point2D p, String text, Font font,
            Alignment align, Color lineColor, Stroke lineStroke, float xoffset, float yoffset) {
        renderBalloon(g2d, p.getX(), p.getY(), text, font, align, lineColor, lineStroke, xoffset, yoffset);
    }

    public void renderBalloon(Graphics2D g2d, double x, double y, String text, Font font,
            Alignment align, Color lineColor, Stroke lineStroke, float xoffset, float yoffset) {
        BasicObjectRenderer.renderBalloon(g2d, x, y, text,
                font, align, measurementColor, textBackground,
                null, null, xoffset, yoffset);
    }

    public void renderMeasure(Graphics2D g2d, MeasureObject m) {
        Position p1 = m.getPoint1();
        Position p2 = m.getPoint2();
        BasicObjectRenderer.renderCircle(g2d, p1, 10, null, measurementColor, doubleStroke);
        BasicObjectRenderer.renderCircle(g2d, p2, 10, null, measurementColor, doubleStroke);

        Line2D.Double line = new Line2D.Double(p1.getX(), p1.getY(), p2.getX(), p2.getY());
        BasicObjectRenderer.renderShape(g2d, line, null, measurementColor, measurementStroke);

        Point2D p = new Point2D.Double((p1.getX() + p2.getX()) / 2, (p1.getY() + p2.getY()) / 2);
        BasicObjectRenderer.renderTextRect(g2d, p.getX(), p.getY(), m.getText(), measurementFont,
                measurementColor, textBackground, null, null);
    }

    public Color getCartStatusPaint(int paceIndex) {
        switch (paceIndex) {
            case 0: return commLostPaint;
            case 1: return normalPaint;
            case 2: return okPaint;
            case 3: return cautionPaint;
            case 4: return warningPaint;
            default: return normalPaint;
        }
    }

}
