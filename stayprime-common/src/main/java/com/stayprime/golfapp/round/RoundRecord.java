/*
 * 
 */
package com.stayprime.golfapp.round;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 *
 * @author benjamin
 */
public class RoundRecord {
    private Calendar date;
    private boolean ongoing = true;

    private int playedHoles = 0;
    private int played9HoleRounds = 0;
    private int played18HoleRounds = 0;

    private List<RoundDescription> rounds;

    public RoundRecord(Calendar date) {
        this.date = date;
        rounds = new ArrayList<RoundDescription>();
    }

    public Calendar getDate() {
        return date;
    }

    public void addPlayedHole() {
        playedHoles++;
    }

    public int getPlayedHoles() {
        return playedHoles;
    }

    public void addPlayed9HoleRound() {
        played9HoleRounds++;
    }

    public int getPlayed9HoleRounds() {
        return played9HoleRounds;
    }

    public void addPlayed18HoleRound() {
        played18HoleRounds++;
    }

    public int getPlayed18HoleRounds() {
        return played18HoleRounds;
    }

    public void addRound(RoundDescription round) {
        rounds.add(round);
    }

    public void finalizeRecord() {
        this.ongoing = false;
    }

    @Override
    public String toString() {
        return "Played holes: " + playedHoles +
                ", 9holes: " + played9HoleRounds +
                ", 18holes: " + played18HoleRounds;
    }

}
