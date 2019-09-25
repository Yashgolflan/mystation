/*
 *
 */
package com.stayprime.golf.course;

import com.stayprime.geo.Coordinates;
import com.stayprime.golf.course.objects.CartPath;
import com.stayprime.golf.course.objects.GreenImpl;
import com.stayprime.golf.course.objects.HoleZone;
import com.stayprime.golf.course.objects.ObjectType;
import com.stayprime.golf.course.objects.WarningArea;
import com.stayprime.golf.objects.AbstractFeature;
import com.stayprime.golf.objects.GeomType;
import com.stayprime.oncourseads.AdZone;
import com.stayprime.util.gson.Exclude;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.collections.CollectionUtils;

/**
 * Represent hole features from a list of <code>AbstractFeature</code> objects.
 * It takes the AbstractFeature from the database and converts it into hole
 * features.
 * @author benjamin
 */
public class GolfHoleFeatures {

    @Exclude
    private GolfHole hole;

    private AbstractFeature holeOutline;

    private AbstractFeature approachLine;

    private Coordinates pinLocation;

    private GreenImpl green;

    private List<CartPath> cartPaths;

    private List<HoleZone> teeZones;

    private List<AdZone> adZones;

    private List<AbstractFeature> hazards;

    private List<WarningArea> warningZones;

    private List<AbstractFeature> extraFeatures;

    public GolfHoleFeatures(GolfHole hole) {
        this.hole = hole;
        cartPaths = new ArrayList<CartPath>();
        teeZones = new ArrayList<HoleZone>();
        adZones = new ArrayList<AdZone>();
        hazards = new ArrayList<AbstractFeature>();
        warningZones = new ArrayList<WarningArea>();
        extraFeatures = new ArrayList<AbstractFeature>();
    }

    public void setFeatures(List<AbstractFeature> featuresList) {
        extraFeatures.clear();

        for (AbstractFeature f: featuresList) {
            switch (f.getType()) {
                case HOLE_OUTLINE: setHoleOutline(f);
                    break;
                case APPROACH_LINE: setApproachLine(f);
                    break;
                case PINFLAG: setPinLocation(f.getLocation());
                    break;
                case GREEN: setGreen(new GreenImpl(f, hole));
                    break;
                case CARTH_PATH: cartPaths.add(new CartPath(f));
                    break;
                case HOLE_ZONE: teeZones.add(new HoleZone(f, hole));
                    break;
                case AD_ZONE: adZones.add(new AdZone(f));
                    break;
                case HAZARD: hazards.add(f);
                    break;
                case RESTRICTED_ZONE:
                case ACTION_ZONE:
                    warningZones.add(new WarningArea(f));
                    break;
                default:
                    extraFeatures.add(f);
            }
        }
    }

    public List<AbstractFeature> getFeatures() {
        ArrayList<AbstractFeature> features = new ArrayList<AbstractFeature>();
        CollectionUtils.addIgnoreNull(features, holeOutline);
        CollectionUtils.addIgnoreNull(features, approachLine);
        CollectionUtils.addIgnoreNull(features, green);
        CollectionUtils.addIgnoreNull(features, getPointFeature(ObjectType.PINFLAG, pinLocation, "Pin"));
        features.addAll(cartPaths);
        features.addAll(teeZones);
        features.addAll(adZones);
        features.addAll(hazards);
        features.addAll(warningZones);
        features.addAll(extraFeatures);
        return features;
    }

    public static AbstractFeature getPointFeature(ObjectType type, Coordinates point, String name) {
        if (point != null) {
            AbstractFeature feature = new AbstractFeature(type, GeomType.POINT, name);
            feature.setLocation(point);
            return feature;
        }
        else {
            return null;
        }
    }

    public AbstractFeature getHoleOutline() {
        return holeOutline;
    }

    public void setHoleOutline(AbstractFeature holeOutline) {
        this.holeOutline = holeOutline;
    }

    public AbstractFeature getApproachLine() {
        return approachLine;
    }

    public void setApproachLine(AbstractFeature approachLine) {
        this.approachLine = approachLine;
    }

    public Coordinates getPinLocation() {
        return pinLocation;
    }

    public void setPinLocation(Coordinates pinLocation) {
        this.pinLocation = pinLocation;
    }

    public GreenImpl getGreen() {
        return green;
    }

    public void setGreen(GreenImpl green) {
        this.green = green;
    }

    public List<CartPath> getCartPaths() {
        return cartPaths;
    }

    public void setCartPaths(List<CartPath> cartPaths) {
        this.cartPaths = cartPaths;
    }

    public List<HoleZone> getHoleZones() {
        return teeZones;
    }

    public void setHoleZones(List<HoleZone> holeZones) {
        this.teeZones = holeZones;
    }

    public List<AdZone> getAdZones() {
        return adZones;
    }

    public void setAdZones(List<AdZone> adZones) {
        this.adZones = adZones;
    }

    public List<AbstractFeature> getHazards() {
        return hazards;
    }

    public void setHazards(List<AbstractFeature> hazards) {
        this.hazards = hazards;
    }

    public List<WarningArea> getWarningZones() {
        return warningZones;
    }

    public void setWarningZones(List<WarningArea> warningZones) {
        this.warningZones = warningZones;
    }

    public List<AbstractFeature> getExtraFeatures() {
        return extraFeatures;
    }

}
