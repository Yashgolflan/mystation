/*
 * 
 */
package com.stayprime.basestation2.util;

import com.aeben.elementos.mapview.DrawableObject;
import com.aeben.golfclub.HoleDefinition;
import com.aeben.golfclub.view.Dashboard;
import com.aeben.golfcourse.util.DistanceUnits;
import com.stayprime.basestation2.renderers.DistanceRenderer;
import com.stayprime.basestation2.renderers.DrawableDistance;
import com.stayprime.basestation2.ui.modules.DrawingToolCalculations;
import com.stayprime.geo.CoordinateCalculations;
import com.stayprime.geo.Coordinates;
import com.stayprime.util.geometry.UnitVector;
import java.awt.Font;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author benjamin
 */
public class PinLocationUtil {
    public static List<DrawableObject> createPinMeasurements(Point point,
            HoleDefinition hole, List<Point2D> greenShape, Dashboard converter,
            Font font, DistanceUnits units) {

        List<DrawableObject> distances = new ArrayList<DrawableObject>();
        Point2D mapPoint = converter.convertViewPointToMap(point);
        Coordinates coord = converter.convertMapPointToCoordinates(mapPoint);

        DistanceRenderer.DistancePosition END_POINT = DistanceRenderer.DistancePosition.END_POINT;

        Coordinates front = hole.green.front;
        Coordinates back = hole.green.back;

        if (front != null && back != null && CoordinateCalculations.shapeContains(greenShape, point)) {
            Point2D z = new Point2D.Double();
            Point2D bk = CoordinateCalculations.getMercatorProjectionInMeters(back, front);
            double bkLen = z.distance(bk);
            Point2D p = CoordinateCalculations.getMercatorProjectionInMeters(coord, front);

            double dotP = bk.getX()*p.getX() + bk.getY()*p.getY();
            double scalarProj = dotP/bkLen;
            Point2D pOnBk = new Point2D.Double(scalarProj*bk.getX()/bkLen, scalarProj*bk.getY()/bkLen);

            Coordinates vertical = CoordinateCalculations.getInverseMercatorProjectionFromMeters(pOnBk, front);
            distances.add(new DrawableDistance(vertical, front, font, units, END_POINT));
            distances.add(new DrawableDistance(vertical, back, font, units, END_POINT));

            Point2D.Double result = new Point2D.Double();

            Point2D v = converter.convertMapPointToView(converter.convertCoordinatesToMapPoint(vertical));
            double angle = Math.atan2(v.getY() - point.getY(), v.getX() - point.getX());
            UnitVector uv = new UnitVector(point, angle);
            if(DrawingToolCalculations.findIntersectionDirectional(uv, greenShape, result) >= 0) {
                Coordinates rp = converter.convertMapPointToCoordinates(converter.convertViewPointToMap(result));
                distances.add(new DrawableDistance(coord, rp, font, units, END_POINT));
            }

            uv = uv.invert();
            if(DrawingToolCalculations.findIntersectionDirectional(uv, greenShape, result) >= 0) {
                Coordinates lp = converter.convertMapPointToCoordinates(converter.convertViewPointToMap(result));
                distances.add(new DrawableDistance(coord, lp, font, units, END_POINT));
            }
        }
//        else if (front != null) {
//            distances.add(new DrawableDistance(coord, front, font, units, END_POINT));
//        }
//        else if (back != null) {
//            distances.add(new DrawableDistance(coord, back, font, units, END_POINT));
//        }

        return distances;
    }

}
