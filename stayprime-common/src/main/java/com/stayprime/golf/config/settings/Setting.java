/*
 * 
 */
package com.stayprime.golf.config.settings;

import org.apache.commons.configuration.PropertiesConfiguration;

/*
 * Configuration keys and properties
 */
public class Setting {
    public final String key;
    private String value;
    private PropertiesConfiguration config;

    /**
     * Create an instance of a setting named by a key String.
     * The initial value will be null.
     * @param key the String identifier for this setting in a config file.
     */
    public Setting(String key) {
        this.key = key;
    }

    /**
     * Create an instance of a setting named by a key String and initial value.
     * @param key the String identifier for this setting in a config file.
     * @param value the initial value of this setting.
     */
    public Setting(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public void setConfig(PropertiesConfiguration config) {
        this.config = config;
    }

    public void loadConfig() {
        set(config.getString(key));
    }

    public void loadConfig(PropertiesConfiguration config) {
        set(config.getString(key));
    }

    public void saveConfig() {
        if (config != null) {
            config.setProperty(key, value);
        }
    }

    public String get() {
        return value;
    }

    public void set(String value) {
        this.value = value;
        saveConfig();
    }

}
