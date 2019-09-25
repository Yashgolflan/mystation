/*
 * 
 */
package com.stayprime.comm.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * @author benjamin
 */
public class CommandIOStream implements IOStream<InputStream, OutputStream> {
    //Command for starting the process
    private String command;

    private Process process;
    private InputStream in;
    private OutputStream out;

    //True if the process is running and the IO streams open
    private boolean open = false;
    
    public CommandIOStream(String command) {
        setCommand(command);
    }

    public CommandIOStream() {
    }

    public final void setCommand(String command) {
        this.command = command;
    }

    @Override
    public InputStream getInputStream() {
	return in;
    }

    @Override
    public OutputStream getOutputStream() {
	return out;
    }

    @Override
    public void start() throws IOException  {
	if(process != null) {
	    throw new IOException("CommandIOStream already started");
        }
	if(command == null) {
	    throw new IOException("Command is null");
        }
	
	try {
	    process = Runtime.getRuntime().exec(command);
	    in = process.getInputStream();
	    out = process.getOutputStream();
            open = true;
	}
	catch (IOException ex) {
	    stop();
	    throw ex;
	}
	catch (Exception ex) {
	    throw new IOException("Exception opening serial port communication", ex);
	}
    }

    @Override
    public void stop() {
	if(process != null) {
            open = false;
	    process.destroy();
	    
	    try {process.waitFor(); }
	    catch(InterruptedException ex) {}
	    
	    process = null;
	}
    }

    @Override
    public boolean isOpen() {
        return open;
    }

}
