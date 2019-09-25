/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stayprime.tournament.impact.model;

/**
 *
 * @author Omer
 */
public class TournamentList {
    
    private String id;
    private String name;
    private String clubname;   
    private String year;
    private String startDate;
    private String endDate;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public String getClubname() {
        return clubname;
    }

    public void setClubname(String clubname) {
        this.clubname = clubname;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }
    
    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }   
    
}
