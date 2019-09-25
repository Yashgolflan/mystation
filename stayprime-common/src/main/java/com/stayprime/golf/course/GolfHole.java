/*
 * 
 */
package com.stayprime.golf.course;

import com.stayprime.geo.BasicMapImage;
import com.stayprime.geo.Coordinates;
import com.stayprime.golf.objects.AbstractFeature;
import com.stayprime.util.gson.Exclude;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;

/**
 *
 * @author benjamin
 */
//@Entity
public class GolfHole {
    @Exclude
    @ManyToOne
    @JoinColumn(name = "siteId")
    private Site site;

    @Exclude
    @ManyToOne
    @JoinColumn(name = "courseId")
    private GolfCourse golfCourse;

    @Id @GeneratedValue
    private Integer holeId;

    // hole_index is mapped as AccessType.PROPERTY

    private int number;

    private int par;

    private int strokeIndex;

    private int holePace;

    private String details;

    private String proTips;

    private boolean pathOnly;

    @Embedded
    private BasicMapImage mapImage;

    private String flyoverVideo;

    private String updated;

    @Transient
    private List<HoleTeeBox> teeBoxes;

    @Transient
    private GolfHoleFeatures features;

    public GolfHole() {
        this(null, 0);
    }

    public GolfHole(GolfCourse course, int number) {
	this.golfCourse = course;
        setNumber(number);

        teeBoxes = new ArrayList<HoleTeeBox>();
        features = new GolfHoleFeatures(this);
    }

    public GolfCourse getGolfCourse() {
        return golfCourse;
    }

    public void setGolfCourse(GolfCourse golfCourse) {
        this.golfCourse = golfCourse;
    }

    public Integer getHoleId() {
        return holeId;
    }

    public void setHoleId(Integer holeId) {
        this.holeId = holeId;
    }

    @Access(AccessType.PROPERTY)
    public int getHoleIndex() {
        if (golfCourse != null) {
            return golfCourse.getFirstHoleIndex() + number - 1;
        }
        else {
            return 0;
        }
    }

    public void setHoleIndex(int hole_index) {
    }

    public int getNumber() {
        return number;
    }

    public final void setNumber(int number) {
        this.number = number;
    }

    public int getPar() {
        return par;
    }

    public void setPar(int par) {
        this.par = par;
    }

    public int getStrokeIndex() {
        return strokeIndex;
    }

    public void setStrokeIndex(int strokeIndex) {
        this.strokeIndex = strokeIndex;
    }

    public int getHolePace() {
        return holePace;
    }

    public void setHolePace(int hole_pace) {
        this.holePace = hole_pace;
    }

    public boolean isCartPathOnly() {
        return pathOnly;
    }

    public void setCartPathOnly(boolean cartPathOnly) {
        this.pathOnly = cartPathOnly;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getProTips() {
        return proTips;
    }

    public void setProTips(String proTips) {
        this.proTips = proTips;
    }

    public BasicMapImage getMapImage() {
        return mapImage;
    }

    public void setMapImage(BasicMapImage map) {
        this.mapImage = map;
    }

    public String getFlyoverVideo() {
        return flyoverVideo;
    }

    public void setFlyoverVideo(String flyoverVideo) {
        this.flyoverVideo = flyoverVideo;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public Coordinates getPinLocation() {
        return features.getPinLocation();
    }

    public void setPinLocation(Coordinates pinLocation) {
        features.setPinLocation(pinLocation);
    }

    @Access(AccessType.PROPERTY)
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name="golf_hole_teebox",joinColumns=@JoinColumn(name="hole_id"))
    @OrderBy("number")
    public List<HoleTeeBox> getTeeBoxes() {
        return teeBoxes;
    }

    public void setTeeBoxes(List<HoleTeeBox> teeBoxes) {
        this.teeBoxes = teeBoxes;
    }

    @Access(AccessType.PROPERTY)
    @OneToMany(mappedBy = "golfHole", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
//    @ElementCollection(fetch = FetchType.EAGER)
//    @CollectionTable(name="golf_hole_feature",joinColumns=@JoinColumn(name="hole_id"))
    public List<AbstractFeature> getFeaturesList() {
        return features.getFeatures();
    }

    public void setFeaturesList(List<AbstractFeature> featuresList) {
        if (features == null) {
            features = new GolfHoleFeatures(this);
        }
        features.setFeatures(featuresList);
    }

    public GolfHoleFeatures getFeatures() {
        return features;
    }

    public void setFeatures(GolfHoleFeatures features) {
        this.features = features;
    }

    public void setParents() {
//        for(AbstractFeature abstractFeature: features.getFeatures()){
//            abstractFeature ;
//        }

    }
}
