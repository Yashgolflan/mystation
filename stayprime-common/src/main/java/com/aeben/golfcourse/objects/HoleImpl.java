/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.aeben.golfcourse.objects;

import com.stayprime.geo.Coordinates;
import com.aeben.golfcourse.elements.BasicArea;
import com.aeben.golfcourse.elements.CompositeCourseElement;
import com.aeben.golfcourse.elements.CourseElement;
import com.aeben.golfcourse.elements.ObjectType;
import com.aeben.golfcourse.elements.Path;
import com.stayprime.geo.BasicMapImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 *
 * @author benjamin
 */
public class HoleImpl extends CompositeCourseElement implements Hole {
    private int paceOfPlay = 0;
    private int par = 0;
    private int strokeIndex;

    private BasicMapImage mapImage;
    private String flyover;
    private String proTips;
    private Green green;
    private BasicArea approachLine;
    private BasicArea holeOutline;
    private Path cartPath;
    private Coordinates pinLocation;
    private Coordinates referencePoint;
    private Course course;
    private boolean cartPathOnly;
    private List<TeeBox> teeBoxes;
    private List<HoleZone> holeZones;
    private List<HoleZone> unmodifiableHoleZones;

    private Date lastUpdated;

    public HoleImpl(Course course, Integer number) {
	super(course, number, null);
	this.course = course;

	teeBoxes = new ArrayList<TeeBox>(5);
	holeZones = new ArrayList<HoleZone>(1);
	unmodifiableHoleZones = Collections.unmodifiableList(holeZones);
    }

    public Date getLastUpdated() {
	return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
	this.lastUpdated = lastUpdated;
    }

    /*
     * Setters
     */
    public void setNumber(int number) {
	setId(number);
    }

    public void setMapImage(BasicMapImage mapImage) {
	this.mapImage = mapImage;
    }

    public void setFlyover(String flyover) {
	this.flyover = flyover;
    }

    public void setProTips(String proTips) {
	this.proTips = proTips;
    }

    public void setPaceOfPlay(int paceOfPlay) {
	this.paceOfPlay = paceOfPlay;
    }

    public void setPar(int par) {
	this.par = par;
    }

    public void setStrokeIndex(int strokeIndex) {
	this.strokeIndex = strokeIndex;
    }

    public void setPinLocation(Coordinates pinLocation) {
	this.pinLocation = pinLocation;
    }

    public void setReferencePoint(Coordinates referencePoint) {
	this.referencePoint = referencePoint;
    }

    public void setGreen(Green green) {
//	if(this.green != null)
//	    super.remove(green);

	this.green = green;

//	super.add(green);
    }

    @Override
    public BasicArea getApproachLine() {
        return approachLine;
    }

    public void setApproachLine(BasicArea approachLine) {
        this.approachLine = approachLine;
    }

    public void setHoleOutline(BasicArea holeOutline) {
	this.holeOutline = holeOutline;
    }

    public void setCartPath(Path cartPath) {
	this.cartPath = cartPath;
    }

    public void setCartPathOnly(boolean cartPathOnly) {
	this.cartPathOnly = cartPathOnly;
    }

    public void setTeeBoxes(List<TeeBox> teeBoxes) {
	this.teeBoxes = teeBoxes;
    }

    public void addHoleZone(HoleZone holeZone) {
	holeZones.add(holeZone);
    }

    /*
     * Getters
     */

    @Override
    public Course getCourse() {
	return course;
    }

    @Override
    public int getNumber() {
	return getId();
    }

    @Override
    public int getAbsoluteNumber() {
	if(course != null && course.getGolfClub() != null) {
	    int absoluteNumber = 0;
	    int courseNumber = course.getNumber();
	    GolfClub golfClub = course.getGolfClub();

	    //Add hole count before the current course
	    for(int n = 1; n < courseNumber; n++)
		absoluteNumber += golfClub.getCourse(n).getHoleCount();

	    absoluteNumber += getNumber();
	    return absoluteNumber;
	}
        else {
            return getNumber();
        }
    }

    @Override
    public Coordinates getPinLocation() {
	return pinLocation;
    }

    @Override
    public Coordinates getReferencePoint() {
	if (referencePoint != null) {
            return referencePoint;
        }
        else if (approachLine != null) {
            List<Coordinates> shape = approachLine.getShape();
            if (shape != null && shape.size() >= 2) {
                return shape.get(1);
            }
        }

        return null;
    }

    public BasicMapImage getMapImage() {
	return mapImage;
    }

    @Override
    public String getFlyover() {
	return flyover;
    }

    @Override
    public String getProTips() {
	return proTips;
    }

    @Override
    public int getPaceOfPlay() {
	return paceOfPlay;
    }

    @Override
    public int getStrokeIndex() {
	return strokeIndex;
    }

    @Override
    public int getPar() {
	return par;
    }

    @Override
    public BasicArea getHoleOutline() {
	return holeOutline;
    }

    @Override
    public Path getCartPath() {
	return cartPath;
    }

    @Override
    public Green getGreen() {
	return green;
    }

    @Override
    public boolean isCartPathOnly() {
	return cartPathOnly;
    }

    @Override
    public List<TeeBox> getTeeBoxes() {
	return teeBoxes;
    }

    @Override
    public List<HoleZone> getHoleZones() {
	return unmodifiableHoleZones;
    }

    /*
     * Implement CourseElement
     */
    @Override
    public ObjectType getType() {
	return ObjectTypes.GOLF_HOLE;
    }

    @Override
    public void setParent(CourseElement parent) {
	throw new IllegalArgumentException("Parent must be a Course");
    }

    public void setParent(CourseImpl course) {
	super.setParent(course);
	this.course = course;
    }

    /*
     * Override CompositeCourseElement
     */
    public void add(Green green) {
	setGreen(green);
    }

    @Override
    public String toString() {
        return "Course: " + (course == null? null : course.getName()) + " Hole: " + getNumber();
    }

}
