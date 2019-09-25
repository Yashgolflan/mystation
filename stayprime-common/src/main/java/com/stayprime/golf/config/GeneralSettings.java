/*
 * 
 */
package com.stayprime.golf.config;

import com.stayprime.golf.config.settings.CartAppModesSetting;
import com.stayprime.golf.config.settings.Setting;

/**
 *
 * @author benjamin
 */
public class GeneralSettings extends SettingsGroup {
    public final Setting timeZone = new Setting("timeZone");
    public final CartAppModesSetting cartAppModes = new CartAppModesSetting("cartAppModes");

    public GeneralSettings() {
        setSettings(timeZone, cartAppModes);
    }

}
