/*
 * 
 */
package com.stayprime.basestation2.util;

import com.aeben.elementos.mapview.DrawableMapImage;
import com.aeben.golfclub.DrawableGreen;
import com.aeben.golfclub.HoleDefinition;
import com.stayprime.geo.BasicMapImage;
import com.stayprime.geo.CoordinateCalculations;
import com.stayprime.geo.Coordinates;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.List;

/**
 *
 * @author benjamin
 */
public class CourseRenderUtil {

    public static DrawableMapImage createGreenViewReferenceMapImage(HoleDefinition selectedHole, int h) {
        if (selectedHole != null) {
            DrawableGreen green = selectedHole.green;
            if (green != null && green.front != null && green.back != null) {
                Coordinates front = green.front;
                Coordinates back = green.back;
                Coordinates center = new Coordinates((front.latitude + back.latitude) / 2, (front.longitude + back.longitude) / 2);
                Point2D z = new Point2D.Double();
                Point2D ft = CoordinateCalculations.getMercatorProjectionInMeters(front, center);
                Point2D ftNorm = new Point2D.Double(ft.getY(), -ft.getX());
                double ftLen = z.distance(ft);
                Point2D bk = CoordinateCalculations.getMercatorProjectionInMeters(back, center);
                double extraHt = 1.2F;
                double extraWt = 1.0F;
                Point2D top = new Point2D.Double(ft.getX(), ft.getY());
                Point2D bot = new Point2D.Double(bk.getX(), bk.getY());
                List<Coordinates> shape = green.shapeCoordinates;
                if (shape != null && shape.isEmpty() == false) {
                    Point2D p;
                    for (Coordinates c : shape) {
                        p = CoordinateCalculations.getMercatorProjectionInMeters(c, center);
                        double dotProd = ft.getX() * p.getX() + ft.getY() * p.getY();
                        double scalarProj = dotProd / ftLen;
                        Point2D pOnFt = new Point2D.Double(scalarProj * ft.getX() / ftLen, scalarProj * ft.getY() / ftLen);
                        double pOnFtLen = z.distance(pOnFt);
                        if (dotProd > 0 && pOnFtLen > z.distance(top)) {
                            top.setLocation(pOnFt);
                        }
                        else if (dotProd < 0 && pOnFtLen > z.distance(bot)) {
                            bot.setLocation(pOnFt);
                        }
                    }
                    double halfHeight = bot.distance(top) / 2;
                    for (Coordinates c : shape) {
                        p = CoordinateCalculations.getMercatorProjectionInMeters(c, center);
                        double dotProd = ftNorm.getX() * p.getX() + ftNorm.getY() * p.getY();
                        double scalarProj = dotProd / ftLen;
                        double extra = Math.abs(scalarProj) / halfHeight * 1.2;
                        extraWt = Math.max(extra, extraWt);
                    }
                    extraHt = Math.max(extraWt, extraHt);
                }
                Point2D nft = new Point2D.Double(top.getX() * extraHt, top.getY() * extraHt);
                Point2D nbk = new Point2D.Double(bot.getX() * extraHt, bot.getY() * extraHt);
                double totalHeight = nbk.distance(nft);
                Point2D b = new Point2D.Double(-ft.getY() / ftLen, ft.getX() / ftLen);
                double f2h = 50.0 / h * totalHeight;
                Point2D botRightM = new Point2D.Double(nft.getX() + b.getX() * f2h, nft.getY() + b.getY() * f2h);
                Point2D botLeftM = new Point2D.Double(nft.getX() - b.getX() * f2h, nft.getY() - b.getY() * f2h);
                Point2D topRightM = new Point2D.Double(nbk.getX() + b.getX() * f2h, nbk.getY() + b.getY() * f2h);
                Point2D topLeftM = new Point2D.Double(nbk.getX() - b.getX() * f2h, nbk.getY() - b.getY() * f2h);
                BufferedImage customImage = new BufferedImage(100, h, BufferedImage.TYPE_INT_RGB);
                BasicMapImage customMap = new BasicMapImage("", CoordinateCalculations.getInverseMercatorProjectionFromMeters(topLeftM, center), CoordinateCalculations.getInverseMercatorProjectionFromMeters(topRightM, center), CoordinateCalculations.getInverseMercatorProjectionFromMeters(botLeftM, center), CoordinateCalculations.getInverseMercatorProjectionFromMeters(botRightM, center));
                DrawableMapImage drawableMapImage = new DrawableMapImage(customMap);
                drawableMapImage.setCachedImage(customImage);
                return drawableMapImage;
            }
        }
        return null;
    }
    
}
