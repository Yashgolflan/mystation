/*
 * 
 */

package com.stayprime.golf.course.objects;

import com.stayprime.golf.objects.BasicPoint;

/**
 *
 * @author benjamin
 */
public class Hazard extends BasicPoint {
    public Hazard(Integer id, String name) {
	super(ObjectType.HAZARD, id, name);
    }
}
