/*
 * 
 */
package com.stayprime.comm.io;

import java.io.InputStream;
import java.io.OutputStream;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author benjamin
 */
public class BasicIOStream<I extends InputStream, O extends OutputStream> implements IOStream {
    private I inputStream;
    private O outputStream;
    private boolean open = true;

    public BasicIOStream(I inputStream, O outputStream) {
	this.inputStream = inputStream;
	this.outputStream = outputStream;
    }
    
    @Override
    public void start() {
    }

    @Override
    public I getInputStream() {
	return inputStream;
    }

    @Override
    public O getOutputStream() {
	return outputStream;
    }

    @Override
    public void stop() {
        open = false;
	IOUtils.closeQuietly(inputStream);
	IOUtils.closeQuietly(outputStream);
    }

    public boolean isOpen() {
        return open;
    }
}
