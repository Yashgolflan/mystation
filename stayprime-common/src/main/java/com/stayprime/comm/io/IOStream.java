/*
 * 
 */
package com.stayprime.comm.io;

import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * @author benjamin
 */
public interface IOStream<I extends InputStream, O extends OutputStream> {
    public void start() throws Exception;
    public I getInputStream();
    public O getOutputStream();
    public void stop();

    public boolean isOpen();
}
