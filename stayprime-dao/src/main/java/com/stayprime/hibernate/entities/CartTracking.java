package com.stayprime.hibernate.entities;
// Generated Sep 17, 2014 5:18:02 PM by Hibernate Tools 4.3.1

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * CartTracking generated by hbm2java
 */
@Entity
@Table(name = "cart_tracking")
public class CartTracking implements java.io.Serializable {

    @EmbeddedId
    @AttributeOverrides({
        @AttributeOverride(name = "cartNumber", column = @Column(name = "cart_number", nullable = false)),
        @AttributeOverride(name = "timestamp", column = @Column(name = "timestamp", nullable = false, length = 19))
    })
    private CartTrackingId id;

    private double latitude;

    private double longitude;

    private Float heading;

    private Float speed;

    private Float distance;

    private Integer roundId;

    public CartTracking() {
    }

    public CartTracking(CartTrackingId id, double latitude, double longitude) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public CartTracking(CartTrackingId id, double latitude, double longitude, Float heading, Float speed) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.heading = heading;
        this.speed = speed;
    }

    public CartTrackingId getId() {
        return this.id;
    }

    public void setId(CartTrackingId id) {
        this.id = id;
    }

    public double getLatitude() {
        return this.latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return this.longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public Float getHeading() {
        return this.heading;
    }

    public void setHeading(Float heading) {
        this.heading = heading;
    }

    public Float getSpeed() {
        return this.speed;
    }

    public void setSpeed(Float speed) {
        this.speed = speed;
    }

    public Float getDistance() {
        return distance;
    }

    public void setDistance(Float distanceTravelled) {
        this.distance = distanceTravelled;
    }

    public Integer getRoundId() {
        return this.roundId;
    }

    public void setRoundId(Integer roundId) {
        this.roundId = roundId;
    }

}