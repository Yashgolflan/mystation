/*
 * 
 */
package com.stayprime.golf.config;

import com.stayprime.golf.config.settings.Setting;

/**
 *
 * @author benjamin
 */
public class MobileSettings extends SettingsGroup {
    public final Setting serverSettings = new Setting("serverSettings");
    public final Setting apnSettings = new Setting("apnSettings");
    public final Setting networkOperator = new Setting("networkOperator");
    public final Setting networkCode = new Setting("networkCode");
    public final Setting networkSpec = new Setting("networkSpec");

    public MobileSettings() {
        setSettings(serverSettings, apnSettings,
                networkOperator, networkCode, networkSpec);
    }

}
