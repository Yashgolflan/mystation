/*
 * 
 */
package com.stayprime.geo;

import com.stayprime.geo.CoordinateCalculations;
import com.stayprime.geo.Coordinates;
import com.stayprime.util.MathUtil;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

/**
 *
 * @author benjamin
 */
public class MapUtils {

    public static boolean hasFourCornerCoordinates(BasicMapImage map) {
        return map.getTopLeft() != null
                && map.getTopRight() != null
                && map.getBottomLeft() != null
                && map.getBottomRight() != null;
    }

    public static BufferedImage getNormalizedMapImage(BasicMapImage mapImage, BufferedImage image) {
        if (hasFourCornerCoordinates(mapImage)) {
            Coordinates center = new Coordinates(
                    (mapImage.getTopLeft().latitude + mapImage.getBottomRight().latitude) / 2,
                    (mapImage.getTopLeft().longitude + mapImage.getBottomRight().longitude) / 2);

            Point2D tl = CoordinateCalculations.getMercatorProjectionInMeters(mapImage.getTopLeft(), center);
            Point2D tr = CoordinateCalculations.getMercatorProjectionInMeters(mapImage.getTopRight(), center);
            Point2D bl = CoordinateCalculations.getMercatorProjectionInMeters(mapImage.getBottomLeft(), center);
            double width = tl.distance(tr);
            double height = tl.distance(bl);

            double w2h1 = width / height;
            int w = image.getWidth();
            int h = image.getHeight();
            double nw = h * w2h1;

            BufferedImage before = image;
            if (before.getType() == BufferedImage.TYPE_4BYTE_ABGR) {
                before = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
                before.getGraphics().drawImage(image, 0, 0, null);
            }

            BufferedImage after = new BufferedImage(Math.round((float) nw), h, BufferedImage.TYPE_INT_ARGB);
            AffineTransform at = new AffineTransform();
            at.scale(nw / w, 1.0);
            AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);

            scaleOp.filter(before, after);

            return after;
        }

        return image;
    }

    public static void normalizeMapImage(BasicMapImage mapImage) {
        if (hasFourCornerCoordinates(mapImage)) {
            Coordinates center = new Coordinates(
                    (mapImage.getTopLeft().latitude + mapImage.getBottomRight().latitude) / 2,
                    (mapImage.getTopLeft().longitude + mapImage.getBottomRight().longitude) / 2);

            Point2D tl = CoordinateCalculations.getMercatorProjectionInMeters(mapImage.getTopLeft(), center);
            Point2D tr = CoordinateCalculations.getMercatorProjectionInMeters(mapImage.getTopRight(), center);
            Point2D br = CoordinateCalculations.getMercatorProjectionInMeters(mapImage.getBottomRight(), center);
            Point2D ctr = new Point2D.Double((tl.getX() + br.getX()) / 2, (tl.getY() + br.getY()) / 2);
            double centerToCornerDist = ctr.distance(tl);

            //Unit vector from center to topRight
            Point2D tru = MathUtil.getUnitVector(ctr, tr);

            //Normalized topRight vector
            Point2D.Double trnv = MathUtil.scale(tru, centerToCornerDist);
            Point2D.Double trn = MathUtil.add(ctr, trnv);
            Point2D.Double bln = MathUtil.sub(ctr, trnv);

            Coordinates normalizedTopRight = CoordinateCalculations.getInverseMercatorProjectionFromMeters(trn, center);
            mapImage.setTopRight(normalizedTopRight);

            Coordinates normalizedBottomLeft = CoordinateCalculations.getInverseMercatorProjectionFromMeters(bln, center);
            mapImage.setBottomLeft(normalizedBottomLeft);
        }
        else
            throw new RuntimeException("MapImage doesn't have four corner coordinates.");
    }
}
