/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.aeben.golfclub;

import com.aeben.golfcourse.util.DistanceUnits;
import com.stayprime.geo.BasicMapImage;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author benjamin
 */
public class GolfClub extends GolfCourseObject implements Cloneable {
    private String siteId;
    private String name;
    private String logoImage;
    private BasicMapImage courseImage;

    private String courseDescription = "";
    private String contactInfo = "";

    private int units = DistanceUnits.Yards.key;
    private boolean unitsSelectable = false;

    private String version;
    private Date pinLocationUpdated;

    private List<CourseDefinition> courses;
    private List<GolfCourseObject> courseObjects;
    private static final GolfCourseObject.ObjectType type = GolfCourseObject.ObjectType.GOLF_CLUB;

    public static final String defaultCartImage = "com/aeben/cartunit/resources/golfcart.png";
    public static final String defaultFlagImage = "com/aeben/cartunit/resources/flag.png";
    private String welcomeImage;
    private String thankyouImage;

    public GolfClub(String name) {
        this.name = name;
        courses = new ArrayList<CourseDefinition>();
        courseObjects = new ArrayList<GolfCourseObject>();
    }

    public String getSiteId(){
        return siteId;
    }
 
    public void setSiteId (String siteId){
        this.siteId = siteId;
    }
    public CourseDefinition getCourseNumber(int number) {
        //TODO: improve efficiency;
        for(CourseDefinition course: courses) {
            if(course.getId() != null && course.getId() == number)
                return course;
        }
        return null;
    }

    public HoleDefinition getAbsoluteHoleNumber(int holeNumber) {
	int countedHoles = 0;
	
	if(holeNumber > 0) {
	    for(CourseDefinition course: courses) {
		if(countedHoles + course.getHoleCount() >= holeNumber)
		    return course.getHoleNumber(holeNumber - countedHoles);
		else
		    countedHoles += course.getHoleCount();
	    }
	}

	return null;
    }
    
    public String getWelcomeImage() {
        return welcomeImage;
    }

    public void setWelcomeImage(String welcomeImage) {
        this.welcomeImage = welcomeImage;
    }

    public String getThankyouImage() {
        return thankyouImage;
    }

    public void setThankyouImage(String thankyouImage) {
        this.thankyouImage = thankyouImage;
    }
    
    @Override
    public String getName() {
        return name;
    }

    @Override
    public Integer getId() {
        return null;
    }

    @Override
    public GolfCourseObject getParentObject() {
        return null;
    }

    @Override
    public ObjectType getType() {
        return type;
    }

    @Override
    public GolfClub clone() {
        GolfClub golfClub = new GolfClub(name);
        golfClub.logoImage = logoImage;
        golfClub.courseImage = courseImage;
        golfClub.welcomeImage = welcomeImage;
        golfClub.thankyouImage = thankyouImage;
        golfClub.courseDescription = courseDescription;
        golfClub.contactInfo = contactInfo;
        golfClub.units = units;
        golfClub.unitsSelectable = unitsSelectable;
        golfClub.version = version;
        golfClub.courses = new ArrayList<CourseDefinition>(courses);
        golfClub.courseObjects = new ArrayList<GolfCourseObject>(courseObjects);

        return golfClub;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogoImage() {
        return logoImage;
    }

    public void setLogoImage(String logoImage) {
        this.logoImage = logoImage;
    }

    public BasicMapImage getCourseImage() {
        return courseImage;
    }

    public void setCourseImage(BasicMapImage courseImage) {
        this.courseImage = courseImage;
    }

    public String getCourseDescription() {
        return courseDescription;
    }

    public void setCourseDescription(String courseDescription) {
        this.courseDescription = courseDescription;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    public DistanceUnits getUnits() {
        return DistanceUnits.get(units);
    }

    public void setUnits(DistanceUnits units) {
        this.units = units.key;
    }

    public boolean isUnitsSelectable() {
        return unitsSelectable;
    }

    public void setUnitsSelectable(boolean unitsSelectable) {
        this.unitsSelectable = unitsSelectable;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Date getPinLocationUpdated() {
        return pinLocationUpdated;
    }

    public void setPinLocationUpdated(Date pinLocationUpdated) {
        this.pinLocationUpdated = pinLocationUpdated;
    }

    public List<CourseDefinition> getCourses() {
        return courses;
    }

    public void setCourses(List<CourseDefinition> courses) {
        this.courses = courses;
    }

    public void removeCourse(int coursenumber) {
        CourseDefinition course = getCourseNumber(coursenumber);

        if(course != null) {
            courses.remove(course);
        }
    }
    public List<GolfCourseObject> getCourseObjects() {
        return courseObjects;
    }

    public void setCourseObjects(List<GolfCourseObject> courseObjects) {
        this.courseObjects = courseObjects;
    }

    public int getTotalHoleCount() {
        int count = 0;
        if (courses != null) {
            for (CourseDefinition course: courses) {
                count += course.getHoleCount();
            }
        }
        return count;
    }

    public void setParents() {
        for (CourseDefinition c : courses) {
            c.setGolfClub(this);
            c.setParents();
        }
    }

    //public static final int YARDS = 0, METERS = 1;
    public enum Units {
        YARDS(0, "Yards", "yd", 0.9144f),
        METERS(1, "Meters", "m", 1f),
        FEET(2, "Feet", "ft", 0.3048f);
        public final int key;
        public final float conversionToMeters;
        public final String suffix;

        private Units(int key, String name, String suffix, float conversionFromMeters) {
            this.key = key;
            this.conversionToMeters = conversionFromMeters;
            this.suffix = suffix;
        }

        public static Units get(int key) {
            if(key == YARDS.key)
                return YARDS;
            else if(key == METERS.key)
                return METERS;
            else
                throw new IllegalArgumentException("Invalid Units key");
        }

        public float convertFromMeters(float m) {
            return m/conversionToMeters;
        }

        public float convertToMeters(float m) {
            return m*conversionToMeters;
        }

        public double convertFromMeters(double m) {
            return m/conversionToMeters;
        }

        public double convertToMeters(double m) {
            return m*conversionToMeters;
        }
    };

}
