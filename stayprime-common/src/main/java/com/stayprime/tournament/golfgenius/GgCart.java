/*
 * 
 */
package com.stayprime.tournament.golfgenius;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

/**
 *
 * @author benjamin
 */
@Entity
public class GgCart {

    @Id
    private int number;

    private int eventId;

//    @ManyToOne
//    @Column(name="eventId")
//    private GgEvent event;

    private int courseId;

    private int teamId;

    private int startingHole;

    private Date startingTime;

    private Date updated;

    private Date unitUpdated;

    @Transient
    private List<GgPlayer> playerList = new ArrayList<GgPlayer>();

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
    
    public int getEventId() {
        return eventId;
    }
    
    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public int getTeamId() {
        return teamId;
    }

    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }

    public int getStartingHole() {
        return startingHole;
    }

    public void setStartingHole(int startingHole) {
        this.startingHole = startingHole;
    }

    public Date getStartingTime() {
        return startingTime;
    }

    public void setStartingTime(Date startingTime) {
        this.startingTime = startingTime;
    }

    @Access(AccessType.PROPERTY)
    public String getPlayers() {
        return GgPlayer.listToString(playerList);
    }

    public void setPlayers(String players) {
        playerList = GgPlayer.addToListFromString(new ArrayList<GgPlayer>(), players);
    }

    public List<GgPlayer> getPlayerList() {
        return playerList;
    }

    public void setPlayerList(List<GgPlayer> playerList) {
        this.playerList = playerList;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public Date getUnitUpdated() {
        return unitUpdated;
    }

    public void setUnitUpdated(Date unitUpdated) {
        this.unitUpdated = unitUpdated;
    }

    @Override
    public String toString() {
        return "GgCart number:" + number + ", updated:" + updated
                + ", unitUpdated:" + unitUpdated + ", players:" + playerList;
    }

}
