package com.stayprime.device.gps;

public class GPRMCSentence {

    private String time = null;
    private String date = null;
    private boolean valid;

    public String getDate() {
	return date;
    }

    public String getTime() {
	return time;
    }

    public boolean isValid() {
	return valid;
    }
    
    public void parseSentence(String line) {
        if (!line.startsWith("$GPRMC,")) {
            throw new IllegalArgumentException();
        }

        //time
        int index0 = line.indexOf(",") + 1;
        int index1 = line.indexOf(",", index0);
        time = line.substring(index0, index1);

        //skip status
        index0 = index1 + 1;
        index1 = line.indexOf(",", index0);
        valid = line.charAt(index0) == 'A';

        index0 = index1 + 1;
        //skip latitude
        index0 = line.indexOf(",", index0) + 1;
        //skip latitude hemisphere
        index0 = line.indexOf(",", index0) + 1;
        //skip longitude
        index0 = line.indexOf(",", index0) + 1;
        //skip longitude hemisphere
        index0 = line.indexOf(",", index0) + 1;
        //skip ground speed
        index0 = line.indexOf(",", index0) + 1;
        //skip track made good

        index0 = line.indexOf(",", index0) + 1;
        index1 = line.indexOf(",", index0);
        date = line.substring(index0, index1);
    }

}
