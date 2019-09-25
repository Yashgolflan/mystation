/*
 * 
 */
package com.stayprime.golf.config;

import com.stayprime.golf.config.settings.ModeSetting;
import com.stayprime.golf.config.settings.Mode;
import com.stayprime.golf.config.settings.ScorecardSetting;
import com.stayprime.golf.config.settings.Setting;
import com.stayprime.golf.config.settings.DistanceUnitsSetting;

/**
 *
 * @author benjamin
 */
public class GolfAppSettings extends SettingsGroup {

    public final Setting welcomeImage = new Setting("welcomeImage");

    public final Setting thankyouImage = new Setting("thankyouImage");

    public final DistanceUnitsSetting distanceUnits = new DistanceUnitsSetting("yd,m");

    public final Setting siLabel = new Setting("siLabel");

    public final Setting fullscreedAdSpeed = new Setting("fullscreedAdSpeed");

    public final Setting rangerCall = new Setting("rangerCall");

    public final Setting emergencyCall = new Setting("emergencyCall");

    public final Setting fnbCartCall = new Setting("fnbCartCall");

    /**
     * Comma separated options: disable|enable,auto,email,print.
     * Default is disable. Auto means automatic scorecard, which will ask the
     * user if he wants to use the scorecard and pop up automatically.
     */
    public final ScorecardSetting scorecard = new ScorecardSetting("scorecard", Mode.disable.name());

    /**
     * F&B menu options: disable|enable|auto.
     * Default is disable. Auto means menu will pop up automatically at some places.
     */
    public final ModeSetting fnb = new ModeSetting("fnb", Mode.disable.name());


    public GolfAppSettings() {
        setSettings(welcomeImage, thankyouImage, distanceUnits, siLabel,
                fullscreedAdSpeed, rangerCall, emergencyCall, fnbCartCall, scorecard, fnb);
    }

}
