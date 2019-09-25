/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.aeben.golfclub;

import com.stayprime.util.gson.Exclude;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author benjamin
 */
public class CourseDefinition extends GolfCourseObject {

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 23 * hash + (this.golfClub != null ? this.golfClub.hashCode() : 0);
        hash = 23 * hash + this.courseNumber;
        hash = 23 * hash + (this.courseName != null ? this.courseName.hashCode() : 0);
        hash = 23 * hash + (this.teeBoxes != null ? this.teeBoxes.hashCode() : 0);
        hash = 23 * hash + Arrays.deepHashCode(this.holes);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CourseDefinition other = (CourseDefinition) obj;
        if (this.courseNumber != other.courseNumber) {
            return false;
        }
        if ((this.courseName == null) ? (other.courseName != null) : !this.courseName.equals(other.courseName)) {
            return false;
        }
        if (this.golfClub != other.golfClub && (this.golfClub == null || !this.golfClub.equals(other.golfClub))) {
            return false;
        }
        if (this.teeBoxes != other.teeBoxes && (this.teeBoxes == null || !this.teeBoxes.equals(other.teeBoxes))) {
            return false;
        }
        if (!Arrays.deepEquals(this.holes, other.holes)) {
            return false;
        }
        return true;
    }
    @Exclude
    private GolfClub golfClub;

    private int courseNumber = 0;
    private String courseName;
    private List<TeeBox> teeBoxes;
    private HoleDefinition[] holes;
    private static final GolfCourseObject.ObjectType type = GolfCourseObject.ObjectType.GOLF_COURSE;


    public CourseDefinition(GolfClub golfClub, int number, String name, int holeCount) {
        this.golfClub = golfClub;
        this.courseNumber = number;
        this.courseName = name;

        holes = new HoleDefinition[holeCount];
	teeBoxes = new ArrayList<TeeBox>();
    }

    public int getHoleCount() {
        return holes.length;
    }

    public void setHoleCount(int holeCount) {
        HoleDefinition newHoles[] = new HoleDefinition[holeCount];
        System.arraycopy(holes, 0, newHoles, 0, Math.min(getHoleCount(), holeCount));
        this.holes = newHoles;
    }

    public HoleDefinition getHoleNumber(int number) {
        return holes[number - 1];
    }

    public void setHoleNumber(int number, HoleDefinition hole) {
        if(hole.getId() != number)
            throw new IllegalArgumentException("Hole number is different to the HoleDefinition");
        
        holes[number - 1] = hole;
    }

    @Override
    public String toString() {
        return courseNumber + ". " + courseName;
    }

    public String getName() {
        return courseName;
    }

    public Integer getId() {
        return courseNumber > 0? courseNumber : null;
    }

    public GolfCourseObject getParentObject() {
        return golfClub;
    }

    public ObjectType getType() {
        return type;
    }

    public GolfClub getGolfClub() {
        return golfClub;
    }

    public void setGolfClub(GolfClub golfClub) {
        this.golfClub = golfClub;
    }

    public int getCourseNumber() {
        return courseNumber;
    }

    public void setCourseNumber(int courseNumber) {
        this.courseNumber = courseNumber;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public List<TeeBox> getTeeBoxes() {
        return teeBoxes;
    }

    public void setTeeBoxes(List<TeeBox> teeBoxes) {
        this.teeBoxes = teeBoxes;
    }

    public HoleDefinition[] getHoles() {
        return holes;
    }

    public void setHoles(HoleDefinition[] holes) {
        this.holes = holes;
    }

    void setParents() {
        for (HoleDefinition h : holes) {
            if (h != null) {
                h.setParents(this);
            }
            else {
                h = null;
            }
        }
    }
}
