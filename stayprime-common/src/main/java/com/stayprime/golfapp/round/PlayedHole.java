/*
 * 
 */
package com.stayprime.golfapp.round;

/**
 *
 * @author benjamin
 */
public class PlayedHole {
    private final int courseId;
    private final int holeNumber;
    private int teeboxTime;
    private int totalTime;

    public PlayedHole(int courseId, int holeNumber) {
        this.courseId = courseId;
        this.holeNumber = holeNumber;
    }

    public int getCourseId() {
        return courseId;
    }
    public int getHoleNumber() {
        return holeNumber;
    }

    public int getTeeboxTime() {
        return teeboxTime;
    }

    public void setTeeboxTime(int teeboxTime) {
        this.teeboxTime = teeboxTime;
    }

    public int getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(int totalTime) {
        this.totalTime = totalTime;
    }

}
