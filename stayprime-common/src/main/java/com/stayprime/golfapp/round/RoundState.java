/*
 * 
 */
package com.stayprime.golfapp.round;

import com.stayprime.device.Time;
import com.stayprime.golf.course.GolfCourse;
import com.stayprime.golf.course.GolfHole;
import com.stayprime.model.golf.GolfRound;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang.ObjectUtils;

/**
 *
 * @author benjamin
 */
public class RoundState {
    private final GolfRound round;

    private GolfRoundState state = GolfRoundState.INITIAL;

    //List of played holes during the current round.
    private final List<HoleStat> playedHoles;

    //Current (playing) hole and time when the hole was entered.
    //currentHole.hole is null when the round hasn't started.
    private HoleStat currentHole;

    //Hole set manually by the user and time it was set.
    private HoleStat manualHole;

    //Current hole in the user view
    private GolfHole viewHole;

    private boolean playedHoleRemoved = false;

    private boolean holeEntered = false;

    //Ffalse if the cart hasn't played an entire hole yet
    private boolean holePlayed = false;

    public RoundState() {
        round = new GolfRound();
	playedHoles = new ArrayList<HoleStat>();
	currentHole = new HoleStat(null);
	manualHole = new HoleStat(null);
    }

    public GolfRoundState getState() {
        return state;
    }

    public void setState(GolfRoundState state) {
        this.state = state;
    }

    public HoleStat getCurrentHoleStat() {
        return currentHole;
    }

    public GolfHole getCurrentHole() {
        return currentHole.getHole();
    }

    public void setCurrentHole(HoleStat holeStat) {
        if (ObjectUtils.notEqual(currentHole.getHole(), holeStat.getHole())) {
            this.currentHole = holeStat;
            holeEntered = true;
            playedHoles.add(holeStat);
            playedHoleRemoved = false;
        }
    }

    public GolfCourse getCurrentCourse() {
        GolfHole hole = currentHole.getHole();
        if (hole != null) {
            return hole.getGolfCourse();
        }
        else {
            return null;
        }
    }

    public boolean isHoleEntered() {
        return holeEntered;
    }

    public boolean isHolePlayed() {
        return holePlayed;
    }

    public void setHolePlayed() {
        this.holePlayed = true;
    }

    public boolean playedHolesEmpty() {
        return playedHoles.isEmpty();
    }

    public int getPlayedHolesCount() {
        return playedHoles.size();
    }

    public List<HoleStat> getPlayedHoles() {
        return Collections.unmodifiableList(playedHoles);
    }

    public HoleStat getFirstPlayedHoleStat() {
        return playedHoles.isEmpty()? null : playedHoles.get(0);
    }

    public void removeCurrentHole() {
        if (playedHoleRemoved == false && playedHoles.isEmpty() == false) {
            playedHoleRemoved = true;
            playedHoles.remove(playedHoles.size() - 1);
        }
    }

    public void updateCurrentHoleLastTeeboxTime() {
        if (currentHole.getHole() != null) {
            currentHole.setLastTeeboxTime(Time.milliTime());
        }
    }

    public GolfHole getViewHole() {
        return viewHole;
    }

    public void setViewHole(GolfHole viewHole) {
        this.viewHole = viewHole;
    }

    public HoleStat getManualHole() {
        return manualHole;
    }

    public void setManualHole(HoleStat manualHole) {
        this.manualHole = manualHole;
    }

}