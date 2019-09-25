/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.aeben.golfcourse.objects;

import com.stayprime.geo.Coordinates;
import com.aeben.golfcourse.elements.AbstractArea;
import com.aeben.golfcourse.elements.CourseElement;
import com.aeben.golfcourse.elements.CourseElementVisitor;
import com.aeben.golfcourse.elements.ObjectType;
import com.aeben.golfcourse.elements.Warning;
import com.aeben.golfcourse.elements.WarningZone;

/**
 *
 * @author benjamin
 */
public class WarningArea extends AbstractArea implements WarningZone {
    private Warning warning;
    private boolean useCartKill = false;

    public WarningArea(CourseElement parent, ObjectType type, Integer id, String name) {
	super(parent, type, id, name);
    }

    public void setWarning(Warning warning) {
	this.warning = warning;
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
    }

    public void accept(CourseElementVisitor visitor) {
	visitor.visitCourseElement(this);
    }
}
