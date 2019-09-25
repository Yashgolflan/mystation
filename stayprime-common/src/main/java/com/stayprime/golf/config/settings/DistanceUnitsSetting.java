/*
 * 
 */
package com.stayprime.golf.config.settings;

import com.aeben.golfcourse.util.DistanceUnits;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author benjamin
 */
public class DistanceUnitsSetting extends Setting {
    public static final String key = "distanceUnits";

    private List<DistanceUnits> distanceUnitsList;

    private final String initialValue;

    private int selected = -1;

    public DistanceUnitsSetting(String value) {
        super(key);
        set(value, true);
        this.initialValue = value;
    }

    @Override
    public final void set(String value) {
        set(value, false);
    }

    public final void set(String value, boolean strict) {
        try {
            String[] unitsSplit = StringUtils.split(value, ',');
            DistanceUnits[] units = new DistanceUnits[unitsSplit.length];
            for (int i = 0; i < unitsSplit.length; i++) {
                units[i] = DistanceUnits.get(unitsSplit[i].trim());
            }
            distanceUnitsList = Collections.unmodifiableList(Arrays.asList(units));
            super.set(value);
            reset();
        }
        catch (RuntimeException ex) {
            if (strict) {
                throw ex;
            }
        }
    }

    public String getInitialValue() {
        return initialValue;
    }

    public List<DistanceUnits> getDistanceUnitsList() {
        return distanceUnitsList;
    }

    public DistanceUnits getDefault() {
        return distanceUnitsList.get(0);
    }

    public DistanceUnits getSelected() {
        return selected >= 0 ? distanceUnitsList.get(selected) : null;
    }

    public DistanceUnits selectNext() {
        if (selected >= 0) {
            selected = (selected + 1) % distanceUnitsList.size();
        }
        return getSelected();
    }

    public DistanceUnits reset() {
        selected = distanceUnitsList.isEmpty()? -1 : 0;
        return getSelected();
    }

    public void read(PropertiesConfiguration config) {
        if (config != null) {
            set(config.getString(key, initialValue));
        }
    }

}
