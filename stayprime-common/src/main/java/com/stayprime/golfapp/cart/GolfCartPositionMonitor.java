/*
 *
 */
package com.stayprime.golfapp.cart;

import com.stayprime.cartapp.CartStatus;
import com.stayprime.cartapp.position.AsyncPositionListener;
import com.stayprime.geo.Coordinates;
import com.stayprime.golf.course.GolfCourse;
import com.stayprime.golf.course.GolfHole;
import com.stayprime.golf.course.Site;
import com.stayprime.golf.course.objects.CartPath;
import com.stayprime.golf.course.objects.ClubhouseZone;
import com.stayprime.golf.course.objects.HoleZone;
import com.stayprime.golf.course.objects.WarningArea;
import com.stayprime.golf.objects.AbstractFeature;
import com.stayprime.oncourseads.AdInformation;
import com.stayprime.oncourseads.AdZone;
import com.stayprime.oncourseads.Contract;
import com.stayprime.util.MathUtil;
import com.stayprime.util.geometry.Xy;

import java.util.List;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.stayprime.cartapp.CartAppStatus;

/**
 *
 * @author benjamin
 */
public class GolfCartPositionMonitor implements AsyncPositionListener {
    private static final Logger log = LoggerFactory.getLogger(GolfCartPositionMonitor.class);

    private final CourseAreaVisitor courseAreaVisitor;

    private CartAppStatus cartStatus;
    private Site golfClub;
    private List<AdInformation> holeAdsList;

    //Counter for how many fixes the cart has been stopped.
    private int stopCount = 0;
    private GolfHole currentHole;

    public GolfCartPositionMonitor() {
        courseAreaVisitor = new CourseAreaVisitor();
    }

    public void setCartStatus(CartAppStatus cartStatus) {
        this.cartStatus = cartStatus;
    }

    public void setGolfClub(Site golfClub) {
        log.debug(golfClub == null ? "setGolfClub(null)" : "setGolfClub(not null)");
        synchronized(courseAreaVisitor) {
            this.golfClub = golfClub;
        }
    }

    public void setHoleAdsList(List<AdInformation> holeAdsList) {
        this.holeAdsList = holeAdsList;
    }

    public CourseAreaVisitor getCourseAreaVisitor() {
        return courseAreaVisitor;
    }

    public int getStopCount() {
        return stopCount;
    }

    /**
     * Implements AsyncPositionListener. If the position is valid,
     * checks the visited active areas of the golf club, using a
     * CourseAreaVisitor object. Then it runs an EDT runnable and sets the
     * position EDT copy, the cart status flags, the HoleSwitcher flags,
     * warnings and cartKillAction. It also processes any cart ahead
     * messages in the EDT (they are only needed for drawing).
     *
     * @param position
     * @param valid
     * @param good
     * @param speed
     * @param heading
     */
    @Override
    public void positionUpdated(Coordinates position, boolean valid, boolean good, float speed, float heading) {
        if (valid) {
//            isCartPathOnlyHole = false;
//            isInCartPathArea = false;
//            isOnCartPath = false;

            if (speed < 1f) {
                if (stopCount < Integer.MAX_VALUE)
                    stopCount++;
            }
            else {
                stopCount = 0;
            }

            traverseCourseAreas(position);
        }
        else {
            stopCount = 0;
        }
        //The changes in the CartStatus and GameStatus go to the EDT,
        //make the changes from a common point here
        //edtPositionUpdater.setHole(playingHole);
    }

    private void traverseCourseAreas(Coordinates cartLocation) {
        synchronized (courseAreaVisitor) {
            if (golfClub != null) {
                courseAreaVisitor.start(cartLocation);

                //XMLGolfClubLoader loads ALL restricted zones directly to the
                //golfClub object, so here we are checking for all restricted zones
                visitCourseAreas(golfClub);

                traverseAllHoles(cartLocation);

                checkAdZones();

                courseAreaVisitor.end();

                VisitedArea<WarningArea> warning = courseAreaVisitor.getVisitedWarningZone();
                cartStatus.setWarningZone(warning == null? null : warning.getArea());

                VisitedArea<WarningArea> restricted = courseAreaVisitor.getVisitedRestrictedZone();
                cartStatus.setRestrictedZone(restricted == null? null : restricted.getArea());
            }
        }
    }

    private void traverseAllHoles(Coordinates cartLocation) {
        boolean isInAnyCartPathArea = false;
        boolean isOnAnyCartPath = false;
        boolean isInAnyCartPathOnlyHole = false;
        double distanceToAnyCartPath = Double.MAX_VALUE;

        for (GolfCourse course : golfClub.getCourses()) {
            //visitCourseAreas(course.getFeatures());

            for (int n = 1; n <= course.getHoleCount(); n++) {
                GolfHole holeN = course.getHole(n);
                visitHoleZones(holeN);

                AbstractFeature outline = holeN.getFeatures().getHoleOutline();
                List<CartPath> paths = holeN.getFeatures().getCartPaths();

                for (CartPath path: paths) {
                    if (outline != null && CollectionUtils.isNotEmpty(outline.getShape())
                            && path != null && CollectionUtils.isNotEmpty(path.getShape())) {

                        if (outline.contains(cartLocation)) {
                            isInAnyCartPathArea = true;
                            isInAnyCartPathOnlyHole = holeN.isCartPathOnly();

                            double distanceToCurrentCartPath =
                                    getDistanceToCartPath(cartLocation, path);

                            distanceToAnyCartPath = Math.min(
                                    distanceToCurrentCartPath,
                                    distanceToAnyCartPath);

                            double cartPathDistance = cartStatus.getCartPathWarningDistance();
                            isOnAnyCartPath = distanceToCurrentCartPath < cartPathDistance;
                        }
                    }
                }
            }
        }

        cartStatus.setInCartPathArea(isInAnyCartPathArea);
        cartStatus.setCartPathOnlyHole(isInAnyCartPathOnlyHole);
        cartStatus.setDistanceToCartPath(distanceToAnyCartPath);
        cartStatus.setOnCartPath(isOnAnyCartPath);

        log.trace("Cart path only: " + isInAnyCartPathArea + ", on path: " + isOnAnyCartPath);
    }

    private void checkAdZones() {
        //TODO this is wrong here, not running on EDT?
        //final Hole playingHole = gameStatus.getPlayingHole();
        //Basically just check for the Ad Zones

        if (currentHole != null) {
            visitAdZones(currentHole.getFeatures().getAdZones());
        }

        //Old ad system
        if (currentHole != null && holeAdsList != null) {
            for (AdInformation adInfo : holeAdsList) {
                if (adInfo.ad.contracts != null) {
                    for (Contract contract : adInfo.ad.contracts) {
                        visitAdZones(contract.getAdZones());
                    }
                }
            }
        }
    }

    private void visitCourseAreas(Site site) {
        for (ClubhouseZone e : site.getClubhouseZones()) {
            courseAreaVisitor.visitCourseElement(e);
        }
        for (WarningArea e : site.getWarningZones()) {
            courseAreaVisitor.visitCourseElement(e);
        }
    }

    public void visitHoleZones(GolfHole hole) {
        if (hole != null) {
            for (HoleZone e: hole.getFeatures().getHoleZones()) {
                courseAreaVisitor.visitCourseElement(e);
            }
        }
    }

    public void visitAdZones(List<AdZone> adZones) {
        for (AdZone e: adZones) {
            courseAreaVisitor.visitCourseElement(e);
        }
    }

    private double getDistanceToCartPath(Coordinates c, CartPath cartPath) {
        List<Coordinates> shape = cartPath.getShape();
        double distance;

        if (shape.isEmpty()) {
            return 0f;
        }
        else if (shape.size() == 1) {
            distance = c.metersTo(shape.get(0));
        }
        else {
            Xy p = new Xy(c.longitude, c.latitude);
            Xy v = new Xy();
            Xy w = new Xy();
            double minDist = Double.MAX_VALUE;

            int i_min = 0;
            for (int i = 0; i < shape.size() - 1; i++) {
                Coordinates cv = shape.get(i);
                Coordinates cw = shape.get(i + 1);
                v.set(cv.longitude, cv.latitude);
                w.set(cw.longitude, cw.latitude);
                double dist = MathUtil.distToSegment2(p, v, w);
                if (dist < minDist) {
                    minDist = dist;
                    i_min = i;
                }
            }

            Coordinates cv = shape.get(i_min);
            Coordinates cw = shape.get(i_min + 1);
            v.set(cv.longitude, cv.latitude);
            w.set(cw.longitude, cw.latitude);
            Xy pl = MathUtil.projectPointOnLineSegment(p, v, w);
            distance = c.metersTo(new Coordinates(pl.getY(), pl.getX()));
        }
        return distance;
    }

}
