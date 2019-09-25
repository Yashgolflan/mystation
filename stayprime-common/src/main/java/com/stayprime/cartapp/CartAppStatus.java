/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stayprime.cartapp;

import com.stayprime.golf.course.objects.WarningArea;
import com.stayprime.golf.objects.Warning;
import com.stayprime.model.golf.CartAppMode;

/**
 *
 * @author stayprime
 */
public interface CartAppStatus {

    public CartAppMode getMode();

    public boolean isAccessibleMode();

    public void setAccessibleMode(boolean b);

    public boolean isInRestrictedZone();

    public boolean isWeatherAlertEnabled();

    public void setWeatherAlertEnabled(boolean b);

    public boolean isCartPathOnlyEnabled();

    public void setCartPathOnlyEnabled(boolean b);

    public double getCartPathWarningDistance();

    public boolean isInCartPathViolation();

    public void setInCartPathArea(boolean inAnyCartPathArea);

    public void setCartPathOnlyHole(boolean inAnyCartPathOnlyHole);

    public void setDistanceToCartPath(double distanceToAnyCartPath);

    public void setOnCartPath(boolean onAnyCartPath);

    public Warning getActiveWarning();

    public void setWarningZone(WarningArea warningArea);

    public void setRestrictedZone(WarningArea warningArea);

    public void updateWarnings(boolean goodGpsSignal);

}
