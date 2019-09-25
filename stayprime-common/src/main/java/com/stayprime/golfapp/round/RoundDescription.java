/*
 * 
 */
package com.stayprime.golfapp.round;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author benjamin
 */
public class RoundDescription {
    private int cartNumber;
    private long startTimeUtc;
    private long endTimeUtc;
    private List<PlayedHole> playedHoles;

    public RoundDescription(int cartNumber) {
        this.cartNumber = cartNumber;
        this.playedHoles = new ArrayList<PlayedHole>();
    }

    public long getStartTimeUtc() {
        return startTimeUtc;
    }

    public void setStartTimeUtc(long startTimeUtc) {
        this.startTimeUtc = startTimeUtc;
    }

    public long getEndTimeUtc() {
        return endTimeUtc;
    }

    public void setEndTimeUtc(long endTimeUtc) {
        this.endTimeUtc = endTimeUtc;
    }

    public void addPlayedHole(PlayedHole playedHole) {
        playedHoles.add(playedHole);
    }

    public List<PlayedHole> getPlayedHoles() {
        return playedHoles;
    }

}
