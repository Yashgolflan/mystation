/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.stayprime.oncourseads;

import com.stayprime.util.gson.Exclude;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author benjamin
 */
public class Ad {
    public static final int TYPE_BANNER = 0;
    public static final int TYPE_SMALL_AD = 1;
    public static final int TYPE_BIG_AD = 2;
    public int clientId;
    public Integer id;
    public String name;
    public String description;
    public int category;
    public int type;
    public String source;
    public List<Contract> contracts;
    public Date updated;
    @Exclude
    public transient Client client;

    public Ad(int clientId, Integer id, String name) {
	this.id = id;
	this.clientId = clientId;
	this.name = name;
        contracts = new ArrayList<Contract>();
    }

    public List<Contract> getContracts() {
	return contracts;
    }

    public boolean hasActiveContracts() {
        return hasActiveContracts(System.currentTimeMillis());
    }

    public boolean hasActiveContracts(long when) {
	if(contracts != null) {
	    for(Contract contract: contracts) {
		if(contract.isActive(when))
		    return true;
	    }
	}
	return false;
    }

    public boolean isActiveForHole(int course, int hole, long when) {
	if(contracts != null) {
	    for (Contract contract: contracts) {
		if (contract.isActive(when) && contract.isForHole(course, hole)) {
		    return true;
                }
	    }
	}
	return false;
    }

    @Override
    public boolean equals(Object o) {
	return o == this || (id != null && o instanceof Ad && id.equals(((Ad)o).id));
    }

    @Override
    public int hashCode() {
	int hash = 3;
	hash = 17 * hash + (this.id != null ? this.id.hashCode() : 0);
	return hash;
    }

    @Override
    public String toString() {
	return name + ",id=" + id + ",client=" + clientId + "type=" + type;
    }
}
