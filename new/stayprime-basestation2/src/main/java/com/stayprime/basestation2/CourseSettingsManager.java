/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.stayprime.basestation2;

import com.stayprime.basestation2.services.CourseService;
import com.stayprime.basestation2.util.NotificationPopup;
import com.stayprime.hibernate.entities.CourseSettings;
import com.stayprime.hibernate.entities.MenuItems;
import com.stayprime.storage.util.LocalStorage;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.List;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author benjamin
 */
public class CourseSettingsManager {
    private static final Logger log = LoggerFactory.getLogger(CourseSettingsManager.class);

    public static final String PROP_WEATHERALERT = "WeatherAlert";
    public static final String PROP_CARTPATHONLY = "CartPathOnly";
    public static final String PROP_GLOBALPACEOFPLAYADDSECONDS = "GlobalPaceOfPlayAddSeconds";
    public static final String PROP_GLOBALPACEOFPLAYDISABLE = "GlobalPaceOfPlayDisable";
    public static final String PROP_PACEOFPLAYSTARTONALLHOLES = "PaceOfPlayStartOnAllHoles";
    public static final String PROP_PACEOFPLAYSTARTONHOLES = "PaceOfPlayStartOnHoles";
    public static final String PROP_PACEOFPLAYTHRESHOLD = "PaceOfPlayThreshold";
    public static final String PROP_HOLEZONEDELAY = "HoleZoneDelay";
    public static final String PROP_GRIDSYSTEMNUMBEROFGRIDS = "GridSystemNumberOfGrids";
    public static final String PROP_GRIDSYSTEMCURRENTGRID = "GridSystemCurrentGrid";
    public static final String PROP_BACKLIGHTOFF = "BacklightOff";

    private final LocalStorage localStorage;

    private final Properties settings;
    
    private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    @Autowired
    CourseService courseService;

    public CourseSettingsManager(LocalStorage localStorage) {
        this.localStorage = localStorage;
        settings = new Properties(getDefaultSettings());
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    public boolean getWeatherAlert() {
        return String.valueOf(true).equals(getProperty(PROP_WEATHERALERT));
    }

    public void setWeatherAlert(boolean weatherAlert) {
        saveSetting(PROP_WEATHERALERT, Boolean.toString(weatherAlert));

        boolean oldWeatherAlert = getWeatherAlert();
        settings.put(PROP_WEATHERALERT, String.valueOf(weatherAlert));
        propertyChangeSupport.firePropertyChange(PROP_WEATHERALERT, oldWeatherAlert, weatherAlert);
    }
    
    public void setWeatherAlertModified(boolean weatherAlert) {
        boolean oldWeatherAlert = getWeatherAlert();
        if (oldWeatherAlert != weatherAlert) {
            settings.put(PROP_WEATHERALERT, String.valueOf(weatherAlert));
            propertyChangeSupport.firePropertyChange(PROP_WEATHERALERT, oldWeatherAlert, weatherAlert);
        }
    }

    public boolean getCartPathOnly() {
        return String.valueOf(true).equals(getProperty(PROP_CARTPATHONLY));
    }

    public void setCartPathOnly(boolean cartPathOnly) {
        saveSetting(PROP_CARTPATHONLY, Boolean.toString(cartPathOnly));

        boolean oldWeatherAlert = getCartPathOnly();
        settings.put(PROP_CARTPATHONLY, String.valueOf(cartPathOnly));
        propertyChangeSupport.firePropertyChange(PROP_CARTPATHONLY, oldWeatherAlert, cartPathOnly);
    }
    
    public void setCartPathOnlyModified(boolean cartPathOnly) {
        boolean oldCartPathOnly = getCartPathOnly();
        if (oldCartPathOnly != cartPathOnly) {
            settings.put(PROP_CARTPATHONLY, String.valueOf(cartPathOnly));
            propertyChangeSupport.firePropertyChange(PROP_CARTPATHONLY, oldCartPathOnly, cartPathOnly);
        }
    }

    public boolean getGlobalPaceOfPlayDisable() {
        return String.valueOf(true).equals(getProperty(PROP_GLOBALPACEOFPLAYDISABLE));
    }

    public void setGlobalPaceOfPlayDisable(boolean GlobalPaceOfPlayDisable) {
        saveSetting(PROP_GLOBALPACEOFPLAYDISABLE, Boolean.toString(GlobalPaceOfPlayDisable));

        boolean oldGlobalPaceOfPlayDisable = getGlobalPaceOfPlayDisable();
        settings.put(PROP_GLOBALPACEOFPLAYDISABLE, String.valueOf(GlobalPaceOfPlayDisable));
        propertyChangeSupport.firePropertyChange(PROP_GLOBALPACEOFPLAYDISABLE, oldGlobalPaceOfPlayDisable, GlobalPaceOfPlayDisable);
    }

    public int getGlobalPaceOfPlayAddSeconds() {
        return Integer.parseInt(getProperty(PROP_GLOBALPACEOFPLAYADDSECONDS));
    }

    public void setGlobalPaceOfPlayAddSeconds(int GlobalPaceOfPlayAddSeconds) {
        saveSetting(PROP_GLOBALPACEOFPLAYADDSECONDS, Integer.toString(GlobalPaceOfPlayAddSeconds));

        int oldGlobalPaceOfPlayAddSeconds = getGlobalPaceOfPlayAddSeconds();
        settings.put(PROP_GLOBALPACEOFPLAYADDSECONDS, String.valueOf(GlobalPaceOfPlayAddSeconds));
        propertyChangeSupport.firePropertyChange(PROP_GLOBALPACEOFPLAYADDSECONDS, oldGlobalPaceOfPlayAddSeconds, GlobalPaceOfPlayAddSeconds);
    }

    public boolean getPaceOfPlayStartOnAllHoles() {
        return String.valueOf(true).equals(getProperty(PROP_PACEOFPLAYSTARTONALLHOLES));
    }

    public void setPaceOfPlayStartOnAllHoles(boolean PaceOfPlayStartOnAllHoles) {
        saveSetting(PROP_PACEOFPLAYSTARTONALLHOLES, Boolean.toString(PaceOfPlayStartOnAllHoles));

        boolean oldPaceOfPlayStartOnAllHoles = getPaceOfPlayStartOnAllHoles();
        settings.put(PROP_PACEOFPLAYSTARTONALLHOLES, String.valueOf(PaceOfPlayStartOnAllHoles));
        propertyChangeSupport.firePropertyChange(PROP_PACEOFPLAYSTARTONALLHOLES, oldPaceOfPlayStartOnAllHoles, PaceOfPlayStartOnAllHoles);
    }

    public String getPaceOfPlayStartOnHoles() {
        return getProperty(PROP_PACEOFPLAYSTARTONHOLES);
    }

    public void setPaceOfPlayStartOnHoles(String PaceOfPlayStartOnHoles) {
        saveSetting(PROP_PACEOFPLAYSTARTONHOLES, PaceOfPlayStartOnHoles);

        String oldPaceOfPlayStartOnHoles = getPaceOfPlayStartOnHoles();
        settings.put(PROP_PACEOFPLAYSTARTONHOLES, String.valueOf(PaceOfPlayStartOnHoles));
        propertyChangeSupport.firePropertyChange(PROP_PACEOFPLAYSTARTONHOLES, oldPaceOfPlayStartOnHoles, PaceOfPlayStartOnHoles);
    }

    public String getPaceOfPlayThreshold() {
        return getProperty(PROP_PACEOFPLAYTHRESHOLD);
    }

    public void setPaceOfPlayThreshold(String PaceOfPlayThreshold) {
        saveSetting(PROP_PACEOFPLAYTHRESHOLD, PaceOfPlayThreshold);

        String oldPaceOfPlayThreshold = getPaceOfPlayThreshold();
        settings.put(PROP_PACEOFPLAYTHRESHOLD, PaceOfPlayThreshold);
        propertyChangeSupport.firePropertyChange(PROP_PACEOFPLAYTHRESHOLD, oldPaceOfPlayThreshold, PaceOfPlayThreshold);
    }

    public int getCommunicationLostTimeout() {
        return 660;
    }

    public int getHoleZoneDelay() {
        return Integer.parseInt(getProperty(PROP_HOLEZONEDELAY));
    }

    public void setHoleZoneDelay(int HoleZoneDelay) {
        saveSetting(PROP_HOLEZONEDELAY, Integer.toString(HoleZoneDelay));

        int oldHoleZoneDelay = getHoleZoneDelay();
        settings.put(PROP_HOLEZONEDELAY, String.valueOf(HoleZoneDelay));
        propertyChangeSupport.firePropertyChange(PROP_HOLEZONEDELAY, oldHoleZoneDelay, HoleZoneDelay);
    }

    public int getGridSystemNumberOfGrids() {
        return Integer.parseInt(getProperty(PROP_GRIDSYSTEMNUMBEROFGRIDS));
    }

    public void setGridSystemNumberOfGrids(int GridSystemNumberOfGrids) {
        saveSetting(PROP_GRIDSYSTEMNUMBEROFGRIDS, Integer.toString(GridSystemNumberOfGrids));

        int oldGridSystemNumberOfGrids = getGridSystemNumberOfGrids();
        settings.put(PROP_GRIDSYSTEMNUMBEROFGRIDS, String.valueOf(GridSystemNumberOfGrids));
        propertyChangeSupport.firePropertyChange(PROP_GRIDSYSTEMNUMBEROFGRIDS, oldGridSystemNumberOfGrids, GridSystemNumberOfGrids);
    }

    public int getGridSystemCurrentGrid() {
        return Integer.parseInt(getProperty(PROP_GRIDSYSTEMCURRENTGRID));
    }

    public void setGridSystemCurrentGrid(int GridSystemCurrentGrid) {
        saveSetting(PROP_GRIDSYSTEMCURRENTGRID, Integer.toString(GridSystemCurrentGrid));

        int oldGridSystemCurrentGrid = getGridSystemCurrentGrid();
        settings.put(PROP_GRIDSYSTEMCURRENTGRID, String.valueOf(GridSystemCurrentGrid));
        propertyChangeSupport.firePropertyChange(PROP_GRIDSYSTEMCURRENTGRID, oldGridSystemCurrentGrid, GridSystemCurrentGrid);
    }
    
    public void setGridSystemCurrentGridModified(int GridSystemCurrentGrid) {
        int oldGridSystemCurrentGrid = getGridSystemCurrentGrid();
        if (oldGridSystemCurrentGrid != GridSystemCurrentGrid) {
            log.info("New is {} and old is {}", GridSystemCurrentGrid, oldGridSystemCurrentGrid);
            settings.put(PROP_GRIDSYSTEMCURRENTGRID, String.valueOf(GridSystemCurrentGrid));
            propertyChangeSupport.firePropertyChange(PROP_GRIDSYSTEMCURRENTGRID, oldGridSystemCurrentGrid, GridSystemCurrentGrid);
        }
    }

    public boolean getBacklightOff() {
        return String.valueOf(true).equals(getProperty(PROP_BACKLIGHTOFF));
    }

    public void setBacklightOff(boolean backlightOff) {
        saveSetting(PROP_BACKLIGHTOFF, Boolean.toString(backlightOff));

        boolean oldBacklightOff = getBacklightOff();
        settings.put(PROP_BACKLIGHTOFF, String.valueOf(backlightOff));
        propertyChangeSupport.firePropertyChange(PROP_BACKLIGHTOFF, oldBacklightOff, backlightOff);
    }


    public synchronized String getProperty(String key) {
        return settings.getProperty(key);
    }

    public synchronized void setProperty(String key, String value) {
        boolean result = saveSetting(key, value);
        if (result) {
            putProperty(key, value);
        }        
    }

    private void putProperty(String key, String value) {
        String oldValue = getProperty(key);
        settings.put(key, value);
        propertyChangeSupport.firePropertyChange(key, oldValue, value);
    }

    public void readAllSettings() {
        try {
            List<CourseSettings> courseSettings = localStorage.listCourseSettings();
            for (CourseSettings setting : courseSettings) {
                putProperty(setting.getName(), setting.getValue());
            }
        }
        catch (Exception ex) {
            log.error(ex.toString());
        }
    }

    private Properties getDefaultSettings() {
        Properties defaultSettings = new Properties();
        defaultSettings.put(PROP_CARTPATHONLY, String.valueOf(false));
        defaultSettings.put(PROP_WEATHERALERT, String.valueOf(false));
        defaultSettings.put(PROP_GLOBALPACEOFPLAYADDSECONDS, "0");
        defaultSettings.put(PROP_GLOBALPACEOFPLAYDISABLE, String.valueOf(false));
        defaultSettings.put(PROP_PACEOFPLAYSTARTONALLHOLES, String.valueOf(true));
        defaultSettings.put(PROP_PACEOFPLAYSTARTONHOLES, "");
        defaultSettings.put(PROP_PACEOFPLAYTHRESHOLD, "0,-600");
        defaultSettings.put(PROP_HOLEZONEDELAY, "30");
        defaultSettings.put(PROP_GRIDSYSTEMNUMBEROFGRIDS, "0");
        defaultSettings.put(PROP_GRIDSYSTEMCURRENTGRID, "0");

        return defaultSettings;
    }

    private boolean saveSetting(String key, String value) {
        if (courseService != null) {
            courseService.saveSetting(key, value);
            return true;
        }
        else {
            NotificationPopup.showErrorPopup(null);
            return false;
        }
    }
    
    public LocalStorage getLocalStorage() {
        return localStorage;
    }
}
