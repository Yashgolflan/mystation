/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.aeben.golfcourse.objects;

import com.aeben.golfcourse.elements.CompositeCourseElement;
import com.aeben.golfcourse.elements.ObjectType;
import com.aeben.golfcourse.util.DistanceUnits;
import com.stayprime.geo.BasicMapImage;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author benjamin
 */
public class GolfClubImpl extends CompositeCourseElement implements GolfClub {
    private int siteId;
    private String logoImage;
    private BasicMapImage mapImage;
    private String contactInfo;
    private DistanceUnits defaultUnits = DistanceUnits.Yards;
    private boolean unitsSelectable = false;
    private String timeZone;
    private Date lastUpdated;
    private Date pinLocationUpdated;
    private List<Course> courses;
    private String welcomeImage;
    private String thankyouImage;

    public GolfClubImpl(Integer id, String name) {
	super(null, id, name);
        courses = new ArrayList<Course>();
    }

    /*
     * CourseElement
     */
    public ObjectType getType() {
	return ObjectTypes.GOLF_CLUB;
    }

    /*
     * Setters
     */
    public void setContactInfo(String contactInfo) {
	this.contactInfo = contactInfo;
    }

    public void setCourses(List<CourseImpl> courses) {
	this.courses.clear();

	for(Course course: courses) {
	    addCourse(course);
	}
    }

    public void addCourse(Course course) {
	if(course.getNumber() != courses.size() + 1)
	    throw new IllegalArgumentException("Course number doesn't match index");

	this.courses.add(course);
    }

    public void setLastUpdated(Date lastUpdated) {
	this.lastUpdated = lastUpdated;
    }

    public void setLogoImage(String logoImage) {
	this.logoImage = logoImage;
    }

    public void setWelcomeImage(String welcomeImage) {
	this.welcomeImage = welcomeImage;
    }

    public void setThankyouImage(String thankyouImage) {
	this.thankyouImage = thankyouImage;
    }

    public void setMapImage(BasicMapImage mapImage) {
	this.mapImage = mapImage;
    }

    public void setPinLocationUpdated(Date pinLocationUpdated) {
	this.pinLocationUpdated = pinLocationUpdated;
    }

    public void setDefaultUnits(DistanceUnits units) {
	this.defaultUnits = units;
    }

    public void setUnitsSelectable(boolean unitsSelectable) {
	this.unitsSelectable = unitsSelectable;
    }

    public void setTimeZone(String timeZone) {
	this.timeZone = timeZone;
    }
    
    /*
     * Accessors
     */
    public int getCourseCount() {
	return getCourses().size();
    }

    public Course getCourse(int number) {
	return courses.get(number - 1);
    }

    public String getWelcomeImage() {
	return welcomeImage;
    }

    public String getThankyouImage() {
	return thankyouImage;
    }

    public String getLogoImage() {
	return logoImage;
    }

    public BasicMapImage getMapImage() {
	return mapImage;
    }

    public String getContactInfo() {
	return contactInfo;
    }

    public DistanceUnits getDefaultUnits() {
	return defaultUnits;
    }

    public boolean isUnitsSelectable() {
	return unitsSelectable;
    }

    public String getTimeZone() {
	return timeZone;
    }

    public Date getLastUpdated() {
	return lastUpdated;
    }

    public Date getPinLocationUpdated() {
	return pinLocationUpdated;
    }

    public List<Course> getCourses() {
	return courses;
    }

//    public List<CourseElement> getCourseObjects() {
//	return courseObjects;
//    }

    public int getSiteId() {
        return siteId;
    }
    
    public void setSiteId(int siteId){
        this.siteId = siteId;
    }

}
