/*
 *
 */

package com.stayprime.golfapp.round;

import com.stayprime.geo.Coordinates;
import com.stayprime.cartapp.CartAppConst;
import com.stayprime.golf.course.objects.ClubhouseZone;
import com.stayprime.golf.course.objects.HoleZone;
import com.stayprime.golf.course.GolfCourse;
import com.stayprime.golf.course.GolfHole;
import com.stayprime.golf.course.Site;
import com.stayprime.device.Time;
import com.stayprime.geo.BasicMapImage;
import com.stayprime.golf.util.GolfCourseUtil;
import com.stayprime.golfapp.cart.CartState;
import com.stayprime.golfapp.cart.VisitedArea;
import com.stayprime.oncourseads.AdZone;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RoundManager handles the golf round progress and hole changing.
 * @author benjamin
 */
public class RoundManager {
    private static final Logger log = LoggerFactory.getLogger(RoundManager.class);

    /*Settings*/

    //Seconds in a reset game area (clubhouse zone) before the game state is reset.
    private int resetGameDelay_s = 300;

    //Minimum time to consider a hole played
    private int minHoleTime_s = 4 * 60;

    //Maximum seconds behind pace of play, after this the game will reset
    private final int paceOfPlayLimit_s = 3600; //1 hour

    private RoundRecordManager roundStats;

    /*Round state*/

    private RoundState round;

    private CartState cart;

    private Site golfClub;

    //RoundObserver list
    private final List<RoundObserver> observers;

    /**
     * RoundManager constructor. Initialise variables.
     */
    public RoundManager() {
        log.debug("Creating RoundManager");
        observers = new ArrayList<RoundObserver>();
        round = new RoundState();
        cart = new CartState();
    }

    public void setRoundStats(RoundRecordManager roundStats) {
        this.roundStats = roundStats;
    }

    /**
     * Set the configuration. Only resetGameDelay implemented.
     * @param config the PropertiesConfiguration object.
     */
    public void setConfig(PropertiesConfiguration config) {
        log.debug("Setting configuration...");
        resetGameDelay_s = config.getInt(CartAppConst.CONFIG_RESETGAMEDELAY, resetGameDelay_s);
        minHoleTime_s = config.getInt(CartAppConst.CONFIG_MINHOLETIME, minHoleTime_s);
    }

    public void setGolfClub(Site golfClub) {
        this.golfClub = golfClub;
    }

    /**
     * The hole that should show currently on the view.
     * Null value means that the view should show a welcome message or home view.
     * @return the hole to show on the view.
     */
    public GolfHole getViewHole() {
        return round.getViewHole();
    }

    /**
     * Current (playing) hole.
     * @return the current (playing) hole
     */
    public GolfHole getCurrentHole() {
        return round.getCurrentHole();
    }

    /*
     * Pace of play and round information
     */

    /**
     * The end of round state means that the round is finishing.
     * Currently this is detected when the cart goes into a reset area
     * (Clubhouse zone, Cartbarn zone or Parking zone).
     * This is not definitive as the cart could go back to the course.
     * The round will be reset after a specific amount of time passes.
     * @return true if the round is ending.
     */
    public boolean isEndOfRound() {
        return getCurrentHole() != null && cart.getResetGameTimer() > 0;
    }

    /**
     * Returns the current target time for the round including all played holes.
     * @return the current round target time in seconds.
     */
    public int getCurrentTargetTime() {
        if (round.playedHolesEmpty()) {
            return 0;
        } else {
            int target = 0;
            for (HoleStat h : round.getPlayedHoles()) {
                target += h.getHole().getHolePace();
            }
            return target;
        }
    }

    /**
     * Returns the total round time from the beginning of the first hole.
     * @return total round time in seconds.
     */
    public int getRoundTime() {
        if (round.playedHolesEmpty()) {
            return 0;
        } else {
            long lastTeeboxTime = round.getFirstPlayedHoleStat().getLastTeeboxTime();
            return (int) ((Time.milliTime() - lastTeeboxTime) / 1000);
        }
    }

    /**
     * Returns the current difference between the target time and the round time.
     * @return current pace of play time.
     */
    public int getCurrentPace() {
        if (round.playedHolesEmpty()) {
            return 0;
        } else {
            return getCurrentTargetTime() - getRoundTime();
        }
    }

    /**
     * Returns the pace of play recorded when the current hole was entered.
     * @return current pace of play time.
     */
    public int getLastTeePace() {
        if (round.playedHolesEmpty()) {
            return 0;
        } else {
            //If played holes is not empty, it must imply that current hole is also not null
            HoleStat currentStat = round.getCurrentHoleStat();
            int lastTeeTarget = getCurrentTargetTime() - currentStat.getHole().getHolePace();

            long firstHoleEnteredTime = round.getFirstPlayedHoleStat().getFirstEnteredTime();
            long currentHoleEnteredTime = round.getCurrentHoleStat().getFirstEnteredTime();

            int lastTeeTime = (int) ((currentHoleEnteredTime - firstHoleEnteredTime) / 1000);
            return lastTeeTarget - lastTeeTime;
        }
    }

    public Coordinates getLastTeeBoxStop() {
        return cart.getLastTeeBoxStop();
    }

    /*
     * Location information set from CartMonitor
     */

    public void setPosition(Coordinates position) {
        cart.setPosition(position);
    }

    public void setStopCount(int stopCount) {
        cart.setStopCount(stopCount);
    }

    /**
     * Set the current hole zone.
     * Depending on the state, this will trigger a hole change in this round.
     * @param holeZone the hole zone that the cart has entered.
     */
    public void setHoleZone(VisitedArea<HoleZone> holeZone) {
        boolean nullZone = holeZone == null || holeZone.getArea().getHole() == null;
        VisitedArea<HoleZone> oldZone = cart.getHoleZone();
        cart.setHoleZone(holeZone);

        //No zone entered, do nothing
        if (nullZone) {
            if (oldZone != null) {
                notifyHoleZoneLeft(oldZone.getArea());
            }
            return;
        }

        //Test if the cart was already in this same hole's zone previously.
        //This will avoid repeatedly triggering a hole change in the view
        //when one or more hole zones of this hole are entered repeatedly.
        GolfHole hole = holeZone.getArea().getHole();
        registerHoleZoneStop();
        if (hole != cart.isInHoleZone()) {
            //The cart entered a hole zone for a different hole
            log.info("Entered hole zone: " + hole);

            if (cart.getAdZone() != null && round.getCurrentHole() != null) {
                //If hole is next hole after current hole, advance hole
                boolean isNext = GolfCourseUtil.isNextInSequence(round.getCurrentHole(), hole);
                if (isNext == false) {
                    //If the cart is moving through the AdZone, ignore the adzone
                    if (cart.getStopCount() < 5) {
                        return;
                    }
                }
            }

            boolean sameCourse = round.getCurrentCourse() == hole.getGolfCourse();

            HoleStat holeEvent = new HoleStat(hole);
            setCurrentHole(holeEvent);
            //teeEntered = true;

            cart.setInHoleZone(hole);
        } else if (round.getCurrentHole() != null) {
            round.updateCurrentHoleLastTeeboxTime();
        }
    }

    private void registerHoleZoneStop() {
        if (cart.getHoleZone() != null && cart.getPosition() != null) {
//        if(round.getCurrentHole() != null) {
//            int stopCount = Application.getInstance().getGolfCartMonitor().getStopCount();

            if (cart.getStopCount() < 5) {
                //Set waiting for stop flag so that we only set the lastTeeBoxStop once
                cart.setWaitingForStop(true);
            } else if (cart.isWaitingForStop()) {
                cart.setLastTeeBoxStop(new Coordinates(cart.getPosition()));
                cart.setWaitingForStop(false);
            }
        }
    }

    /**
     * Set the current Ad zone.
     * Depending on the state, this will trigger a hole change in this round.
     * @param zone the Ad zone that the cart has entered.
     */
    public void setAdZone(VisitedArea<AdZone> zone) {
        cart.setAdZone(zone);
    }

    /**
     * Set the current clubhouse zone.
     * If the round has already started, this will trigger the endOfRound state.
     * @param zone the clubhouse zone that the cart has entered.
     */
    public void setClubhouseZone(VisitedArea<ClubhouseZone> zone) {
        if (zone != null) {
            //Clubhouse zone entered

            if (round.getState() == GolfRoundState.INITIAL) {
                //No changes to state, clear timer
                cart.setResetGameTimer(0);
            } else if (round.getState() == GolfRoundState.STARTED && cart.getResetGameTimer() == 0) {
                //The zone was just entered, set the timer to the current time
                cart.setResetGameTimer(Time.milliTime());
            }

            if (cart.getResetGameTimer() > 0) {
                //The zone was entered before, check if we should reset the round yet
                long secondsInResetZone = (Time.milliTime() - cart.getResetGameTimer()) / 1000;

                if (secondsInResetZone < resetGameDelay_s) {
                    roundFinishing();
                } else {
                    resetRound(false);
                }
            }
        } else {
            //Outside clubhouse zone, go back to STARTED state if FINISHING
            cart.setResetGameTimer(0);
            if (round.getState() == GolfRoundState.FINISHING) {
                //Didn't stay enough in the clubhouse zone to reset the game,
                //set the state back to STARTED
                setState(GolfRoundState.STARTED);
            }
        }
    }

    /*
     * Handle manual hole changes from the UI.
     */

    public void showFirstHole() {
        GolfHole firstHole = golfClub.getFirstHole();

        if (firstHole != null) {
            setCurrentHole(new HoleStat(firstHole));
        }
        //manualHoleChange(golfClub.getCourse(1).getHole(1));
    }

    public void showPreviousHole() {
        GolfHole viewHole = round.getViewHole();
        if (viewHole != null) {
            GolfCourse course = viewHole.getGolfCourse();
            int prevHole = viewHole.getNumber() == 1 ? course.getHoleCount() : viewHole.getNumber() - 1;
            manualHoleChange(course.getHole(prevHole));
        }
    }

    public void showNextHole() {
        GolfHole viewHole = round.getViewHole();
        if (viewHole != null) {
            GolfCourse course = viewHole.getGolfCourse();
            int nextHole = viewHole.getNumber() % course.getHoleCount() + 1;
            manualHoleChange(course.getHole(nextHole));
        }
    }

    /**
     * GolfHole change coming from the user interaction.
     * This could happen if the wrong hole is showing on the screen, so it may
     * really be the currently playing hole. It could also be that the user is
     * only scrolling through the holes.
     * @param hole the current hole to be set from user interaction.
     */
    private void manualHoleChange(GolfHole hole) {
        log.debug("Manual hole change: " + hole);

        round.setManualHole(new HoleStat(hole));
        checkManualHole();
    }

    /**
     * Set the manual hole to current if the cart has entered the hole area.
     * Here we should decide if the manually set hole should be considered part
     * of the round or "playing hole", or if we should ignore this hole.
     */
    private void checkManualHole() {
        if (round.getManualHole().getHole() != null) {
            if (ObjectUtils.equals(round.getManualHole().getHole(), round.getCurrentHole())) {
                //The manually set hole is the same as the current playing hole,
                //no need to keep track of it anymore, clear it and set the view hole
                round.setManualHole(new HoleStat(null));
                setViewHole(round.getCurrentHole());
            } else {
                //Check if the manually set hole should be considered the current hole.
                //These conditions need improvement.
                BasicMapImage map = round.getManualHole().getHole().getMapImage();

                //TODO Probably need to improve this condition:
                if (cart != null && map != null && map.contains(cart.getPosition())) {
                    setCurrentHole(round.getManualHole());
                }

                setViewHole(round.getManualHole().getHole());
            }
        }
    }

    /*
     * Internal state and logic
     */

    public void setDateTimeValid(boolean dateTimeValid) {
        roundStats.setDateTimeValid(dateTimeValid);
    }

    /**
     * Update internal state triggered by time event.
     * Not in use right now since the conditions need to be reviewed.
     */
    public void tick() {
        checkManualHole();
        checkTimeLimits();
    }

    private void checkTimeLimits() {
        if (getCurrentPace() < -paceOfPlayLimit_s) {
            resetRound(true);
        }
    }

    private void roundFinishing() {
        if (round.getState() != GolfRoundState.FINISHING) {
            checkHolePlayed(false);
            setState(GolfRoundState.FINISHING);
        }
    }

    /**
     * Reset round state.
     * @param roundTimedOut true if the round was ended for inactivity
     */
    public void resetRound(boolean roundTimedOut) {
        setViewHole(null);
        manualHoleChange(null);
        finalizeRound(roundTimedOut);
        round = new RoundState();
        cart = new CartState();
        notifyStateChanged();
    }

    private void finalizeRound(boolean roundTimedOut) {
        //This code should probably be in resetRound method?
        if (round.isHolePlayed()) {
            //We have played a hole and now setting hole to null
            //Confirm if the round is really finished and save it to the
            //rounds list, also increase the round counter
            if (roundStats != null) {
                roundStats.updateDate();
                roundStats.addRound(round, roundTimedOut);
            }
        } else {
            //No hole played and now setting hole to null
            //Clear the current round info and reset state
        }
        notifyRoundFinished();
    }

    /**
     * Set the hole that should show currently on the view.
     * This can be changed automatically when the cart enters a hole area,
     * or it can be changed manually by the user.
     * @param viewHole the hole to show on the view.
     */
    private void setViewHole(GolfHole viewHole) {
        log.debug("Entering setViewHole");

        if (round.getViewHole() != viewHole) {
            log.info("View hole changed: " + viewHole);
            round.setViewHole(viewHole);
            notifyViewHoleChanged();
        }
    }

    /**
     * Set the current (playing) hole, and update the round information.
     * @param holeEvent should never be null, holeEvent.hole should be null
     *                  to clear the round state.
     */
    private void setCurrentHole(HoleStat holeEvent) {
        log.debug("Entering setCurrentHole");
        GolfHole oldHole = round.getCurrentHole();
        GolfHole newHole = holeEvent.getHole();

        if (newHole == null) {
            throw new IllegalArgumentException("Can't set current hole to null");
        }

        if (ObjectUtils.notEqual(oldHole, newHole)) {
            log.info("Current hole changed: " + newHole);

            cart.setLastTeeBoxStop(null);
            cart.setWaitingForStop(true);

            checkHolePlayed(true);

            //Set current hole before any events are dispatched
            round.setCurrentHole(holeEvent);
            setState(GolfRoundState.STARTED);
            notifyCurrentHoleChanged();
        }

        //Moved this to the end, current hole event propagation has priority
        setViewHole(newHole);
    }

    private void checkHolePlayed(boolean removeIfNotPlayed) {
        HoleStat currentHole = round.getCurrentHoleStat();
        if (currentHole != null) {
            if (currentHole.isHolePlayed() == false) {
                if (wasHolePlayed(currentHole.getFirstEnteredTime())) {
                    currentHole.setHolePlayed();
                    newHolePlayed(currentHole);
                } else if (removeIfNotPlayed) {
                    //Remove round and pace of play from Round
                    //playedHoles shouldn't be empty because round.getCurrentHole() != null
                    round.removeCurrentHole();
                }
            } else {
                currentHole.updateLastHoleTime();
            }
        }
    }

    /**
     * Check if the given hole was played based on the minimum play time.
     * @return true if the hole was played
     */
    private boolean wasHolePlayed(long timestamp) {
        //Minimum time a hole should take, currently fixed to 4 minutes
        //Later it should depend on the hole par, number of balls and so on
        long playTime = Time.milliTime() - timestamp;
        return TimeUnit.MILLISECONDS.toSeconds(playTime) > minHoleTime_s;
    }

    private void newHolePlayed(HoleStat playedHole) {
        round.setHolePlayed();

        notifyHolePlayed(playedHole);

        if (roundStats != null) {
            roundStats.updateDate();

            //        long secondsPlayed = TimeUnit.MILLISECONDS.toSeconds(Time.milliTime() - playedHole.timestamp);
            roundStats.addPlayedHole();

            if (round.getPlayedHolesCount() % 9 == 0) {
                roundStats.addPlayed9HoleRound();
            }

            if (round.getPlayedHolesCount() % 18 == 0) {
                roundStats.addPlayed18HoleRound();
            }
        }
    }

    /*
     * Observer support
     */

    public void addRoundObserver(RoundObserver observer) {
        assert observer != null;
        observers.add(observer);
    }

    public void removeRoundObserver(RoundObserver observer) {
        assert observer != null;
        observers.remove(observer);
    }

    private void setState(GolfRoundState state) {
        GolfRoundState oldState = round.getState();
        round.setState(state);

        if (oldState != state) {
            log.info("Setting state to " + state);
            notifyStateChanged();
        }
    }

    private void notifyStateChanged() {
        for (RoundObserver o : observers) {
            o.roundStateChanged(round);
        }
    }

    private void notifyRoundFinished() {
        for (RoundObserver o : observers) {
            o.roundFinished();
        }
    }

    private void notifyCurrentHoleChanged() {
        for (RoundObserver o : observers) {
            o.currentHoleChanged(round.getCurrentHole());
        }
    }

    private void notifyViewHoleChanged() {
        for (RoundObserver o : observers) {
            o.viewHoleChanged(round.getViewHole());
        }
    }

    private void notifyHolePlayed(HoleStat playedHole) {
        for (RoundObserver o : observers) {
            o.holePlayed(playedHole.getHole());
        }
    }

    private void notifyHoleZoneLeft(HoleZone area) {
        if (area != null && area.getHole() != null) {
            GolfHole hole = area.getHole();
            for (RoundObserver o : observers) {
                o.holeZoneLeft(hole);
            }
        }
    }

    @Override
    public String toString() {
        return "Round state: " + round.getState().toString() +
                (round.getState() == GolfRoundState.INITIAL ? "" :
                        ", Course: " + getCurrentHole().getGolfCourse().getName() +
                                ", hole " + getCurrentHole().getNumber());
    }

    public static interface RoundObserver {
        public void viewHoleChanged(GolfHole hole);

        public void currentHoleChanged(GolfHole hole);

        public void holeZoneLeft(GolfHole hole);

        public void holePlayed(GolfHole hole);

        public void roundStateChanged(RoundState round);

        public void roundFinished();
    }

}
