/*
 *
 */
package com.stayprime.golfapp.round;

import com.stayprime.golf.course.GolfHole;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * @author benjamin
 */
public class RoundRecordManager {
    private RoundRecord nullRecord;
    private final ArrayList<RoundRecord> records;
    private int maxRecords = 7;
    private Calendar currentDate;
    private boolean dateTimeValid;

    public RoundRecordManager() {
        records = new ArrayList<RoundRecord>(maxRecords);
        nullRecord = new RoundRecord(null);
    }

    public void setDateTimeValid(boolean dateTimeValid) {
        this.dateTimeValid = dateTimeValid;
    }

    public void updateDate() {
        GregorianCalendar c = new GregorianCalendar();
        c.setTime(new Date());
        setCurrentDate(new GregorianCalendar());
    }

    /**
     * Set the current record date.
     * This method has package access for test purposes.
     * @param c the current date.
     */
    void setCurrentDate(Calendar c) {
        this.currentDate = new GregorianCalendar(
                c.get(Calendar.YEAR),
                c.get(Calendar.MONTH),
                c.get(Calendar.DATE)
        );
    }

    public void addPlayedHole() {
        getCurrentRecord().addPlayedHole();
    }

    public int getPlayedHoles() {
        return getCurrentRecord().getPlayedHoles();
    }

    public void addPlayed9HoleRound() {
        getCurrentRecord().addPlayed9HoleRound();
    }

    public int getPlayed9HoleRounds() {
        return getCurrentRecord().getPlayed9HoleRounds();
    }

    public void addPlayed18HoleRound() {
        getCurrentRecord().addPlayed18HoleRound();
    }

    public int getPlayed18HoleRounds() {
        return getCurrentRecord().getPlayed18HoleRounds();
    }

    public void addRound(RoundState r, boolean roundTimedOut) {
        if (r.playedHolesEmpty() == false) {
            RoundDescription round = new RoundDescription(0);
            List<HoleStat> playedHoles = r.getPlayedHoles();
            HoleStat first = playedHoles.get(0);
            HoleStat last = playedHoles.get(playedHoles.size() - 1);

            round.setStartTimeUtc(first.getFirstEnteredTime());
            round.setEndTimeUtc(last.getLastHoleTime());

            for (HoleStat stat : playedHoles) {
                GolfHole h = stat.getHole();

                int course = h.getGolfCourse().getCourseIndex();
                PlayedHole playedHole = new PlayedHole(course, h.getNumber());

                long teeTime = (stat.getLastTeeboxTime() - stat.getFirstEnteredTime()) / 1000;
                playedHole.setTeeboxTime((int) Math.max(0, teeTime));

                long totalTime = (stat.getLastHoleTime() - stat.getFirstEnteredTime()) / 1000;
                playedHole.setTotalTime((int) Math.max(0, totalTime));

                round.addPlayedHole(playedHole);
                getCurrentRecord().addRound(round);
            }
        }
    }

    private RoundRecord getCurrentRecord() {
        if (currentDate == null) {
            return nullRecord;
        }
        else {
            checkCurrentDate();
            return records.get(0);
        }
    }

    private void checkCurrentDate() {
        if (records.isEmpty() || records.get(0).getDate().compareTo(currentDate) != 0) {
            if (records.isEmpty() == false) {
                records.get(0).finalizeRecord();
            }
            if (records.size() >= maxRecords) {
                records.remove(records.size() - 1);
            }
            records.add(0, new RoundRecord(currentDate));
        }
    }

    @Override
    public String toString() {
        RoundRecord r = getCurrentRecord();
        return r.toString();
    }

    public List<RoundRecord> getRecords() {
        return records;
    }

    public void clearOldRecords() {
        while (records.size() > 1) {
            records.remove(records.size() - 1);
        }
    }

}
