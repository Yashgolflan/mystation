/*
 * 
 */
package com.stayprime.golf.course;

import com.stayprime.geo.BasicMapImage;
import com.stayprime.golf.course.objects.ClubhouseZone;
import com.stayprime.golf.course.objects.WarningArea;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.persistence.*;
import org.apache.commons.collections.CollectionUtils;

/**
 *
 * @author benjamin
 */
//@Entity
public class Site {
    public static final String thankyouImage = "thankyouImage";
    public static final String units = "units";
    public static final String timeZone = "timeZone";
    public static final String welcomeImage = "welcomeImage";

    @Id @GeneratedValue
    private Integer siteId;

    private String type;

    private String name;

    private String logo;

    private String details;

    @Embedded
    private BasicMapImage mapImage;

    private String version;

    private String pinsVersion;

    @OneToMany(mappedBy = "site", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("index")
    private List<GolfCourse> courses;

    @Transient
    private PinLocations pinLocations;

    @Transient
    private List<ClubhouseZone> clubhouseZones;

    @Transient
    private List<WarningArea> warningZones;
    @Transient
    private Properties siteConfig;

    public Site() {
        this(null);
    }

    public Site(String name) {
	this.name = name;
        courses = new ArrayList<GolfCourse>();
        pinLocations = new PinLocations();
        clubhouseZones = new ArrayList<ClubhouseZone>();
        warningZones = new ArrayList<WarningArea>();
        siteConfig = new Properties();
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public BasicMapImage getMapImage() {
        return mapImage;
    }

    public void setMapImage(BasicMapImage map) {
        this.mapImage = map;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<GolfCourse> getCourses() {
        return courses;
    }

    public void setCourses(List<GolfCourse> courses) {
        this.courses = courses;
    }

    public GolfCourse getCourse(int n) {
	if(courses == null) {
	    return null;
        }
        else {
	    return courses.get(n - 1);
        }
    }

    public void addCourse(GolfCourse course) {
        addCourse(courses.size(), course);
    }

    public void addCourse(int i, GolfCourse course) {
        course.setSite(this);
        courses.add(i, course);
    }

    public void removeCourse(int n) {
        GolfCourse course = courses.remove(n - 1);
        course.setSite(null);
    }

    public int getCourseCount() {
        return courses.size();
    }

    public PinLocations getPinLocations() {
        return pinLocations;
    }

    public String getPinsVersion() {
        return pinsVersion;
    }

    public void setPinsVersion(String pinsVersion) {
        this.pinsVersion = pinsVersion;
    }

    public void setPinLocations(PinLocations pinLocations) {
        this.pinLocations = pinLocations;
    }

    public List<ClubhouseZone> getClubhouseZones() {
        return clubhouseZones;
    }

    public void setClubhouseZones(List<ClubhouseZone> clubhouseZones) {
        this.clubhouseZones = clubhouseZones;
    }

    public List<WarningArea> getWarningZones() {
        return warningZones;
    }

    public void setWarningZones(List<WarningArea> warningZones) {
        this.warningZones = warningZones;
    }

    public GolfHole getFirstHole() {
        return getFirstHole(1);
    }

    public GolfHole getFirstHole(int courseIndex) {
        if (courses.size() >= courseIndex) {
            return getCourse(courseIndex).getHole(1);
        }
        else {
            return null;
        }
    }

    public GolfHole getAbsoluteHoleNumber(int hole) {
        if (CollectionUtils.isNotEmpty(courses)) {
            for (GolfCourse c: courses) {
                if (hole >= c.getFirstHoleIndex()
                        && hole < c.getFirstHoleIndex() + c.getHoleCount()) {
                    return c.getHole(hole - c.getFirstHoleIndex() + 1);
                }
            }
        }

        return null;
    }

    public int getTotalHoleCount() {
        int total = 0;
        if (CollectionUtils.isNotEmpty(courses)) {
            for (GolfCourse c: courses) {
                total += c.getHoleCount();
            }
        }
        return total;
    }

    public Properties getSiteConfig() {
        return siteConfig;
    }

    public static void setParents(Site s) {
        for(GolfCourse golfCourse : s.getCourses()){
            golfCourse.setSite(s);
            golfCourse.setParents();
        }
    }
}
