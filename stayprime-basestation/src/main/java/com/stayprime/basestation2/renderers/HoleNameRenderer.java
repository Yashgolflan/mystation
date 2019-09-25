/*
 *
 */

package com.stayprime.basestation2.renderers;

import com.stayprime.basestation2.util.HoleGraphicDescription;
import com.aeben.elementos.mapview.DrawableObject;
import com.aeben.elementos.mapview.MapProjection;
import com.aeben.elementos.mapview.ObjectRenderer;
import com.aeben.golfclub.DrawableCourseShape;
import com.aeben.golfclub.DrawableHole;
import com.aeben.golfclub.HoleDefinition;
import com.stayprime.geo.BasicMapImage;
import com.stayprime.geo.Coordinates;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author benjamin
 */
public class HoleNameRenderer implements ObjectRenderer {
    private ArrayList<DrawableObject> courseObjects;
    private HoleGraphicDescription holeDescription;

    public HoleNameRenderer() {
	courseObjects = new ArrayList<DrawableObject>();
	holeDescription = new HoleGraphicDescription(
                new Rectangle(0, 0, 768, 1024),
                new Rectangle(255, 90, 768 - 255, 1024 - 90));

    }

    public void renderObjects(Graphics g, MapProjection p, boolean quickDraw) {
        if(courseObjects != null) {
            Graphics2D g2d = (Graphics2D) g.create();

            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    quickDraw? RenderingHints.VALUE_ANTIALIAS_OFF :
                        RenderingHints.VALUE_ANTIALIAS_ON);

	    for(DrawableObject object: courseObjects) {
                if(object instanceof DrawableHole) {
		    DrawableHole drawableHole = (DrawableHole) object;
		    renderHoleNumber(drawableHole, g2d, p);
		}
            }

            g2d.dispose();
        }
    }

    private Shape renderHoleNumber(DrawableHole drawableHole, Graphics2D g2d, MapProjection p) {
	HoleDefinition hole = drawableHole.getHole();
	BasicMapImage holeMap = hole.map;
	if(holeMap != null) {
	    Coordinates tl = holeMap.getTopLeft();
	    Coordinates tr = holeMap.getTopRight();
	    Coordinates bl = holeMap.getBottomLeft();

	    Point2D topleft = p.project(tl);
	    Point2D topright = p.project(tr);
	    Point2D botleft = p.project(bl);

	    double pi = Math.PI;
	    double angle = Math.atan2(botleft.getY() - topleft.getY(), botleft.getX() - topleft.getX()) + pi;

	    AffineTransform transform = g2d.getTransform();

	    AffineTransform t = new AffineTransform();

	    t.concatenate(AffineTransform.
		    getTranslateInstance(topleft.getX(), topleft.getY()));
	    t.concatenate(AffineTransform.
		    getRotateInstance(angle));

	    String name = Integer.toString(hole.number);
//	    String name = hole.course.getName() + " " + hole.number;

            g2d.setFont(new Font("Droid Sans", Font.BOLD, 20));
	    int width = g2d.getFontMetrics().stringWidth(name);
	    int height = g2d.getFontMetrics().getHeight();

	    double scale = p.getScale();
	    t.concatenate(AffineTransform.getScaleInstance(scale, scale));

	    Rectangle2D rect = holeDescription.getMainRect();
	    double topScale = topleft.distance(topright) / rect.getWidth();
	    double sideScale = topleft.distance(botleft) / rect.getHeight();
	    Rectangle2D active = holeDescription.getActiveRect();
	    Point2D.Double ctr = new Point2D.Double(
		    (active.getX() + active.getWidth()/2) * topScale,
		    (active.getY() + active.getHeight()/2) * sideScale);

            Point2D point = t.transform(new Point2D.Double(
                    -ctr.getY()/scale, ctr.getX()/scale), null);;

            DrawableCourseShape approach = hole.getApproachLine();
            if (approach != null) {
                List<Coordinates> line = approach.getShapeCoordinates();
                if (line != null && line.size() == 2) {
                    Coordinates p1 = line.get(line.size() - 2);
                    Coordinates p2 = line.get(line.size() - 1);
                    point = p.project(new Coordinates(
                            (p1.latitude + p2.latitude)/2,
                            (p1.longitude + p2.longitude)/2));
                }
                else if (line != null && line.size() > 2) {
                    point = p.project(line.get(line.size() - 2));
                }
            }

	    g2d.setColor(Color.white);
	    g2d.drawString(name, (float) (point.getX() - width / 2), (float) (point.getY() + height / 2));
	}
	return null;
    }

    public void reset() {
    }

    public void setDrawableObjects(List<DrawableObject> courseObjects) {
	this.courseObjects.clear();

        if(courseObjects != null) {
            this.courseObjects.addAll(courseObjects);
        }

	this.courseObjects.trimToSize();
    }

    public List<DrawableObject> getDrawableObjects() {
        return Collections.unmodifiableList(courseObjects);
    }

}
