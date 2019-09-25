/*
 *
 */

package com.stayprime.device.gps;

import com.stayprime.geo.CoordinateCalculations;
import com.stayprime.geo.Coordinates;
import com.aeben.golfcourse.cart.ObservablePositionInfo;
import com.aeben.golfcourse.util.Formats;
import com.stayprime.device.Time;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author benjamin
 */
public class CommandGPS implements PositioningSystem, ObservableGPSPosition {
    private static final Logger log = LoggerFactory.getLogger(CommandGPS.class);

    private List<ObservablePositionInfo.Observer> positionObservers;

    private String gpsCommand;
    private CommandOutputReader readingThread;

    private boolean gpsDetected = false;
    private volatile boolean running = false;
    private volatile boolean hasFix = false;
    private long gpsInterval = 1000;
    private long lastSentence = -1;

    private Coordinates coordinates;
    private float speed;
    private float heading;
    private float hdop;
    private int fixQuality = 0;
    private transient float meanSNR = 0;
    private int usedSatellites = 0;
    private int totalSatellites = 0;

    private Coordinates lastCoordinates;
    private Coordinates lastHeadingPoint;
    private long lastTimeSentence = 0;
    private long lastValidFixTime = 0;
    private final SimpleDateFormat gpsDateFormat;

    private GPGGASentence gpgga;
    private GPRMCSentence gprmc;
    private GPGSVSentence gpgsv;

    private float snrLowLimit = 20f;
    private int svsLowLimit = 6;
    private float snrHighLimit = 26f;
    private int svsHighLimit = 8;

    public CommandGPS() {
	this("setBaud /dev/ttymxc2 9600");
    }

    public CommandGPS(String gpsCommand) {
	if (log.isDebugEnabled())
	    log.debug("Creating CommandGPS: " + gpsCommand);

	this.gpsCommand = gpsCommand;

	gpgga = new GPGGASentence();
	gprmc = new GPRMCSentence();
        gpgsv = new GPGSVSentence();
	coordinates = new Coordinates(0, 0);
	lastCoordinates = new Coordinates(0, 0);
	lastHeadingPoint = null;

	positionObservers = new ArrayList<ObservableGPSPosition.Observer>(3);

	gpsDateFormat = new SimpleDateFormat("ddMMyyHHmmss");
	gpsDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    public long milliTime() {
        return Time.milliTime();
    }

    public void setSnrLowLimit(float snrLowLimit) {
        this.snrLowLimit = snrLowLimit;
    }

    public void setSnrHighLimit(float snrHighLimit) {
        this.snrHighLimit = snrHighLimit;
    }

    public void setSvsLowLimit(int svsLowLimit) {
        this.svsLowLimit = svsLowLimit;
    }

    public void setSvsHighLimit(int svsHighLimit) {
        this.svsHighLimit = svsHighLimit;
    }

    public void checkState() {
        long last = Time.milliTime() - lastSentence;
        if (lastSentence >= 0 && last > 10000) {
            try {
                CommandOutputReader reader = readingThread;
                reader.stopProcess();
            }
            catch (Throwable t) {
                log.error("Error stopping GPS process");
            }
        }
    }

    private synchronized void lineRead(String line) {
	if(line.startsWith("$GPGGA")) {
            lastSentence = Time.milliTime();
	    gpsDetected = true;
	    gpgga.parseSentence(line);
	    sentenceRead(gpgga);
	}
	else if(line.startsWith("$GPRMC")) {
            lastSentence = Time.milliTime();
	    lastTimeSentence = Time.milliTime();
	    gprmc.parseSentence(line);
	}
	else if(line.startsWith("$GPGSV")) {
            lastSentence = Time.milliTime();
	    if(gpgsv.parseSentence(line) == false) {
		log.trace("GPGSV error: " + line);
		gpgsv.reset();
		//meanSNR = 0;
	    }
	    else if(gpgsv.isFullyRead()) {
		meanSNR = gpgsv.getMeanSNR();
		totalSatellites = gpgsv.getTotalSVs();
		gpgsv.reset();
	    }
	}
    }

    private void sentenceRead(GPGGASentence gpgga) {
	boolean valid = gpgga.isValid();
	boolean updated = false;

	try {
	    if(valid) {
                boolean hadFix = hasFix;
		hasFix = true;
		coordinates.set(gpgga.getLatitude(), gpgga.getLongitude());

                //Only calculate speed and bearing for consecutive fixes
		if(hadFix && lastValidFixTime > 0) {
		    double meters = lastCoordinates.metersTo(coordinates);
		    float seconds = (Time.milliTime() - lastValidFixTime) / 1000f;
		    speed = (float) (meters / seconds  / 1000f * 3600f);

		    if(lastHeadingPoint == null) {
			heading = CoordinateCalculations.getAngle(lastCoordinates, coordinates);
			lastHeadingPoint = new Coordinates(coordinates);
		    }
		    else if(meters > 2 || lastHeadingPoint.metersTo(coordinates) > 2) {
			heading = CoordinateCalculations.getAngle(lastHeadingPoint, coordinates);
			lastHeadingPoint.set(coordinates);
		    }
		}
		else {
		    speed = 0f;
		    heading = -90f;
		}

		lastValidFixTime = Time.milliTime();
		lastCoordinates.set(coordinates);
	    }
	    else {
		hasFix = false;
		speed = 0;
		heading = 0;
	    }

	    hdop = gpgga.getHdop();
	    updated = setUsedSatellites(gpgga.getUsedSatellites());
	    updated |= setFixQuality(gpgga.getFixQuality());
	}
	catch(Throwable t) {log.error(t.toString());}

	if(valid || updated)
	    notifyUpdated();
    }

    private void notifyUpdated() {
	for(ObservablePositionInfo.Observer o: positionObservers) {
	    try { o.positionUpdated(this, lastCoordinates); }
	    catch(Throwable t) { log.error(t.toString()); }
	}
    }

    private boolean setUsedSatellites(int usedSatellites) {
	if(usedSatellites != this.usedSatellites) {
	    this.usedSatellites = usedSatellites;
	    return true;
	}
	return false;
    }

    private boolean setFixQuality(int fixQuality) {
	if(fixQuality != this.fixQuality) {
	    this.fixQuality = fixQuality;
	    return true;
	}
	return false;
    }

    /*
     * Implement PositioningSystem
     */
    @Override
    public void start() {
        if(!running) {
            log.debug("Starting CommandGPS");
            running = true;

            readingThread = new CommandOutputReader(gpsCommand, "GPSCommandReader");
            readingThread.start();
	    //log.debug("Started command reader thread");
        }
    }

    @Override
    public void stop() {
	//log.debug("Stop reading command output. isRunning = " + running);
        running = false;

        if(readingThread != null) {
            try {
                readingThread.stopProcess();
                readingThread.join(1000);
            }
            catch(InterruptedException ex) {
                //log.error(ex);
            }
            finally {
                readingThread = null;
            }
        }
	else {
	    //log.debug("Reading thread is already null");
	}
    }
    
    @Override
    public void reset () {
        log.debug("resetting the GPS...");
        if (running) {
            stop();

            if (running == false && readingThread == null) {
                start();
            }
        }
    }

    @Override
    public Coordinates getCoordinates() {
	return coordinates;
    }

    @Override
    public float getSpeed() {
	return speed;
    }

    @Override
    public float getHeading() {
	return heading;
    }

    @Override
    public void addPositionObserver(ObservablePositionInfo.Observer observer) {
	positionObservers.add(observer);
    }

    @Override
    public void removePositionObserver(ObservablePositionInfo.Observer observer) {
	positionObservers.remove(observer);
    }

    @Override
    public boolean isPositionValid() {
	return hasFix && usedSatellites >= svsLowLimit && meanSNR > snrLowLimit;
    }

    @Override
    public boolean isPositionGood() {
        return hasFix && usedSatellites >= svsHighLimit && meanSNR > snrHighLimit;
    }

    /*
     * Implement ObservableGPSPosition
     */
    public boolean isGPSDetected() {
	return gpsDetected;
    }

    @Override
    public int getTotalSVs() {
	return totalSatellites;
    }

    @Override
    public int getUsedSVs() {
	return usedSatellites;
    }

    @Override
    public int getFixQuality() {
	return fixQuality;
    }

    @Override
    public float getMeanSNR() {
	return meanSNR;
    }

    @Override
    public float getHDOP() {
	return hdop;
    }

    @Override
    public synchronized long getTime() {
	if(gprmc.isValid()) {
	    try {
		if(lastTimeSentence > 0) {
		    long correction = Time.milliTime() - lastTimeSentence;
		    boolean isExpired = correction > gpsInterval;

		    if(isExpired == false) {
			StringBuilder sb = new StringBuilder(gprmc.getDate()).append(gprmc.getTime());
			return gpsDateFormat.parse(sb.toString()).getTime();
		    }
		}
	    }
	    catch (Exception ex) {
	    }
	}

	return 0;
    }

    public long getLastFixTime() {
        return lastValidFixTime;
    }

    /*
     * Worker class
     * TODO: This must be separated to make the input stream implementation independent
     */
    private class CommandOutputReader extends Thread {
        private String command;
	private Process proc;

        CommandOutputReader(String command, String threadName) {
            super(threadName);
            this.command = command;
        }

        private void stopProcess() {
            Process process = proc;
            if (process != null) {
                process.destroy();
            }
        }

        @Override
        public void run() {
            while(running) {
                proc = null;
                try {
                    InputStream is;
                    proc = Runtime.getRuntime().exec(command);
                    is = proc.getInputStream();

		    gpsDetected = false;
                    readStream(is);
		    gpsDetected = false;
                }
                catch(Throwable t) {
		    try { Thread.sleep(1000); }
		    catch (InterruptedException ex) {}
                }
                finally {
                    if(proc != null) {
                        try {
                            proc.destroy();
                            proc.waitFor();
                        }
                        catch(Throwable t) {
                            //log.error(t);
                        }
                    }
                }
            }
        }

        public void readStream(InputStream is) throws IOException {
	    InputStreamReader isr = new InputStreamReader(is);
	    BufferedReader br = new BufferedReader(isr);
	    String line;

            lastSentence = Time.milliTime();
	    while ((line = br.readLine()) != null && running) {
		try {
		    lineRead(line);
		}
		catch(Throwable t) {
		    log.debug(t.toString());
		    log.trace(t.toString(), t);
		}
	    }
        }

    }

    public String getStatusLine() {
        if(isGPSDetected()) {
            StringBuilder status = new StringBuilder("GPS Detected   ");
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
        else
            return "GPS Not Detected";
    }

}
