package com.stayprime.oncourseads;

import com.aeben.golfcourse.util.Formats;

public class AdInformation {

    public Ad ad;
    public int timesPlayed = 0;
    public double secondsPlayed = 0.0F;
    public boolean errorLoadingImage = false;

    public AdInformation(Ad ad) {
	this.ad = ad;
    }

    @Override
    public String toString() {
	return "[" + ad.source
		+ "," + errorLoadingImage 
		+ "," + timesPlayed
		+ ","+ Formats.decimalFormat1Zero.format(secondsPlayed) + "]";
    }

	public String getAdSource() {
		return ad.source;
	}

    private String getExtension() {
	if(ad != null && ad.source != null) {
	    int point = ad.source.lastIndexOf('.');
	    if(point >= 0) {
		return ad.source.substring(point+1);
	    }
	}
	
	return null;
    }

    public boolean isImage() {
	String ext = getExtension();
	
	return ext.equalsIgnoreCase("jpg") || 
		ext.equalsIgnoreCase("jpeg") || 
		ext.equalsIgnoreCase("png");
    }

    public boolean isVideo() {
	String ext = getExtension();
	
	return ext.equalsIgnoreCase("mp4") || 
		ext.equalsIgnoreCase("wmv");
    }

    public boolean isActiveForHole(int course, int hole, long when) {
        return ad != null && ad.isActiveForHole(course, hole, when);
    }

}
