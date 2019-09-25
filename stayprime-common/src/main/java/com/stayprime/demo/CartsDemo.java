/*
 *
 */
package com.stayprime.demo;

import com.stayprime.localservice.WebServiceUtils;
import com.stayprime.geo.Coordinates;
import com.aeben.golfcourse.cart.ObservablePositionInfo;
import com.aeben.golfcourse.cart.PositionInfo;
import com.stayprime.device.gps.DemoFilePS;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author benjamin
 */
public class CartsDemo extends Thread {
    private DemoUnitCommunication comm;
    private final String basePath;
    private PropertiesConfiguration config;
    private File demoFile;
    private int lineDelay = 1000;
    private int pairCount = 18;
    private int pairSeparation = 10;
    private int startNumber = 10;
    private int lines;

    private final Callback callback;

    public CartsDemo(String basePath, PropertiesConfiguration conf) {
        this(basePath, conf, null);
    }

    public CartsDemo(String basePath, PropertiesConfiguration conf, Callback callback) {
	this.basePath = basePath;
	if(conf != null)
	    this.config = conf;
	else
	    loadConfig();

	lineDelay = config.getInt("lineDelay", lineDelay);
	pairCount = config.getInt("pairCount", pairCount);
	pairSeparation = config.getInt("pairSeparation", pairSeparation);
	startNumber = config.getInt("startNumber", startNumber);
	demoFile = new File(basePath, config.getString("demoFile", "creekdemo_long.txt"));

        this.callback = callback;
    }

    @Override
    public void run() {
	comm = WebServiceUtils.createDemoCommunicationApi(config.getString("connectionString", "http://localhost:8080/"));
	lines = countLines();

	if(comm != null && lines > 100) {
	    for(int i = 0; i < pairCount; i++) {
		int start = lines/pairCount*i;
		int randomSeparation = pairSeparation + (int)(Math.random() * 20);
		int pace = 1000 - (int)(Math.random() * 2000);

		new DemoCart(2*i+startNumber, lineDelay, start, pace).start();
		new DemoCart(2*i+1+startNumber, lineDelay, start+randomSeparation, pace).start();
	    }
	}
    }

    private void loadConfig() {
	config = new PropertiesConfiguration();
	String configPath = basePath;

	try {
	    config.setBasePath(configPath);
	    config.setFileName("demo.properties");
	    config.load();
	}
	catch (ConfigurationException ex) {
	    ex.printStackTrace();
	}
    }

    public int countLines() {
	int lines = 0;
	BufferedReader reader = null;
	try {
	    reader = new BufferedReader(new FileReader(demoFile));
	    while (reader.readLine() != null) lines++;
	    reader.close();
	}
	catch (Exception ex) {
	    ex.printStackTrace();
	}
	finally {
	    IOUtils.closeQuietly(reader);
	}

	return lines;
    }

    private class DemoCart implements ObservablePositionInfo.Observer {
	int cartNumber;
	int counter;
	int pace;
	DemoFilePS demo;

	public DemoCart(int cartNumber, int delay, int skip, int pace) {
	    this.cartNumber = cartNumber;
	    this.pace = pace;
	    counter = cartNumber;
	    demo = new DemoFilePS(demoFile, delay, 0, skip);
	}

	private void start() {
	    comm.reportUnit(cartNumber, new byte[] {0, 1, 2, 3, 4, (byte)cartNumber});
	    demo.addPositionObserver(this);
	    demo.start();
	}

	public void positionUpdated(PositionInfo location, Coordinates oldLocation) {
	    counter = (counter+1) % lines;

	    if((counter + cartNumber) % 13 == 0) {
		Coordinates c = location.getCoordinates();
		int holeNumber = 1+Math.round(17f*demo.getLinesRead()/lines);
		comm.sendLocation(cartNumber, c.latitude, c.longitude, 0, 1, holeNumber, 0, 0, pace, 0);

                if (callback != null) {
                    callback.updated(cartNumber, c, 0, holeNumber, pace);
                }
	    }
	}

    }

}
