/*
 * 
 */
package com.stayprime.model.golf;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import org.joda.time.DateTime;

/**
 *
 * @author benjamin
 */
//@Entity
public class CartRecord {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToOne
    private GolfCart cart;

    private DateTime time;

    private String cartMode;

    private Position position;

    private double heading;

    private double speed;

    private float batteryLevel;

    @ManyToOne
    private GolfRound golfRound;

    public CartRecord() {
    }

    public long getId() {
        return id;
    }

     void setId(long id) {
        this.id = id;
    }

    public GolfCart getCart() {
        return cart;
    }

    public void setCart(GolfCart cart) {
        this.cart = cart;
    }

    public DateTime getTime() {
        return time;
    }

    public void setTime(DateTime time) {
        this.time = time;
    }

    public String getCartMode() {
        return cartMode;
    }

    public void setCartMode(String cartMode) {
        this.cartMode = cartMode;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public double getHeading() {
        return heading;
    }

    public void setHeading(double heading) {
        this.heading = heading;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public float getBatteryLevel() {
        return batteryLevel;
    }

    public void setBatteryLevel(float batteryLevel) {
        this.batteryLevel = batteryLevel;
    }

    public GolfRound getGolfRound() {
        return golfRound;
    }

    public void setGolfRound(GolfRound golfRound) {
        this.golfRound = golfRound;
    }

}
