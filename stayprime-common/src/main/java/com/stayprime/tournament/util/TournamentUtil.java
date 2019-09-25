/*
 * 
 */
package com.stayprime.tournament.util;

import ca.odell.glazedlists.EventList;
import com.stayprime.tournament.model.Player;
import com.stayprime.tournament.model.PlayerScores;
import com.stayprime.tournament.model.Tournament;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author benjamin
 */
public class TournamentUtil {
    private static Comparator<Tournament> tournamentDateComparator;

    public static String buildHtmlPlayerNames(List<PlayerScores> scores,
            String prefix, String suffix) {
        return buildHtmlPlayerNames(scores, prefix, suffix, "<br>", "<br>");
    }


    public static String buildHtmlPlayerNames(List<PlayerScores> scores,
            String prefix, String suffix, String itemPre, String itemSuf) {
        if (CollectionUtils.isNotEmpty(scores)) {
            StringBuilder playerNames = new StringBuilder(prefix);
            boolean validName = false;

            for (PlayerScores s : scores) {
                Player p = s.getPlayer();
                validName = appendPlayer(playerNames, p, itemPre, itemSuf);
            }

            if (validName) {
                playerNames.append(suffix);
                return playerNames.toString();
            }
        }
        return null;
    }

    private static boolean appendPlayer(StringBuilder playerNames, Player p, String itemPre, String itemSuf) {
        if (p != null && StringUtils.isNotBlank(p.getName())) {
            playerNames.append(itemPre);
            playerNames.append(p.getName());
            playerNames.append(itemSuf);
            return true;
        }
        return false;
    }

    public static int compareDate(Date date, Tournament t, boolean onlyDateStart, boolean onlyDateEnd) {
        return compareDate(date, t.getStartDate(), t.getEndDate(), onlyDateStart, onlyDateEnd);
    }

    public static int compareDate(Date date, Date startDate, Date endDate, boolean onlyDateStart, boolean onlyDateEnd) {
        if (startDate == null || date == null) {
            return Integer.MAX_VALUE;
        }

        GregorianCalendar start = createCalendar(startDate);
        if (onlyDateStart) {
            start = getDate(start, false);
        }
        if (date.getTime() < start.getTimeInMillis()) {
            return 1;
        }

        GregorianCalendar end;
        if (endDate != null && endDate.getTime() > startDate.getTime()) {
            end = createCalendar(endDate);
        }
        else {
            end = getDate(start, true);
        }
        if (onlyDateEnd) {
            end = getDate(end, true);
        }

        if (date.getTime() < end.getTimeInMillis()) {
            return 0;
        }
        else {
            return -1;
        }
    }

    public static GregorianCalendar createCalendar(long time) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTimeInMillis(time);
        return cal;
    }

    public static GregorianCalendar createCalendar(Date date) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(date);
        return cal;
    }

    public static GregorianCalendar getTodayDate(boolean end) {
        return getDate(createCalendar(System.currentTimeMillis()), end);
    }

    public static GregorianCalendar getDate(GregorianCalendar src, boolean end) {
        int year = src.get(Calendar.YEAR);
        int month = src.get(Calendar.MONTH);
        int date = src.get(Calendar.DAY_OF_MONTH);
        GregorianCalendar cal = new GregorianCalendar(year, month, date);

        if (end) {
            cal.add(Calendar.DATE, 1);
            cal.add(Calendar.SECOND, -1);
        }
        return cal;
    }

    public static int compareByDate(Tournament t1, Tournament t2) {
        long start1 = t1 == null || t1.getStartDate() == null? Long.MAX_VALUE : t1.getStartDate().getTime();
        long start2 = t2 == null || t2.getStartDate() == null? Long.MAX_VALUE : t2.getStartDate().getTime();
        long diff = start1 - start2;

        if (diff < Integer.MIN_VALUE) {
            return Integer.MIN_VALUE;
        }
        else if (diff > Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }
        else {
            return (int) diff;
        }
    }

    public static Comparator<Tournament> getDateComparator() {
        if (tournamentDateComparator == null) {
            tournamentDateComparator = new TournamentDateComparator();
        }
        return tournamentDateComparator;
    }

    public static int findPlayerByExtId(List<Player> sourceList, String extId) {
        for (int i = 0; i < sourceList.size(); i++) {
            Player p = sourceList.get(i);
            if (p != null && ObjectUtils.equals(p.getExtId(), extId)) {
                return i;
            }
        }
        return -1;
    }

    public static int findPlayerScoreByExtId(List<PlayerScores> sourceList, String extId) {
        for (int i = 0; i < sourceList.size(); i++) {
            PlayerScores s = sourceList.get(i);
            if (s != null && ObjectUtils.equals(s.getPlayer().getExtId(), extId)) {
                return i;
            }
        }
        return -1;
    }

    public static class TournamentDateComparator implements Comparator<Tournament> {
        @Override
        public int compare(Tournament t1, Tournament t2) {
            return compareByDate(t1, t2);
        }
    }

}
