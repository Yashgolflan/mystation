/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.stayprime.basestation2.renderers;

import com.stayprime.basestation2.CourseSettingsManager;
import com.aeben.elementos.mapview.BasicObjectRenderer;
import com.aeben.elementos.mapview.DrawableObject;
import com.aeben.elementos.mapview.MapProjection;
import com.stayprime.geo.Coordinates;
import com.stayprime.hibernate.entities.CartTracking;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.jhotdraw.geom.Polygon2D;

/**
 *
 * @author benjamin
 */
public class DashboardCartTrackRenderer extends BasicObjectRenderer {
//    private int paceOfPlayCautionThreshold = 0;
//    private int paceOfPlayWarningThreshold = -600;
//    private int communicationLostThreshold = 60;
//    private Color normalPaint = Color.white;
//    private Color okPaint = Color.green;
//    private Color cautionPaint = Color.orange;
//    private Color warningPaint = Color.red;
//    private Color commLostPaint = Color.gray;
    private Font font = new Font("Sans", Font.BOLD, 12);
    private Stroke stroke = new BasicStroke(2f);
    private List<List<Coordinates>> contiguousLines;
    private List<Coordinates> points;

    private CourseSettingsManager courseSettingsManager;
    private boolean drawLines;
    private Integer cartNumber;
    private List<CartTracking> track;
    private Polygon2D.Double trackPoly;

    public DashboardCartTrackRenderer() {
    }

    public DashboardCartTrackRenderer(CourseSettingsManager courseSettingsManager) {
        setCourseSettingsManager(courseSettingsManager);
    }

    @Override
    public void setDrawableObjects(List<DrawableObject> courseObjects) {
        throw new UnsupportedOperationException();
    }

    public void setCartTrack(Integer cart, List<CartTracking> track) {
        setPoints(track);

        setDrawLines(true);
        this.cartNumber = cart;

        contiguousLines = new ArrayList();
        List<Coordinates> lines = null;

        if (track != null) {
            for (int i = 0; i < track.size() - 1; i++) {
                CartTracking c1 = track.get(i);
                CartTracking c2 = track.get(i + 1);

                Date d1 = c1.getId().getTimestamp();
                Date d2 = c2.getId().getTimestamp();

                //Draw lines only when the tracking points are within 5 minutes of each other
                if (d1 != null && d2 != null && Math.abs(d1.getTime() - d2.getTime()) < 300000l) {
                    if (lines == null) {
                        lines = new ArrayList<Coordinates>();
                        contiguousLines.add(lines);
                    }
                    lines.add(new Coordinates(c1.getLatitude(), c1.getLongitude()));
                    lines.add(new Coordinates(c2.getLatitude(), c2.getLongitude()));
                }
                else {
                    lines = null;
                }
            }
        }
    }

    public void setPoints(List<CartTracking> track) {
        setDrawLines(false);
        this.cartNumber = null;
        this.track = track;
        contiguousLines = null;
        points = new ArrayList<Coordinates>();

        if (track != null) {
            for (CartTracking t : track) {
                points.add(new Coordinates(t.getLatitude(), t.getLongitude()));
            }
        }
    }

    @Override
    public void renderObjects(Graphics g, MapProjection p, boolean quickDraw) {
        trackPoly = null;
        if(track != null) {
            Graphics2D g2d = (Graphics2D) g.create();

            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    quickDraw? RenderingHints.VALUE_ANTIALIAS_OFF :
                        RenderingHints.VALUE_ANTIALIAS_ON);

            renderCartTrack(stroke, g2d, p);
	    g2d.dispose();
        }
    }

    public final void setCourseSettingsManager(CourseSettingsManager settingsManager) {
        this.courseSettingsManager = settingsManager;

    }

    public void setDrawLines(boolean b) {
        this.drawLines = b;
    }

    private void renderCartTrack(Stroke stroke, Graphics2D g2d, MapProjection p) {
        if (drawLines) {
            drawContiguousLines(stroke, g2d, p);
        }
        
        trackPoly = renderShape(points, null, Color.blue, null, false, g2d, p);
    }

    private void drawContiguousLines(Stroke stroke, Graphics2D g2d, MapProjection p) {
        for (List<Coordinates> list: contiguousLines) {
                renderShape(list, null, Color.BLUE, stroke, false, g2d, p);
        }
    }

    public int getNearestTrackPoint(Point2D p) {
        if (p != null && trackPoly != null && trackPoly.npoints > 0) {
            int n = 0;
            Point2D nearest = new Point2D.Double(trackPoly.xpoints[0], trackPoly.ypoints[0]);
            Point2D next = new Point2D.Double();
            for (int i = 1; i < trackPoly.npoints; i++) {
                next.setLocation(trackPoly.xpoints[i], trackPoly.ypoints[i]);
                if (p.distance(next) < p.distance(nearest)) {
                    n = i;
                    nearest.setLocation(next);
                }
            }
            return n;
        }

        return -1;
    }

    public List<CartTracking> getTrack() {
        return track;
    }

    public Point2D getViewPoint(int n) {
        if (trackPoly != null && n >= 0 && n < trackPoly.npoints) {
            return new Point2D.Double(trackPoly.xpoints[n], trackPoly.ypoints[n]);
        }
        return null;
    }

}