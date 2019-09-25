/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stayprime.tournament.impact.model;

import com.stayprime.tournament.model.Player;
import com.stayprime.tournament.model.Player;
import java.util.Date;

/**
 *
 * @author Omer
 */
public class Leaderboard {
    
    private String tournamentName;
    private String date;
    private Date lastUpdated;
    private int currentRoundNo;
    private String logo;    
    private Player player;
    private String parData;
    private String handicapData;
    private String scoringResults;
    private String courses;

    
    public String getTournamentName() {
        return tournamentName;
    }

    public void setTournamentName(String TournamentName) {
        this.tournamentName = TournamentName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String Date) {
        this.date = Date;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public int getCurrentRoundNo() {
        return currentRoundNo;
    }

    public void setCurrentRoundNo(int currentRoundNo) {
        this.currentRoundNo = currentRoundNo;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public String getParData() {
        return parData;
    }

    public void setParData(String parData) {
        this.parData = parData;
    }

    public String getHandicapData() {
        return handicapData;
    }

    public void setHandicapData(String handicapData) {
        this.handicapData = handicapData;
    }

    public String getScoringResults() {
        return scoringResults;
    }

    public void setScoringResults(String scoringResults) {
        this.scoringResults = scoringResults;
    }

    public String getCourses() {
        return courses;
    }

    public void setCourses(String courses) {
        this.courses = courses;
    }        
    
}
