/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.stayprime.oncourseads;

import java.util.Date;

/**
 *
 * @author benjamin
 */
public class Client {
    public Integer id;
    public String name;
    public String details;
    public String contactInfo;
    public String email;
    public int reportPrefs;
    public boolean active;
    public Date created;
    public AdList ads;

    public Client() {
    }

    public Client(Integer id, String name) {
	this.id = id;
	this.name = name;
    }

    public int getActiveAdsCount() {
	int count = 0;
        long now = System.currentTimeMillis();
	if (ads != null) {
	    for (Ad ad: ads.getList()) {
		if (ad.hasActiveContracts(now)) {
		    count++;
                }
            }
	}
	return count;
    }

    public int getAdsCount() {
        if (ads != null) {
            return ads.getList().size();
        }
        else {
            return 0;
        }
    }

    @Override
    public String toString() {
	return name;
    }
}
