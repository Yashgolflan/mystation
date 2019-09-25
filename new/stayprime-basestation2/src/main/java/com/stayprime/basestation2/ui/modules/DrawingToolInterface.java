/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.stayprime.basestation2.ui.modules;

import com.aeben.golfclub.DrawableCourseShape;
import com.aeben.golfclub.GolfCourseObject;
import com.stayprime.geo.Coordinates;

/**
 *
 * @author benjamin
 */
public interface DrawingToolInterface {

    public GolfCourseObject getEditingObject();

    public void deleteSelectedShapePoint(DrawableCourseShape shape);

    public void setSelectedShapePointIndex(int selectingPointIndex);

    public void addShapePoint(DrawableCourseShape shape, Coordinates coord);

    public void setShapePoint(DrawableCourseShape shape, int pointIndex, Coordinates coord);

    public void deleteShapePoint(DrawableCourseShape shape, int pointIndex);

    public void setObjectPoint(GolfCourseObject object, Coordinates coord, boolean adjusting);

    public void setObjectInformationChanged(boolean b);
}
