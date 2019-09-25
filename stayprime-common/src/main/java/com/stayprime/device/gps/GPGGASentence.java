package com.stayprime.device.gps;

import org.apache.commons.lang.math.NumberUtils;

public class GPGGASentence {

    private double longitude = 0, latitude = 0;
    private float hdop = 0.0F;
    private float altitude = 0.0F;
    private String time = null;
    private int fixQuality = 0;
    private int usedSatellites = 0;

    public float getAltitude() {
	return altitude;
    }

    public float getHdop() {
	return hdop;
    }

    public double getLatitude() {
	return latitude;
    }

    public double getLongitude() {
	return longitude;
    }

    public int getUsedSatellites() {
	return usedSatellites;
    }

    public String getTime() {
	return time;
    }

    public int getFixQuality() {
	return fixQuality;
    }
    
    public boolean isValid() {
	return fixQuality > 0 && fixQuality < 5;
    }

    public void parseSentence(String line) {
        if (!line.startsWith("$GPGGA,")) {
            throw new IllegalArgumentException();
        }
        
	String str;
        boolean valid = true;
        double lat = 0;
        double lon = 0;
	//time
        int index0 = line.indexOf(",") + 1;
        int index1 = line.indexOf(",", index0);
        
	//ddmm.sss
        index0 = index1 + 1;
        index1 = line.indexOf(",", index0);
	if (index1 - index0 > 4) {
	    str = line.substring(index0, index0 + 2);
	    lat = NumberUtils.toDouble(str, Double.NaN);
	    str = line.substring(index0 + 2, index1); //index0+9);
	    lat += NumberUtils.toDouble(str, Double.NaN) / 60.0;
	}
        else {
            valid = false;
        }

        index0 = index1 + 1;
        index1 = line.indexOf(",", index0);

        if (index1 - index0 == 1) {
            lat *= line.substring(index0, index0 + 1).equals("N") ? 1.0 : -1.0;
        }
        else {
            valid = false;
        }

	//dddmm.sss
        index0 = index1 + 1;
        index1 = line.indexOf(",", index0);
	if (index1 - index0 > 5) {
	    str = line.substring(index0, index0 + 3);
	    lon = NumberUtils.toDouble(str, Double.NaN);
	    str = line.substring(index0 + 3, index1); //index0+10);
	    lon += NumberUtils.toDouble(str, Double.NaN) / 60.0;
	}
        else {
            valid = false;
        }

        index0 = index1 + 1;
        index1 = line.indexOf(",", index0);
        if (index1 - index0 == 1) {
            lon *= line.substring(index0, index0 + 1).equals("E") ? 1.0 : -1.0;
        }
        else {
            valid = false;
        }

        if (valid) {
            longitude = lon;
            latitude = lat;
        }
        
	try {
            index0 = index1 + 1;
            index1 = line.indexOf(",", index0);
            if (index1 - index0 == 1) {
                fixQuality = Integer.parseInt(line.substring(index0, index1));
            }
            else {
                fixQuality = 0;
            }
            
	    index0 = index1 + 1;
            index1 = line.indexOf(",", index0);
            if (index1 - index0 > 0) {
                str = line.substring(index0, index1);
                usedSatellites = NumberUtils.toInt(str, 0);
            }
            else {
                usedSatellites = 0;
            }

	    index0 = index1 + 1;
	    index1 = line.indexOf(",", index0);
            if (index1 - index0 > 0) {
                str = line.substring(index0, index1);
                hdop = NumberUtils.toFloat(str, 0);
            }
            else {
                hdop = 99.99f;
            }
        }
        catch (Exception ex) {
            valid = false;
        }

        if (valid == false) {
            fixQuality = 0;
        }
    }

}
