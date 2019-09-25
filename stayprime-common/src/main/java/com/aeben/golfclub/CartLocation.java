/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.aeben.golfclub;

import com.stayprime.geo.Coordinates;
import java.util.Date;

/**
 *
 * @author benjamin
 */
public class CartLocation extends GolfCourseObject implements Comparable<CartLocation> {
    public Integer cartNumber;
    public String address;
    public Float heading;
    public Coordinates location;
    public Float speed;
    public Date statusLastUpdated;

    public CartLocation() {
    }

    public CartLocation(Integer cartNumber) {
        this.cartNumber = cartNumber;
    }

    public String getName() {
        return null;
    }

    public Integer getId() {
        return cartNumber;
    }

    public GolfCourseObject getParentObject() {
        return null;
    }

    public ObjectType getType() {
        return ObjectType.GOLFCART;
    }

    @Override
    public GolfCourseObject clone() {
        CartLocation cartLocation = new CartLocation(cartNumber);
        cartLocation.address = address;
        cartLocation.heading = heading;
        cartLocation.location = this.location.clone();
        cartLocation.speed = speed;
        cartLocation.statusLastUpdated = statusLastUpdated;
        return cartLocation;
    }

    @Override
    public boolean equals(Object obj) {
	if(obj instanceof CartStatus) {
	    CartStatus cartStatus = (CartStatus) obj;
	    return cartNumber != null && cartNumber.equals(cartStatus.cartNumber);
	}
	else
	    return false;
    }

    @Override
    public int hashCode() {
	int hash = 7;
	hash = 83 * hash + (this.cartNumber != null ? this.cartNumber.hashCode() : 0);
	return hash;
    }

    public int compareTo(CartLocation o) {
	return cartNumber - o.cartNumber;
    }

}
