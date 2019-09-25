/*
 * 
 */

package com.stayprime.golf.objects;

import com.stayprime.golf.course.objects.ObjectType;
import com.stayprime.geo.Coordinates;
import java.awt.Color;
import java.util.Collections;
import java.util.List;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import org.apache.commons.collections.CollectionUtils;

/**
 *
 * @author benjamin
 */
public class BasicPoint extends AbstractFeature {
    public BasicPoint(ObjectType type, Integer id, String name) {
	super(type, GeomType.POINT, name);
    }
}
