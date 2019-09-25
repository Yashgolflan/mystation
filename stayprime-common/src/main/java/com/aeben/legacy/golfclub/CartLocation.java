/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.aeben.legacy.golfclub;

import com.stayprime.geo.Coordinates;
import com.stayprime.golf.course.objects.ObjectType;
import com.stayprime.golf.objects.BasicPoint;
import java.util.Date;

/**
 *
 * @author benjamin
 */
public class CartLocation extends BasicPoint implements Comparable<CartLocation> {
    private Integer cartNumber;
    private String address;
    private Float heading;
    private Coordinates location;
    private Float speed;
    private Date statusLastUpdated;

    public CartLocation() {
        super(null, 0, "");
    }

    public CartLocation(Integer cartNumber) {
        super(null, cartNumber, "Cart " + cartNumber);
        this.cartNumber = cartNumber;
    }

    public String getName() {
        return null;
    }

    public Integer getId() {
        return cartNumber;
    }

    public ObjectType getType() {
        return ObjectType.GOLFCART;
    }

//    @Override
//    public GolfCourseObject clone() {
//        CartLocation cartLocation = new CartLocation(cartNumber);
//        cartLocation.address = address;
//        cartLocation.heading = heading;
//        cartLocation.location = this.location.clone();
//        cartLocation.parentObject = parentObject;
//        cartLocation.speed = speed;
//        cartLocation.statusLastUpdated = statusLastUpdated;
//        return cartLocation;
//    }

    @Override
    public boolean equals(Object obj) {
	if(obj instanceof CartLocation) {
	    CartLocation cartStatus = (CartLocation) obj;
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

    public Integer getCartNumber() {
        return cartNumber;
    }

    public void setCartNumber(Integer cartNumber) {
        this.cartNumber = cartNumber;
        super.setName("Cart " + cartNumber);
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Float getHeading() {
        return heading;
    }

    public void setHeading(Float heading) {
        this.heading = heading;
    }

    public Float getSpeed() {
        return speed;
    }

    public void setSpeed(Float speed) {
        this.speed = speed;
    }

    public Date getStatusLastUpdated() {
        return statusLastUpdated;
    }

    public void setStatusLastUpdated(Date statusLastUpdated) {
        this.statusLastUpdated = statusLastUpdated;
    }

}
