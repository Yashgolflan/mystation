/*
 * 
 */
package com.stayprime.view.objects;

import com.stayprime.geo.Coordinates;
import com.aeben.golfcourse.util.Formats;
import com.aeben.golfclub.utils.Formatters;
import com.aeben.golfcourse.util.DistanceUnits;
import java.text.NumberFormat;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author benjamin
 */
public class DistanceMeasureObject extends MeasureObject {
    private Coordinates coord1;
    private Coordinates coord2;
    private final boolean pointToPointMeasure;
    private NumberFormat numberFormat = Formatters.integerFormat;
    private DistanceUnits distanceUnits = DistanceUnits.Yards;

    public DistanceMeasureObject(Coordinates coord1) {
        super("", null, null);
        this.coord1 = coord1;
        pointToPointMeasure = false;
    }

    public DistanceMeasureObject(Coordinates coord1, Coordinates coord2) {
        super("", null, null);
        this.coord1 = coord1;
        this.coord2 = coord2;
        pointToPointMeasure = true;
        setDistance();
    }

    public void setDistanceUnits(DistanceUnits distanceUnits) {
        this.distanceUnits = distanceUnits;
        setDistance();
    }

    public void setNumberFormat(NumberFormat numberFormat) {
        this.numberFormat = numberFormat;
    }

    /**
     * @return the coord1
     */
    public Coordinates getCoord1() {
        return coord1;
    }

    /**
     * @param coord1 the coord1 to set
     */
    public void setCoord1(Coordinates coord1) {
        this.coord1 = coord1;
        setDistance();
    }

    /**
     * @return the coord2
     */
    public Coordinates getCoord2() {
        return coord2;
    }

    /**
     * @param coord2 the coord2 to set
     */
    public void setCoord2(Coordinates coord2) {
        this.coord2 = coord2;
        setDistance();
    }

    private void setDistance() {
        if(coord1 != null && coord2 != null) {
            setText(numberFormat.format(distanceUnits.convertFrom(DistanceUnits.Meters, coord1.metersTo(coord2))));
            //getPoint1().setLocation(coordConverter.toPoint(coord1));

            if (pointToPointMeasure) {
                //getPoint2().setLocation(coordConverter.toPoint(coord2));
            }
        }
        else {
//            setPoint1(null);
//            setPoint2(null);
            setText(StringUtils.EMPTY);
        }
    }

}
