/*
 *
 */
package com.stayprime.golf.config;

import com.stayprime.golf.config.settings.Setting;
import java.io.File;
import java.util.ArrayList;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

/**
 *
 * @author benjamin
 */
public class CourseSettings extends SettingsGroup {
    public static final String defaultFileName = "course.config";

    private CourseSettingsListener courseSettingsListener;

    public final Setting updated = new Setting("updated");

    public final GeneralSettings general = new GeneralSettings();

    public final GolfAppSettings golfApp = new GolfAppSettings();

    public final MobileSettings network = new MobileSettings();

    /*
     * Constructor and public methods
     */

    public CourseSettings(File file) {
        super(new PropertiesConfiguration());
        config.setDelimiterParsingDisabled(true);
        config.setFile(file);

        int size = 1
                + general.getSettings().size()
                + golfApp.getSettings().size()
                + network.getSettings().size();

        ArrayList<Setting> settings = new ArrayList<Setting>(size);
        settings.add(updated);
        settings.addAll(general.getSettings());
        settings.addAll(golfApp.getSettings());
        settings.addAll(network.getSettings());

        setSettings(settings);
    }

    public void setCourseSettingsListener(CourseSettingsListener courseSettingsListener) {
        this.courseSettingsListener = courseSettingsListener;
    }

    public void setAutoSave(boolean autoSave) {
        config.setAutoSave(autoSave);
    }

    public File getFile() {
        return config.getFile();
    }

    public CourseSettings load() throws ConfigurationException {
        config.load();
        loadConfig();
        return this;
    }

    public void notifyListeners() {
        if (courseSettingsListener != null) {
            courseSettingsListener.courseSettingsUpdated(this);
        }
    }

    public void save() throws ConfigurationException {
        config.save();
    }
}
