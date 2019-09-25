/*
 * 
 */

package com.stayprime.device.gps;

import com.stayprime.geo.CoordinateCalculations;
import com.stayprime.geo.Coordinates;
import com.aeben.golfcourse.cart.ObservablePositionInfo;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author benjamin
 */
public class DemoFilePS implements PositioningSystem {
    private static final Logger log = LoggerFactory.getLogger(DemoFilePS.class);

    private List<ObservablePositionInfo.Observer> observers;
    private File demoFile;
    private InputStream input;

    private Coordinates coordinates;
    private float speed;
    private float heading;
    private int delay;
    private int skip;
    private int initialSkip;
    private int linesRead;

    private Coordinates lastCoordinates;
    private DemoFileReader readingThread;
    private boolean running, paused;
    private boolean positionValid;

    public DemoFilePS() {
	this(new File("testpath.txt"), 1000, 0, 0);
    }

    public DemoFilePS(File file, int delay, int skip) {
        this(file, delay, skip, 0);
    }

    public DemoFilePS(File file, int delay, int skip, int initialSkip) {
        this(delay, skip, initialSkip);
        log.debug("Creating DemoFilePS with file:" + file.getAbsolutePath() + ", delay:" + delay + ", skip:" + skip);
        this.demoFile = file;
    }

    public DemoFilePS(InputStream input, int delay, int skip) {
        this(delay, skip, 0);
        log.debug("Creating DemoFilePS with inputStream, delay:" + delay + ", skip:" + skip);
        this.input = input;
    }

    private DemoFilePS(int delay, int skip, int initialSkip) {
	this.delay = delay;
	this.skip = skip;
        this.initialSkip = initialSkip;

	coordinates = new Coordinates(0, 0);
	lastCoordinates = new Coordinates(0, 0);

	observers = new ArrayList<Observer>();
    }

    /*
     * GPS communication
     */
    public void start() {
        if(!running) {
            running = true;

            readingThread = new DemoFileReader("DemoFileReader");
            readingThread.start();
	    //log.debug("Started command reader thread");
        }
    }

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    public void reset() {
        if (readingThread != null) {
            readingThread.reset();
        }
    }

    public void stop() {
	//log.debug("Stop reading command output. isRunning = " + running);

        running = false;

        if(readingThread != null) {
            try {
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

    private void positionRead(boolean valid, Coordinates coord) {
	if(log.isTraceEnabled()) log.trace("Read coord: " + coord + "; speed: " + speed);

	if(valid) {
	    positionValid = true;
	    coordinates.set(coord);

	    if(lastCoordinates != null) {
		heading = CoordinateCalculations.getAngle(lastCoordinates, coordinates);
	    }
	    else {
		heading = -90f;
	    }

	    lastCoordinates.set(coordinates);
            notifyObservers();
	}
	else {
	    positionValid = false;
	    speed = 0;
	    heading = 0;
	}

    }

    private void notifyObservers() {
        for(Observer o: observers) {
            try {
                o.positionUpdated(this, null);
                //Thread.sleep(100); //Test for concurrent modification exception
            }
            catch (Exception ex) {
                log.warn(ex.toString());
                log.debug(ex.toString(), ex);
            }
        }
    }

    /*
     * Implement PositioningSystem
     */
    
    public Coordinates getCoordinates() {
	return coordinates;
    }

    public float getSpeed() {
	return speed;
    }

    public float getHeading() {
	return heading;
    }

    public void addPositionObserver(Observer observer) {
	observers.add(observer);
    }

    public void removePositionObserver(Observer observer) {
	observers.remove(observer);
    }

    public boolean isPositionValid() {
	return positionValid;
    }

    public boolean isPositionGood() {
        return true;
    }

    public int getLinesRead() {
	return linesRead;
    }

    private class DemoFileReader extends Thread {
	private long lastDataReadTime;
	private boolean watchdogHasRun = false;
	private InputStream is;

        DemoFileReader(String threadName) {
            super(threadName);
        }

        public void reset() {
            //Only reset by closing the stream if reading from a file
            if (demoFile != null) {
                IOUtils.closeQuietly(is);
            }
        }

        @Override
        public void run() {
	    boolean shouldLogError = true;
            //log.info("CommandOutputReader thread started. Command: " + command);

	    lastDataReadTime = System.currentTimeMillis();

            while(running) {
                try {
                    if(demoFile != null)
                        is = new FileInputStream(demoFile);
                    else
                        is = input;

                    readStream(is);
                }
                catch(Exception ex) {
                    if(shouldLogError && log.isErrorEnabled())
			log.error("Error reading demo file: " + ex);
		    
		    shouldLogError = false;
                }
                finally {
		    IOUtils.closeQuietly(is);
                }
		
		try {
		    Thread.sleep(delay);
		}
		catch(InterruptedException ex) {
		}
            }
        }

	public void watchDogRoutine() {
	    if(!watchdogHasRun) {
		watchdogHasRun = true;
		//log.info("Running GPS watchdog routine for the first time: " + new Date());
	    }

	    long stalledMillis = System.currentTimeMillis() - lastDataReadTime;
	    if(stalledMillis > 10000l) {
		IOUtils.closeQuietly(is);
	    }
	}

        private void readStream(InputStream is) throws IOException {
	    InputStreamReader isr = new InputStreamReader(is);
	    BufferedReader br = new BufferedReader(isr);
	    String line;
	    Coordinates coord = new Coordinates(0,0);
	    boolean valid;

            int skipped = skip;

	    linesRead = 0;

	    while ((line = br.readLine()) != null && running) {
		lastDataReadTime = System.currentTimeMillis();
		linesRead++;
		//if(gpsDataLog.isDebugEnabled()) gpsDataLog.debug(line);

                if (initialSkip > 0) {
                    if (skipped < initialSkip) {
                        skipped++;
                        continue;
                    }
                    else {
                        skipped = 0;
                        initialSkip = 0;
                    }
                }

                if(skipped < skip) {
		    skipped++;
		    continue;
		}
                else {
		    skipped = 0;
                }

		try {
		    if(line.startsWith("NULL")) {
			valid = false;

			positionRead(valid, coord);
		    }
		    else {
			String[] parts = line.split(",");
			if(parts.length >= 2) {
			    coord.set(Double.parseDouble(parts[1].trim()), Double.parseDouble(parts[0].trim()));
			    if (parts.length > 2) {
				speed = Float.parseFloat(parts[2].trim());
			    }
			    valid = true;

			    positionRead(valid, coord);
			}
			else {
			    valid = false;
			    coord.set(0, 0);
			}
		    }
		    
		}
		catch (Exception ex) {
		    if(log.isDebugEnabled()) log.warn(ex.toString());
		    if(log.isTraceEnabled()) log.debug(ex.toString(), ex);
		}

                do {
                    try {
                        Thread.sleep(delay);
                    }
                    catch(InterruptedException ex) {

                    }
                } while(paused);
	    }
        }
    }
}
