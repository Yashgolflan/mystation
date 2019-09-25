/*
 * 
 */
package com.stayprime.device.gps;

import com.aeben.golfcourse.cart.PositionInfoImpl;
import com.aeben.golfcourse.util.Formats;

/**
 *
 * @author benjamin
 */
public class GPSPositionImpl extends PositionInfoImpl implements GPSPosition {
    private int fixQuality;
    private float hdop;
    private float meanSNR;
    private long time;
    private int totalSVs;
    private int usedSVs;

    public void setFixQuality(int fixQuality) {
	this.fixQuality = fixQuality;
    }

    public int getFixQuality() {
	return fixQuality;
    }

    public void setHDOP(float hdop) {
	this.hdop = hdop;
    }

    public float getHDOP() {
	return hdop;
    }

    public void setMeanSNR(float meanSNR) {
	this.meanSNR = meanSNR;
    }

    public float getMeanSNR() {
	return meanSNR;
    }

    public void setTime(long time) {
	this.time = time;
    }

    public long getTime() {
	return time;
    }

    public void setTotalSVs(int totalSVs) {
	this.totalSVs = totalSVs;
    }

    public int getTotalSVs() {
	return totalSVs;
    }

    public void setUsedSVs(int usedSVs) {
	this.usedSVs = usedSVs;
    }

    public int getUsedSVs() {
	return usedSVs;
    }

    public void set(GPSPosition p) {
	set(p.getFixQuality(), p.getHDOP(), p.getMeanSNR(), p.getTime(), p.getTotalSVs(), p.getUsedSVs());
	super.set(p);
    }

    public void set(int fixQuality, float hdop, float meanSNR, long time, int totalSVs, int usedSVs) {
	setFixQuality(fixQuality);
	setHDOP(hdop);
	setMeanSNR(meanSNR);
	setTime(time);
	setTotalSVs(totalSVs);
	setUsedSVs(usedSVs);
    }

    
    public String getStatusLine() {
        StringBuilder status = new StringBuilder(getClass().getSimpleName() + "   ");
        status.append("fix: ").append(getFixQuality()).append("   ");
        status.append("SVs: ").append(getUsedSVs());

        if(getTotalSVs() > 0)
            status.append("/").append(getTotalSVs());

        status.append("   ");
        status.append("HDOP: ");
        status.append(Formats.decimalFormat.format(getHDOP())).append("   ");
        status.append("SNR: ");
        status.append(Formats.decimalFormat.format(getMeanSNR()));

        return status.toString();
    }

}
