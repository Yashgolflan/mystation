/*
 *
 */

package com.stayprime.golf.objects;

import com.stayprime.golf.course.objects.ObjectType;

/**
 *
 * @author benjamin
 */
public class BasicArea extends AbstractFeature {

    public BasicArea() {
        this(ObjectType.UNKNOWN, GeomType.POLY, null);
    }

    public BasicArea(ObjectType type, GeomType geomType, String name) {
	super(type, geomType, name);
    }

}
