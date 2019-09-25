/*
 * 
 */
package com.stayprime.spe;

/**
 *
 * @author benjamin
 */
public class SPEConst {
    public static final String microcomCommand = "microcom -X -s ";
    public static final String microcom = "microcom";
    public static final String lockFilePrefix = "/var/lock/LCK..";

    public static final String proc = "/proc/";
    public static final String proc_comm = "/comm";
    public static final String dev = "/dev/";
    public static final String killCommand = "kill -KILL ";

    public static final String spe2ModemPort = "ttyUSB1";
    public static final String spe2ModemDev = dev + "ttyUSB1";
    public static final String spe2GPSPort = "ttymxc3";
    public static final String spe2GPSDev = dev + "ttymxc3";

    public static final String spe1ModemPort = "ttymxc0";
    public static final String spe1ModemDev = dev + "ttymxc0";
    public static final String spe1GPSPort = "ttymxc2";
    public static final String spe1GPSDev = dev + "ttymxc2";
}
