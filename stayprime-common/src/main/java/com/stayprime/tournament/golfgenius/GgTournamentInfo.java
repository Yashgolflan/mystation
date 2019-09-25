/*
 * 
 */
package com.stayprime.tournament.golfgenius;

import java.util.List;

/**
 *
 * @author benjamin
 */
public class GgTournamentInfo {

    private int stayprimeSiteId;

    private int stayprimeToken;

    private List<GgCart> carts;

    private List<GgTournament> tournaments;

    public int getStayprimeSiteId() {
        return stayprimeSiteId;
    }

    public void setStayprimeSiteId(int stayprimeSiteId) {
        this.stayprimeSiteId = stayprimeSiteId;
    }

    public int getStayprimeToken() {
        return stayprimeToken;
    }

    public void setStayprimeToken(int stayprimeToken) {
        this.stayprimeToken = stayprimeToken;
    }

    public List<GgCart> getCarts() {
        return carts;
    }

    public void setCarts(List<GgCart> carts) {
        this.carts = carts;
    }

    public List<GgTournament> getTournaments() {
        return tournaments;
    }

    public void setTournaments(List<GgTournament> tournaments) {
        this.tournaments = tournaments;
    }

}
