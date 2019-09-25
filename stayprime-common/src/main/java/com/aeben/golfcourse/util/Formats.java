/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.aeben.golfcourse.util;

import com.stayprime.golf.course.GolfHole;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 *
 * @author benjamin
 */
public class Formats {
    public static final DecimalFormat decimalFormat = new DecimalFormat("#.#");
    public static final DecimalFormat decimalFormat1Zero = new DecimalFormat("0.0");
    public static final NumberFormat zeroPad2Format = new DecimalFormat("00");
    public static final NumberFormat integerFormat = new DecimalFormat("0");
    public static final DateFormat shortDateTimeFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);

    public static String getHeadingString(float angle) {
        String heading[] = {"E", "NE", "N", "NW", "W", "SW", "S", "SE"};
        return(heading[(int) (((angle + 360 + 22.5) % 360) / 45)]);
    }

    public static String getPaceOfPlayString(int paceOfPlaySeconds) {
        int seconds = Math.abs(paceOfPlaySeconds);
        return (paceOfPlaySeconds < 0? "-" : "+") +
                zeroPad2Format.format(seconds / 60)
                + ":" +
                zeroPad2Format.format(seconds % 60);
    }

    public static String distanceRounded(double meters, DistanceUnits units) {
            return decimalFormat.format(Math.round(units.convertFrom(DistanceUnits.Meters, meters)));
    }

    public static String distance1DecimalPlace(double meters, DistanceUnits units) {
            return decimalFormat1Zero.format(units.convertFrom(DistanceUnits.Meters, meters));
    }

    public static String getShortCourseAndHoleString(GolfHole hole) {
        return hole.getGolfCourse().getName()+ " - " + hole.getNumber();
    }

    public static int[] getPaceOfPlayThresholdArray(String thresholdString) {
        String[] threshold = thresholdString.split(",");
        if(threshold.length == 2) {
            return new int[] {Integer.parseInt(threshold[0]), Integer.parseInt(threshold[1])};
        }
        return null;
    }

    //TODO: Join repeated code
    public static String getTimeString(int minutes) {
        int min = Math.abs(minutes);
        return (minutes < 0? "-" : "") +
                integerFormat.format(min / 60)
                + ":" +
                zeroPad2Format.format(min % 60);
    }

    public static int parseSecondsDuration(String value) {
	int seconds = 0;

	String hhmmss[] = value.split(":");

	if(hhmmss.length > 0)
	    seconds += Integer.parseInt(hhmmss[hhmmss.length - 1]);

	if(hhmmss.length > 1)
	    seconds += 60*Integer.parseInt(hhmmss[hhmmss.length - 2]);

	if(hhmmss.length > 2)
	    seconds += 60*60*Integer.parseInt(hhmmss[hhmmss.length - 3]);

	return seconds;
    }

//    public static String distanceRounded(double meters, GolfClub.Units units) {
//            return decimalFormat.format(Math.round(units.convertFromMeters((float) meters)));
//    }
//
//    public static String distance1DecimalPlace(double meters, GolfClub.Units units) {
//            return decimalFormat1Zero.format(units.convertFromMeters((float) meters));
//    }
//
//    public static String getCourseAndHoleString(GolfCourseObject hole) {
//        return hole instanceof HoleDefinition? ((HoleDefinition)hole).course.toString() + ", Hole " +
//                ((HoleDefinition)hole).number : "Invalid Hole";
//    }
//
//    public static String getShortCourseAndHoleString(GolfCourseObject hole) {
//        return hole instanceof HoleDefinition? ((HoleDefinition)hole).course.toString() + " - " +
//                ((HoleDefinition)hole).number : "";
//    }
//
//    public static int[] getPaceOfPlayThresholdArray(String thresholdString) {
//        String[] threshold = thresholdString.split(",");
//        if(threshold.length == 2) {
//            return new int[] {Integer.parseInt(threshold[0]), Integer.parseInt(threshold[1])};
//        }
//        return null;
//    }
//
//    public static Map<Integer, CartStatus> decodeCartsStatusNumbers(Number cartsData[][]) {
//	Map<Integer, CartStatus> cartsAhead = new HashMap<Integer, CartStatus>();
//
//	for (Number[] cartLocation: cartsData) {
//	    if (cartLocation[0] instanceof Integer) {
//		int cartNumber = cartLocation[0].intValue() & 0xFFFF;
//		int statusFlags = cartLocation[0].intValue() >> 2 & 0xFF;
//		int angle = ((byte) (cartLocation[0].intValue() >> 3 & 0xFF)) * 2;
//
//		CartStatus status = new CartStatus(cartNumber);
//		status.heading = (float) angle;
//		status.location = new Coordinates(cartLocation[2].doubleValue(), cartLocation[1].doubleValue());
//		status.onActionZone = (statusFlags & CartStatus.STATUS_ACTIONZONE_HIT) > 0;
//		status.onClubhouseZone = (statusFlags & CartStatus.STATUS_CLUBZONE_HIT) > 0;
//		status.outOfCartPath = (statusFlags & CartStatus.STATUS_OUT_OF_CARTPATH) > 0;
//
//		cartsAhead.put(cartNumber, status);
//	    }
//	}
//
//	return cartsAhead;
//    }
//
//    public static Number[][] encodeCartsStatusNumbers(Map<Integer, CartStatus> cartsAhead) {
//	Number cartsData[][] = new Number[cartsAhead.size()][3];
//
//	int index = 0;
//	for(Integer cartNumber: cartsAhead.keySet()) {
//	    CartStatus status = cartsAhead.get(cartNumber);
//
//	    int statusFlags =
//		    (status.onActionZone? CartStatus.STATUS_ACTIONZONE_HIT : 0) |
//		    (status.onClubhouseZone? CartStatus.STATUS_CLUBZONE_HIT : 0) |
//		    (status.outOfCartPath? CartStatus.STATUS_OUT_OF_CARTPATH : 0);
//	    int heading = status.heading == null? 0 :
//		Math.round(status.heading / 2);
//
//	    int numberAndFlags =
//		    (status.cartNumber & 0xFFFF) |
//		    (statusFlags << 2 & 0xFF0000) |
//		    (heading << 3 & 0xFF000000);
//
//	    cartsData[index][0] = numberAndFlags;
//	    cartsData[index][1] = status.location.longitude;
//	    cartsData[index][2] = status.location.latitude;
//
//	    index++;
//	}
//
//	return cartsData;
//    }

}
