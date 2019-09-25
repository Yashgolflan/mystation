/*
 * 
 */
package com.aeben.golfcourse.tracking;

import java.util.List;

/**
 *
 * @author benjamin
 */
public class Track {
    private List<TrackPoint> track;

    public void setTrack(List<TrackPoint> track) {
	this.track = track;
    }

    public List<TrackPoint> getTrack() {
	return track;
    }

}
