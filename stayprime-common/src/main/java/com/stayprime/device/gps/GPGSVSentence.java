package com.stayprime.device.gps;

import org.apache.commons.lang.math.NumberUtils;

public class GPGSVSentence {
    public static final int MAX_SV = 32;
    public static final int MAX_SVPERLINE = 4;

    private final SV svs[];
    private int totalSVs;
    private int readSVs;
    private int totalMessages;
    private int readMessages;

    private int validSVs;
    private float totalSNR;

    public GPGSVSentence() {
	svs = new SV[MAX_SV];
	for(int i = 0; i < svs.length; i++)
	    svs[i] = new SV();
    }

    public void reset() {
	validSVs = readSVs = totalSVs = 0;
	readMessages = totalMessages = 0;
	totalSNR = 0;

	for(SV sv: svs) sv.PRN = 0;
    }

    public int getTotalSVs() {
	return totalSVs;
    }

    public int getReadSVs() {
	if(readSVs == 0 || svs[readSVs - 1].PRN > 0)
	    return readSVs;
	else //Last SVs is empty
	    return readSVs - 1;
    }

    public boolean isFullyRead() {
	return totalMessages > 0 && readMessages == totalMessages;
    }

    public float getMeanSNR() {
	return totalSNR / validSVs;
    }

    /**
     *
     * @param line The NMEA GPGSV sentence line
     * @return
     */
    public boolean parseSentence(String line) {
        if (!line.startsWith("$GPGSV,")) {
            throw new IllegalArgumentException();
        }

	//Total messages
        int index0 = line.indexOf(",") + 1;
        int index1 = line.indexOf(",", index0);

	if(resetInvalid(index0, index1))
	    return false;

	String str = line.substring(index0, index1);
	int num = NumberUtils.toInt(str, 0);

	if(num < 1) //Invalid!
	    return false;
	else if(totalMessages == 0) //Set total lines
	    totalMessages = num;
	else if(num != totalMessages || isFullyRead() || readSVs >= MAX_SV) //Invalid/Fully Read
	    return false;

	//Message number
	index0 = index1 + 1;
	index1 = line.indexOf(",", index0);

	if(resetInvalid(index0, index1))
	    return false;

	str = line.substring(index0, index1);
	num = NumberUtils.toInt(str, 0);

	if(readMessages + 1 != num)
	    ; //Non sequential sentences!

	//Total SVs
	index0 = index1 + 1;
	index1 = line.indexOf(",", index0);

	//No SVs
	if(index1 == -1) {
	    totalSVs = 0;
	    return true;
	}

	str = line.substring(index0, index1);
	num = NumberUtils.toInt(str, 0);

	if(num < 1) //Invalid!
	    return false;
	else if(totalSVs == 0) //Set total SVs
	    totalSVs = num;
	else if(num != totalSVs) //Invalid/Fully Read
	    return false;

	int i = 0;
	//Read more SVs if we havent reached the max number - 32 (should never happen),
	//AND (we haven't read any yet NOR the last read one was null)
	//MAX 4 SVs per sentence
	while(i < MAX_SVPERLINE && readSVs < MAX_SV && (readSVs == 0 || svs[readSVs - 1].PRN > 0)) {
	    index0 = index1 + 1;
	    index1 = line.indexOf(",", index0);
	    if(index1 < 0)
		break;
	    str = line.substring(index0, index1);
	    svs[readSVs].PRN = NumberUtils.toInt(str, 0);

	    index0 = index1 + 1;
	    index1 = line.indexOf(",", index0);
	    str = line.substring(index0, index1);
	    svs[readSVs].elevation = NumberUtils.toInt(str, 0);

	    index0 = index1 + 1;
	    index1 = line.indexOf(",", index0);
	    str = line.substring(index0, index1);
	    svs[readSVs].azimuth = NumberUtils.toInt(str, 0);

	    index0 = index1 + 1;
	    index1 = line.indexOf(",", index0);
	    if(index1 < 0)
		index1 = line.indexOf("*", index0);
	    str = line.substring(index0, index1);
	    svs[readSVs].SNR = NumberUtils.toInt(str, 0);

	    if(svs[readSVs].PRN > 0 && svs[readSVs].SNR > 0) {
		validSVs++;
		totalSNR += svs[readSVs].SNR;
	    }

	    readSVs++;
	    i++;
	}

	readMessages++;
	return true;
    }

    private boolean invalidIndex(int index0, int index1) {
	boolean invalid = index0 < 0 || index1 < 0;
	return invalid;
    }

    private boolean resetInvalid(int index0, int index1) {
	boolean invalid = invalidIndex(index0, index1);
	if(invalid)
	    reset();

	return invalid;
    }

    private class SV {
	int PRN;
	int elevation;
	int azimuth;
	int SNR;
//	public int getPRN() {
//	    return PRN;
//	}
//
//	public int getElevation() {
//	    return elevation;
//	}
//
//	public int getAzimuth() {
//	    return azimuth;
//	}
//
//	public int getSNR() {
//	    return SNR;
//	}
    }

}
