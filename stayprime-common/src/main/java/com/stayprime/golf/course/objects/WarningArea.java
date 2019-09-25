/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.stayprime.golf.course.objects;

import com.stayprime.golf.objects.BasicArea;
import com.stayprime.golf.objects.Warning;
import com.stayprime.geo.Coordinates;
import com.stayprime.golf.objects.AbstractFeature;
import com.stayprime.golf.objects.GeomType;
import com.stayprime.golf.objects.WarningLevel;
import org.apache.commons.lang.math.NumberUtils;

/**
 *
 * @author benjamin
 */
public class WarningArea extends BasicArea {
    private Warning warning;
    private boolean useCartKill = false;

    public WarningArea(ObjectType type, Integer id, String name) {
	super(type, GeomType.POLY, name);
    }

    public WarningArea(AbstractFeature f) {
	super(f.getType(), GeomType.POLY, f.getName());
        setProperties(f.getProperties());
    }

    public void setWarning(Warning warning) {
	this.warning = warning;
        if (warning != null) {
            setPropertyString("warning.level", String.valueOf(warning.getLevel().id));
            setPropertyString("warning.message", warning.getMessage());
            setPropertyString("warning.subMessage", warning.getSubMessage());
        }
        else {
            properties.remove("warning.level");
            properties.remove("warning.message");
            properties.remove("warning.subMessage");
        }
    }

    public Warning getWarning() {
	return warning;
    }

    public Warning getWarning(Coordinates coord) {
	if(contains(coord))
	    return warning;
	else
	    return null;
    }

    public boolean isUseCartKill() {
	return useCartKill;
    }

    public void setUseCartKill(boolean useCartKill) {
	this.useCartKill = useCartKill;
        properties.setProperty("useCartKill", String.valueOf(useCartKill));
    }

    @Override
    public void setProperties(String props) {
        super.setProperties(props); //To change body of generated methods, choose Tools | Templates.
        setProperties();
    }

    private void setProperties() {
        setUseCartKill(Boolean.valueOf(properties.getProperty("useCartKill")));

        int level = NumberUtils.toInt(properties.getProperty("warning.level"), 0);
        String message = properties.getProperty("warning.message");
        String subMessage = properties.getProperty("warning.subMessage");
        setWarning(new Warning(WarningLevel.getLevel(level), message, subMessage));
    }

}
