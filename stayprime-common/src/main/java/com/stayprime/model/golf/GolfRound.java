/*
 * 
 */
package com.stayprime.model.golf;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.joda.time.DateTime;


/**
 *
 * @author benjamin
 */
//@Entity
public class GolfRound {

    @Id @GeneratedValue
    private long id;

    @ManyToOne
    private GolfCart cart;

    private DateTime startTime;

    private DateTime endTime;

    private int paceOfPlay;

    private int startHole;

    private int playedHolesCount;

    private int currentHole;

    public GolfRound() {
    }

    public GolfRound(int startHole, int currentHole, int paceOfPlay) {
        this.paceOfPlay = paceOfPlay;
        this.startHole = startHole;
        this.currentHole = currentHole;
    }

    /*
     * Accessors
     */

    public long getId() {
        return id;
    }

    public GolfCart getCart() {
        return cart;
    }

    public void setCart(GolfCart cart) {
        this.cart = cart;
    }

    public DateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(DateTime startTime) {
        this.startTime = startTime;
    }

    public DateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(DateTime endTime) {
        this.endTime = endTime;
    }

    public int getPaceOfPlay() {
        return paceOfPlay;
    }

    public void setPaceOfPlay(int paceOfPlay) {
        this.paceOfPlay = paceOfPlay;
    }

    public int getStartHole() {
        return startHole;
    }

    public void setStartHole(int startHole) {
        this.startHole = startHole;
    }

    public int getPlayedHolesCount() {
        return playedHolesCount;
    }

    public void setPlayedHolesCount(int playedHolesCount) {
        this.playedHolesCount = playedHolesCount;
    }

    public int getCurrentHole() {
        return currentHole;
    }

    public void setCurrentHole(int currentHole) {
        this.currentHole = currentHole;
    }


    /*
     * Functional methods
     */

    public boolean isStarted() {
        return playedHolesCount > 0 || currentHole > 0;
    }

    /*
     * Overriden methods
     */

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof GolfRound)) {
            return false;
        }

        if (obj == this) {
            return true;
        }

        GolfRound r = (GolfRound) obj;
        return new EqualsBuilder()
                .append(startHole, r.startHole)
                .append(playedHolesCount, r.playedHolesCount)
                .append(currentHole, r.currentHole)
                .append(paceOfPlay, r.paceOfPlay)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(113, 101)
                .append(startHole)
                .append(playedHolesCount)
                .append(currentHole)
                .append(paceOfPlay)
                .toHashCode();
    }
}
