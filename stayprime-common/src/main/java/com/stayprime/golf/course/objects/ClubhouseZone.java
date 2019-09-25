/*
 * 
 */

package com.stayprime.golf.course.objects;

import com.stayprime.golf.objects.BasicArea;
import com.stayprime.golf.course.Site;
import com.stayprime.golf.objects.GeomType;

/**
 *
 * @author benjamin
 */
public class ClubhouseZone extends BasicArea {
    private Integer resetGameDelay = null;
    private boolean cartBarn = false;

    public ClubhouseZone(Site parent, ObjectType type, Integer id, String name) {
	super(type, GeomType.POLY, name);
    }

    public Integer getResetGameDelay() {
	return resetGameDelay;
    }

    public void setResetGameDelay(Integer resetGameDelay) {
	this.resetGameDelay = resetGameDelay;
    }

    public boolean isCartBarn() {
	return cartBarn;
    }

    public void setCartBarn(boolean cartBarn) {
	this.cartBarn = cartBarn;
    }

}
