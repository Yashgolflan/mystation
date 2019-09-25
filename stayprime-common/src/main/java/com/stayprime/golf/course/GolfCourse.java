/*
 *
 */
package com.stayprime.golf.course;

import com.stayprime.geo.BasicMapImage;
import com.stayprime.util.gson.Exclude;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;

/**
 *
 * @author benjamin
 */
//@Entity
public class GolfCourse {
    @Exclude
    @ManyToOne
    @JoinColumn(name = "siteId")
    private Site site;

    @Id @GeneratedValue
    private Integer courseId;

    private int courseIndex;

    private String name;

    private String details;

    private int holeCount;

    @Embedded
    private BasicMapImage mapImage;

    @OneToMany(mappedBy = "golfCourse", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GolfHole> holes;


    public GolfCourse() {
        this(null, null, 0);
    }

    public GolfCourse(Site site, String name, int number) {
	this.site = site;
	this.name = name;
	this.courseIndex = number;

        holes = new ArrayList<GolfHole>();
    }

    public Site getSite() {
        return site;
    }

    public void setSite(Site golfSite) {
        this.site = golfSite;
    }

    public Integer getCourseId() {
        return courseId;
    }

    public void setCourseId(Integer courseId) {
        this.courseId = courseId;
    }

    public int getCourseIndex() {
        return courseIndex;
    }

    public void setCourseIndex(int index) {
        this.courseIndex = index;
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

    public BasicMapImage getMapImage() {
        return mapImage;
    }

    public void setMapImage(BasicMapImage map) {
        this.mapImage = map;
    }

    public int getHoleCount() {
        return holeCount;
    }

    public void setHoleCount(int holeCount) {
        //trimHoleCount(holeCount);
        this.holeCount = holeCount;
    }

    public List<GolfHole> getHoles() {
        return holes;
    }

    public GolfHole getHole(int n) {
	if(holes == null) {
	    return null;
        }
        else {
	    return holes.get(n - 1);
        }
    }

    public void addHole(GolfHole hole) {
        hole.setGolfCourse(this);
        holes.add(hole);
    }

    public void removeHole(int n) {
        removeHoleFromList(n);
        setHoleCount(holes.size());
    }

    private void removeHoleFromList(int n) {
        GolfHole hole = holes.remove(n - 1);
        hole.setGolfCourse(null);
    }

    public void setHole(int n, GolfHole hole) {
        holes.get(n - 1).setGolfCourse(null);
        hole.setGolfCourse(this);
        holes.set(n - 1, hole);
    }

    public void trimHoleCount() {
        if (holes == null) {
            holes = new ArrayList<GolfHole>(holeCount);
        }

        while (holes.size() > holeCount) {
            removeHoleFromList(holes.size());
        }

	for(int n = holes.size(); n < holeCount; n++) {
	    addHole(new GolfHole(this, n + 1));
	}
    }

    /**
     * Calculates the absolute index of the first hole.
     * @return hole_index for the first hole
     */
    public int getFirstHoleIndex() {
        int i = 1;

        if (site != null) {
            for (int n = 1; n <= site.getCourseCount() && n < courseIndex; n++) {
                i += site.getCourse(n).getHoleCount();
            }
        }

        return i;
    }

    @Override
    public String toString() {
        return name;
    }

    public void setParents() {
        for(GolfHole golfHole: holes){
            golfHole.setGolfCourse(this);
            golfHole.setParents();
        }

    }
}
