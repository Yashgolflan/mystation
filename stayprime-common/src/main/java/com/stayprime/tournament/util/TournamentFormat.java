/*
 * 
 */
package com.stayprime.tournament.util;

import com.stayprime.tournament.model.Tournament;
import com.stayprime.util.FormatUtils;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author benjamin
 */
public class TournamentFormat {
    public static final DateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public static String formatDateTime(Date d) {
        if (d == null) {
            return "";
        }
        else {
            return dateTimeFormat.format(d);
        }
    }

    public static Date parseDateTime(String s) {
        if (s != null) {
            try {
                return dateTimeFormat.parse(s);
            }
            catch (ParseException ex) {
            }
        }
        return null;
    }

    public static String formatDate(Date d) {
        if (d == null) {
            return "";
        }
        else {
            return dateFormat.format(d);
        }
    }

    public static Date parseDate(String s) {
        if (s != null) {
            try {
                return dateFormat.parse(s);
            }
            catch (ParseException ex) {
            }
        }
        return null;
    }

    public static String formatStartToEndDates(Tournament t) {
        if (t == null || t.getStartDate() == null) {
            return "";
        }

        Date start = t.getStartDate();
        Date end = t.getEndDate();

        if (end == null || end.compareTo(start) <= 0){
            return FormatUtils.dateFormat.format(start);
        }
        else {
            return FormatUtils.dateFormat.format(start)
                    + " to "  + FormatUtils.dateFormat.format(end);
        }
    }

    public static Object getScoringFormatDisplay(Tournament t) {
        return t == null || t.getDefaultFormat() == null? "" : t.getDefaultFormat().getDisplay();
    }
}
