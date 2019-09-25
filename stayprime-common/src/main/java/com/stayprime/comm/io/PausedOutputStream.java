/*
 * 
 */
package com.stayprime.comm.io;

import java.io.IOException;
import java.io.OutputStream;

/**
 *
 * @author benjamin
 */
public class PausedOutputStream extends OutputStream {
    private final OutputStream outputStream;
    private int delay;

    public PausedOutputStream(OutputStream outputStream, int delay) {
        this.outputStream = outputStream;
        this.delay = delay;
    }

    @Override
    public void write(int b) throws IOException {
        outputStream.write(b);
        outputStream.flush();
        try {
            Thread.sleep(delay);
        }
        catch (InterruptedException ex) {
        }
    }

    @Override
    public void flush() throws IOException {
        outputStream.flush();
    }

    @Override
    public void close() throws IOException {
        outputStream.close();
    }

}
