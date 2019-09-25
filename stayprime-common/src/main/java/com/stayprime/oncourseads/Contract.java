/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.stayprime.oncourseads;

import com.stayprime.util.gson.Exclude;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang.math.NumberUtils;

/**
 *
 * @author benjamin
 */
public class Contract {
    public static final int PLAN_LIGHT = 0;
    public static final int PLAN_MODERATE = 1;
    public static final int PLAN_HEAVY = 2;
    public static final int PLAN_SPONSOR = 3;
    public static final int INACTIVE = 3;

    private Integer id;
    private Date startDate;
    private Date endDate;
    private int plan;
    private String sponsoredHoles;
    private List<AdZone> adZones;
    @Exclude
    private final Ad ad;

    public Contract(Ad ad, Integer id, Date startDate, Date endDate, int plan) {
	this.ad = ad;
	this.id = id;
	this.startDate = startDate;
	this.endDate = endDate;
	this.plan = plan;
	adZones = new ArrayList<AdZone>();
    }

    public Ad getAd() {
	return ad;
    }

    public List<AdZone> getAdZones() {
	return adZones;
    }

    public void setAdZones(List<AdZone> adZones) {
	this.adZones = adZones;
    }

    public Date getEndDate() {
	return endDate;
    }

    public void setEndDate(Date endDate) {
	this.endDate = endDate;
    }

    public Integer getId() {
	return id;
    }

    public void setId(Integer id) {
	this.id = id;
    }

    public int getPlan() {
	return plan;
    }

    public void setPlan(int plan) {
	this.plan = plan;
    }

    public String getSponsoredHolesString() {
	return sponsoredHoles;
    }

    public void setSponsoredHoles(String sponsoredHoles) {
	this.sponsoredHoles = sponsoredHoles;
    }

    public Date getStartDate() {
	return startDate;
    }

    public void setStartDate(Date startDate) {
	this.startDate = startDate;
    }

    public boolean isActive(long when) {
	if (startDate == null || when >= startDate.getTime()) {
	    if (isExpired(when) == false) {
		return true;
            }
	}

	return false;
    }

    public boolean isExpired(long when) {
	return endDate != null && when >= endDate.getTime();
    }

    public int getSponsoredHoleCount() {
	return countSponsoredHoles(sponsoredHoles);
    }

    public boolean isForHole(int course, int hole) {
	if (sponsoredHoles != null) {
	    String courses[] = sponsoredHoles.split(";");

            for (String courseInfo: courses) {
		String ch[] = courseInfo.split(":");
                if(ch.length == 2 && NumberUtils.toInt(ch[0]) == course) {
                    String h[] = ch[1].split(",");
                    for (String n: h) {
                        if (NumberUtils.toInt(n) == hole) {
                            return true;
                        }
                    }
		}
	    }
	}

        return false;
    }

    public static int countSponsoredHoles(String sponsoredHoles) {
	int count = 0;
	if (sponsoredHoles != null) {
	    String courses[] = sponsoredHoles.split(";");

            for (String course: courses) {
		String courseHoles[] = course.split(":");

                if(courseHoles.length == 2) {
		    count += courseHoles[1].split(",").length;
		}
	    }
	}
	return count;
    }

    @Override
    public boolean equals(Object o) {
	return o == this || (id != null && o instanceof Contract && id.equals(((Contract)o).id));
    }

    @Override
    public int hashCode() {
	int hash = 5;
	hash = 83 * hash + (this.id != null ? this.id.hashCode() : 0);
	return hash;
    }

}
