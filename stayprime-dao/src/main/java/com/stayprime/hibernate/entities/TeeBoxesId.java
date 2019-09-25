package com.stayprime.hibernate.entities;
// Generated Sep 17, 2014 5:18:02 PM by Hibernate Tools 4.3.1

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * TeeBoxesId generated by hbm2java
 */
@Embeddable
public class TeeBoxesId implements java.io.Serializable {

    @Column(nullable = false)
    private int course;

    @Column(nullable = false)
    private int hole;

    @Column(nullable = false)
    private int number;

    public TeeBoxesId() {
    }

    public TeeBoxesId(int course, int hole, int number) {
        this.course = course;
        this.hole = hole;
        this.number = number;
    }

    public int getCourse() {
        return this.course;
    }

    public void setCourse(int course) {
        this.course = course;
    }

    public int getHole() {
        return this.hole;
    }

    public void setHole(int hole) {
        this.hole = hole;
    }

    public int getNumber() {
        return this.number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public boolean equals(Object other) {
        if ((this == other))
            return true;
        if ((other == null))
            return false;
        if (!(other instanceof TeeBoxesId))
            return false;
        TeeBoxesId castOther = (TeeBoxesId) other;

        return (this.getCourse() == castOther.getCourse())
                && (this.getHole() == castOther.getHole())
                && (this.getNumber() == castOther.getNumber());
    }

    public int hashCode() {
        int result = 17;

        result = 37 * result + this.getCourse();
        result = 37 * result + this.getHole();
        result = 37 * result + this.getNumber();
        return result;
    }

}
