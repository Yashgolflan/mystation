/*
 * 
 */

package com.stayprime.oncourseads;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author benjamin
 */
public class AdList {
    private List<Ad> ads;
    private String version;
    private Date lastUpdated;

    public AdList() {
        this.ads = new ArrayList<Ad>();
    }

    public AdList(int capacity) {
        this.ads = new ArrayList<Ad>(capacity);
    }

    public AdList(List<Ad> ads) {
        this.ads = new ArrayList<Ad>(ads);
    }

    public List<Ad> getList() {
        return ads;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public boolean add(Ad e) {
        return ads.add(e);
    }

}
