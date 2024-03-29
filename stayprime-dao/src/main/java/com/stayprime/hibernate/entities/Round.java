package com.stayprime.hibernate.entities;
// Generated Sep 17, 2014 5:18:02 PM by Hibernate Tools 4.3.1

import java.util.Date;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Round generated by hbm2java
 */
@Entity
@Table(name = "Cart_Round")
public class Round implements java.io.Serializable {

//    @EmbeddedId
//    @AttributeOverrides({
//        @AttributeOverride(name = "cartNumber", column = @Column(name = "cart_number", nullable = false))
//        ,
//        @AttributeOverride(name = "timestamp", column = @Column(name = "timestamp", updatable = true,nullable = false, length = 19))})
//    private RoundId id;
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Integer roundId;

    @Column
    private Integer cartNumber;

    @Temporal(TemporalType.TIMESTAMP)
    private Date startTime;

    @Temporal(TemporalType.TIMESTAMP)
    private Date endTime;

    @OneToMany(mappedBy = "cartRound", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<RoundHole> roundHoleSet;

    public Round() {
    }

    public Round(Integer cartNumber, Date startTime, Date endTime) {
        this.cartNumber = cartNumber;
        this.startTime = startTime;
        this.endTime = endTime;

    }

    public Integer getRoundId() {
        return roundId;
    }

    public Integer getCartNumber() {
        return this.cartNumber;
    }

    public void setCartNumber(Integer cartNumber) {
        this.cartNumber = cartNumber;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return this.endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Set<RoundHole> getRoundHoleSet() {
        return roundHoleSet;
    }

    public void setRoundHoleSet(Set<RoundHole> roundHoleSet) {
        this.roundHoleSet = roundHoleSet;
    }

}
