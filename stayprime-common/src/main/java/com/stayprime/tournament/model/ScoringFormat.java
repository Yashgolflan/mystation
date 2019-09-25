/*
 * 
 */
package com.stayprime.tournament.model;

/**
 *
 * @author benjamin
 */
public class ScoringFormat {
    private String display;
    private String type;
    private boolean team;
    private boolean matchPlay;

    public ScoringFormat() {
    }

    public ScoringFormat(String display, String type, boolean team, boolean matchPlay) {
        this.display = display;
        this.type = type;
        this.team = team;
        this.matchPlay = matchPlay;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isTeam() {
        return team;
    }

    public void setTeam(boolean team) {
        this.team = team;
    }

    public boolean isMatchPlay() {
        return matchPlay;
    }

    public void setMatchPlay(boolean matchPlay) {
        this.matchPlay = matchPlay;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ScoringFormat
                && ((ScoringFormat)obj).hashCode() == hashCode();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + (this.display != null ? this.display.hashCode() : 0);
        hash = 79 * hash + (this.type != null ? this.type.hashCode() : 0);
        hash = 79 * hash + (this.team ? 1 : 0);
        hash = 79 * hash + (this.matchPlay ? 1 : 0);
        return hash;
    }

}
