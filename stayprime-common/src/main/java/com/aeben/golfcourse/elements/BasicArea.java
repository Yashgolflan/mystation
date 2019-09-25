package com.aeben.golfcourse.elements;

import com.stayprime.geo.Coordinates;
import java.util.List;
import org.apache.commons.lang.StringUtils;

public class BasicArea extends AbstractArea {

    public BasicArea(CourseElement parent, ObjectType type, Integer id, String name) {
	super(parent, type, id, name);
    }

    public void accept(CourseElementVisitor visitor) {
	visitor.visitCourseElement(this);
    }

    public String getGeometryString() {
        return getGeometryString(getShape());
    }

    public static String getGeometryString(List<Coordinates> shape) {
        if (shape != null) {
            return StringUtils.join(shape, ';');
        }
        return null;
    }

}
