package com.aeben.golfcourse.tracking;

import com.stayprime.geo.Coordinates;

public class TrackPoint {

    public final Coordinates coordinates;
    public final int svs;
    public final float hdop, snr;
    public final boolean mark;

    public TrackPoint(Coordinates coord, boolean mark) {
	this(coord, mark, 0, 0, 0);
    }

    public TrackPoint(Coordinates coord, boolean mark, int svs, float hdop, float snr) {
	coordinates = new Coordinates(coord);
	this.mark = mark;
	this.svs = svs;
	this.hdop = hdop;
	this.snr = snr;
    }
}
