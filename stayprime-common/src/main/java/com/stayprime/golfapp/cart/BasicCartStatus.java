/*
 * 
 */

package com.stayprime.golfapp.cart;

import com.stayprime.golf.course.objects.ClubhouseZone;
import com.stayprime.golf.objects.Warning;
import com.stayprime.oncourseads.AdZone;

/**
 *
 * @author benjamin
 */
public class BasicCartStatus {
    private boolean inRestrictedZone;
    private boolean inCartPathOnlyZone;
    private boolean outOfCartPath;
    private Warning activeWarning;
    private ClubhouseZone clubhouseZone;
    private AdZone adZone;

    private boolean weatherAlertActive;
    private boolean cartPathOnlyActive;

    public BasicCartStatus() {
    }

    public boolean isInRestrictedZone() {
        return inRestrictedZone;
    }

    public void setInRestrictedZone(boolean inRestrictedZone) {
        this.inRestrictedZone = inRestrictedZone;
    }

    public boolean isInCartPathOnlyZone() {
        return inCartPathOnlyZone;
    }

    public void setInCartPathOnlyZone(boolean inCartPathOnlyZone) {
        this.inCartPathOnlyZone = inCartPathOnlyZone;
    }

    public boolean isOutOfCartPath() {
        return outOfCartPath;
    }

    public void setOutOfCartPath(boolean outOfCartPath) {
        this.outOfCartPath = outOfCartPath;
    }

    public Warning getActiveWarning() {
        return activeWarning;
    }

    public void setActiveWarning(Warning activeWarning) {
        this.activeWarning = activeWarning;
    }

    public boolean isInClubhouseZone() {
	return clubhouseZone != null;
    }

    public ClubhouseZone getClubhouseZone() {
        return clubhouseZone;
    }

    public void setClubhouseZone(ClubhouseZone clubhouseZone) {
        this.clubhouseZone = clubhouseZone;
    }

    public boolean isInAdZone() {
	return adZone != null;//inAdZone;
    }

    public AdZone getAdZone() {
        return adZone;
    }

    public void setAdZone(AdZone adZone) {
        this.adZone = adZone;
    }

    public boolean isWeatherAlertActive() {
        return weatherAlertActive;
    }

    public void setWeatherAlertActive(boolean weatherAlertActive) {
        this.weatherAlertActive = weatherAlertActive;
    }

    public boolean isCartPathOnlyActive() {
        return cartPathOnlyActive;
    }

    public void setCartPathOnlyActive(boolean cartPathOnlyActive) {
        this.cartPathOnlyActive = cartPathOnlyActive;
    }

    public void set(BasicCartStatus s) {
        inRestrictedZone = s.inRestrictedZone;
        inCartPathOnlyZone = s.inCartPathOnlyZone;
        outOfCartPath = s.outOfCartPath;
        activeWarning = s.activeWarning;
        clubhouseZone = s.clubhouseZone;
        adZone = s.adZone;
        weatherAlertActive = s.weatherAlertActive;
        cartPathOnlyActive = s.cartPathOnlyActive;
    }

}
