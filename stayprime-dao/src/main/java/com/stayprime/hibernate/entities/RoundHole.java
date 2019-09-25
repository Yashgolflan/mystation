/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stayprime.hibernate.entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author nirajcj
 */
@Entity
@Table(name = "cart_round_hole")
public class RoundHole implements Serializable {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Integer id;

    @Column
    private Integer holeNumber;

    @Temporal(TemporalType.TIMESTAMP)
    private Date holeStartTime;

    @Temporal(TemporalType.TIMESTAMP)
    private Date holeEndTime;

    @Column
    private Float distance;

    @ManyToOne
    @JoinColumn(name = "round_id",nullable = false)
    private Round cartRound;

    public Integer getId() {
        return id;
    }

    public RoundHole() {

    }

    public RoundHole(Integer holeNumber, Date holeStartTime, Date holeEndTime,Float distance,Round cartRound) {
        this.holeNumber = holeNumber;
        this.holeStartTime = holeStartTime;
        this.holeEndTime = holeEndTime;
        this.distance=distance;
        this.cartRound=cartRound;
    }

    public Integer getHoleNumber() {
        return holeNumber;
    }

    public void setHoleNumber(Integer holeNumber) {
        this.holeNumber = holeNumber;
    }

    public Date getHoleStartTime() {
        return holeStartTime;
    }

    public void setHoleStartTime(Date roundStartTime) {
        this.holeStartTime = holeStartTime;
    }

    public Date getHoleEndTime() {
        return holeEndTime;
    }

    public void setHoleEndTime(Date holeEndTime) {
        this.holeEndTime = holeEndTime;
    }

    public Round getCartRound() {
        return cartRound;
    }

    public void setCartRound(Round catRound) {
        this.cartRound = cartRound;
    }

    public Float getDistance() {
        return distance;
    }

    public void setDistance(Float distance) {
        this.distance = distance;
    }

}
