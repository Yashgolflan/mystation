/*
 * 
 */

package com.aeben.golfcourse.cart;

import com.aeben.golfcourse.util.DistanceUnits;
import com.stayprime.golf.course.GolfHole;
import com.stayprime.golf.course.Site;

/**
 *
 * @author benjamin
 */
public interface GameStatus {
    public Site getGolfClub();
    public GolfHole getPlayingHole();
    public boolean isEndOfRound();
    public Integer getPaceOfPlaySeconds();
    public Integer getPaceOfPlayOnHoleStart();
    public Integer getPaceOfPlayTarget();
    public DistanceUnits getDistanceUnits();

    public void addGameStatusObserver(Observer o);
    public void removeGameStatusObserver(Observer o);

    public interface Observer {
	public void gameStatusChanged(GameStatus gameStatus);
	public void gameStatusReset(GameStatus gameStatus);
	public void golfClubChanged(GameStatus gameStatus);
	public void distanceUnitsChanged(GameStatus gameStatus);
    }
}
