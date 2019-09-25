/*
 * 
 */
package com.stayprime.golfapp.cart;

import com.stayprime.geo.Coordinates;
import com.stayprime.golf.course.GolfHole;
import com.stayprime.golf.course.objects.HoleZone;
import com.stayprime.oncourseads.AdZone;

/**
 *
 * @author benjamin
 */
public class CartState {
    
    //When the cart enters a hole zone, we keep track of the hole.
    //Must be null if the cart is not inside a hole zone.
    private GolfHole inHoleZone;

    //Last position where the cart stopped inside a hole zone for drive distance
    private Coordinates lastTeeBoxStop;

    private boolean waitingForStop = true;

    //Timer to reset the game when the cart enters a reset area (clubhouse Zone).
    //The value is zero when the cart hasn't entered a reset area.
    private long resetGameTimer = 0;

    //The current AdZone when the cart is inside one
    private VisitedArea<AdZone> adZone;

    private VisitedArea<HoleZone> holeZone;

    private Coordinates position;

    private int stopCount;

    public GolfHole isInHoleZone() {
        return inHoleZone;
    }

    public void setInHoleZone(GolfHole inHoleZone) {
        this.inHoleZone = inHoleZone;
    }

    public Coordinates getLastTeeBoxStop() {
        return lastTeeBoxStop;
    }

    public void setLastTeeBoxStop(Coordinates lastTeeBoxStop) {
        this.lastTeeBoxStop = lastTeeBoxStop;
    }

    public boolean isWaitingForStop() {
        return waitingForStop;
    }

    public void setWaitingForStop(boolean waitingForStop) {
        this.waitingForStop = waitingForStop;
    }

    public long getResetGameTimer() {
        return resetGameTimer;
    }

    public void setResetGameTimer(long resetGameTimer) {
        this.resetGameTimer = resetGameTimer;
    }

    public VisitedArea<AdZone> getAdZone() {
        return adZone;
    }

    public void setAdZone(VisitedArea<AdZone> adZone) {
        this.adZone = adZone;
    }

    public VisitedArea<HoleZone> getHoleZone() {
        return holeZone;
    }

    public void setHoleZone(VisitedArea<HoleZone> holeZone) {
        this.holeZone = holeZone;
    }

    public Coordinates getPosition() {
        return position;
    }

    public void setPosition(Coordinates position) {
        this.position = position;
    }

    public int getStopCount() {
        return stopCount;
    }

    public void setStopCount(int stopCount) {
        this.stopCount = stopCount;
    }

}
