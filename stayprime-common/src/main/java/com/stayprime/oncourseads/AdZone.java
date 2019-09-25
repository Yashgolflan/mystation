/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.stayprime.oncourseads;

import com.stayprime.golf.objects.BasicArea;
import com.stayprime.golf.course.objects.ObjectType;
import com.stayprime.golf.objects.AbstractFeature;
import com.stayprime.golf.objects.GeomType;
import com.stayprime.util.gson.Exclude;

/**
 *
 * @author benjamin
 */
public class AdZone extends BasicArea {
    @Exclude
    private Contract contract;
    private boolean keepVisible;

    public AdZone(String name, Integer id) {
	super(ObjectType.AD_ZONE, GeomType.POLY, name);
    }

    public AdZone(AbstractFeature f) {
	super(ObjectType.AD_ZONE, GeomType.POLY, f.getName());
    }

    public Contract getContract() {
	return contract;
    }

    public void setContract(Contract contract) {
	this.contract = contract;
    }

    public void setKeepVisible(boolean keepVisible) {
	this.keepVisible = keepVisible;
    }

    public boolean isKeepVisible() {
	return keepVisible;
    }

}
