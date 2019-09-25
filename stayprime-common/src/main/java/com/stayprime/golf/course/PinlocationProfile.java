/*
 * 
 */
package com.stayprime.golf.course;

import com.stayprime.geo.Coordinates;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

/**
 *
 * @author benjamin
 */
//@Entity
public class PinlocationProfile {

    @Id @GeneratedValue
    private Integer profileId;

    @ManyToOne
    @JoinColumn(name = "siteId")
    private Site site;

    private String name;

    @Transient
    private ArrayList<Coordinates> pinLocations;

    public PinlocationProfile() {
        this(0);
    }

    public PinlocationProfile(int totalHoleCount) {
        pinLocations = new ArrayList<Coordinates>(totalHoleCount);
        setHoleCount(totalHoleCount);
    }

    public Site getSite() {
        return site;
    }

    public void setSite(Site golfSite) {
        this.site = golfSite;
    }

    public Integer getProfileId() {
        return profileId;
    }

    public void setProfileId(Integer profile_id) {
        this.profileId = profile_id;
    }

    public void setPinLocations(List<Coordinates> locations) {
        pinLocations.clear();
        pinLocations.addAll(locations);
        pinLocations.trimToSize();
    }

    public List<Coordinates> getPinLocations() {
        return pinLocations;
    }

    private void setHoleCount(int count) {
        pinLocations.ensureCapacity(count);
        while (pinLocations.size() > count) {
            pinLocations.remove(pinLocations.size() - 1);
        }
        while (pinLocations.size() < count) {
            pinLocations.add(null);
        }
        pinLocations.trimToSize();
    }

    @Column(name="geometry") @Access(AccessType.PROPERTY)
    public String getGeometry() {
	StringBuilder geom = new StringBuilder();
        if (pinLocations != null) {
            return Coordinates.listToString(pinLocations);
        }
        else {
            return null;
        }
    }

    public void setGeometry(String geom) {
        try {
            setPinLocations(Coordinates.listFromString(geom));
        }
        catch (Exception ex) {
            setPinLocations(null);
        }
    }

}
