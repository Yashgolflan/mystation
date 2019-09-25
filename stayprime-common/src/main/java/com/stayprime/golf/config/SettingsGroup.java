/*
 * 
 */
package com.stayprime.golf.config;

import com.stayprime.golf.config.settings.Setting;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.commons.configuration.PropertiesConfiguration;

/**
 * Base class to group Settings, provides methods to handle them in batches.
 * @author benjamin
 */
public abstract class SettingsGroup {

    private List<Setting> settings = Collections.EMPTY_LIST;
    protected final PropertiesConfiguration config;

    public SettingsGroup() {
        config = new PropertiesConfiguration();
    }

    public SettingsGroup(PropertiesConfiguration config) {
        this.config = config;
    }

    public final List<Setting> getSettings() {
        return settings;
    }

    protected void setSettings(Setting ... settingsList) {
        settings = Collections.unmodifiableList(Arrays.asList(settingsList));
        setConfig();
    }

    protected void setSettings(List<Setting> settingsList) {
        settings = Collections.unmodifiableList(settingsList);
        setConfig();
    }

    private void setConfig() {
        for (Setting s: settings) {
            s.setConfig(config);
        }
    }

    public void loadConfig() {
        for (Setting s: settings) {
            s.loadConfig();
        }
    }
}
