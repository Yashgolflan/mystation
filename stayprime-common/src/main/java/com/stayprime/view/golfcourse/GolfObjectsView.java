package com.stayprime.view.golfcourse;

import com.stayprime.geo.BasicMapImage;
import com.stayprime.geo.CoordConverter;
import com.stayprime.geo.Coordinates;
import com.stayprime.model.golf.Position;
import com.stayprime.view.DrawableView;
import com.stayprime.view.TransformView;
import com.stayprime.view.map.MapView;
import com.stayprime.view.objects.BasicObjectRenderer;
import com.stayprime.view.objects.PolygonObject;
import com.stayprime.view.objects.TransformPoint;
import com.stayprime.view.objects.TransformPolygon;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import org.jhotdraw.geom.Polygon2D;

/**
 * Base class to render shapes transformed golf objects.
 * Provides a GolfObjectsRenderer object to subclasses, and a reference
 * to a MapView and TransformView.
 * It also provides a list of shapes that the subclasses can use to draw.
 * @author benjamin
 */
public class GolfObjectsView implements DrawableView, CoordConverter {
//    private static final Logger log = LoggerFactory.getLogger(GolfObjectsView.class);

    protected final MapView mapView;
    protected final TransformView transformView;
    protected final GolfObjectsRenderer golfRenderer;

    private boolean validMap = false;

    //Shapes drawn on the map
    protected final List<PolygonObject> shapes;

    public GolfObjectsView(MapView mapView, TransformView transformView) {
        this.mapView = mapView;
        this.transformView = transformView;

        golfRenderer = new GolfObjectsRenderer();

        shapes = new ArrayList<PolygonObject>();
    }

    public void setFont(Font font) {
        golfRenderer.setMeasurementFont(font);
    }

    /**
     * Set the current map and make sure it matches the mapView map.
     * @param map the current MapImage to show.
     */
    protected void setMap(BasicMapImage map) {
        validMap = map != null && map == mapView.getMap() && mapView.isValidMap();
    }

    public boolean isValidMap() {
        return validMap;
    }

    /**
     * @return map scale in meter/pixel
     */
    public double getMapScale() {
        Point2D mapScale = mapView.getMapScale();
        if(mapScale != null) {
            return (mapScale.getX() + mapScale.getY()) / 2 * transformView.getScale();
        }
        else {
            return 1;
        }
    }

    /*
     * ObjectRenderer
     */

    @Override
    public boolean render(Graphics2D g2d, AffineTransform parentTransform, boolean quick) {
        return renderShapes(g2d, parentTransform, quick);
    }

    protected boolean renderShapes(Graphics2D g2d, AffineTransform parentTransform, boolean quick) {
        for (PolygonObject shape: shapes) {
            BasicObjectRenderer.renderPolygon(g2d, shape, GolfObjectsRenderer.basicStroke);
        }

        return false;
    }

    /*
     * Implement CoordConverter
     */

    @Override
    public Point2D toPoint(Coordinates pinLocation) {
        if (mapView.isValidMap() == false) {
            return null;
        }
        Point2D point = mapView.toPoint(pinLocation);
        transformView.transform(point);
        return point;
    }

    @Override
    public Coordinates toLatLong(Point2D p) {
        if (mapView.isValidMap() == false) {
            return null;
        }
        Point2D point = new Point2D.Double(p.getX(), p.getY());
        transformView.inverseTransform(point);
        return mapView.toLatLong(point);
    }

    /*
     * Utility methods to create TransformPoints and TransformPolygons
     */

    protected Position transPoint(Coordinates coord) {
        return transPoint(coord.latitude, coord.longitude);
    }

    protected Position transPoint(double lat, double lon) {
        if (mapView.isValidMap() == false) {
            return null;
        }
        return new TransformPoint(transformView, mapView.toPoint(lat, lon));
    }

    protected Polygon2D transPolygon(List<Coordinates> shape) {
        if (mapView.isValidMap() == false) {
            return null;
        }
        double[] xpoints = new double[shape.size()];
        double[] ypoints = new double[shape.size()];
        for(int i = 0; i < shape.size(); i++) {
            Point2D p = mapView.toPoint(shape.get(i));
            xpoints[i] = p.getX();
            ypoints[i] = p.getY();
        }

        return new TransformPolygon(transformView, new Polygon2D.Double(xpoints, ypoints, shape.size()));
    }

    protected void addAreaPolygon(List<Coordinates> shape, Color fill, Color stroke) {
        if (shape != null) {
            Polygon2D poly = transPolygon(shape);
            PolygonObject polyObject = new PolygonObject(poly, fill, stroke);
            shapes.add(polyObject);
        }
    }

    public static double[][] projectPolygon(List<Coordinates> shape, CoordConverter converter) {
        double[] xpoints = new double[shape.size()];
        double[] ypoints = new double[shape.size()];
        for(int i = 0; i < shape.size(); i++) {
            Point2D p = converter.toPoint(shape.get(i));
            xpoints[i] = p.getX();
            ypoints[i] = p.getY();
        }

        return new double[][] {xpoints, ypoints};
    }

}
