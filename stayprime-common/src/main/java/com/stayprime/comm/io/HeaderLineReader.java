/*
 * 
 */
package com.stayprime.comm.io;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author benjamin
 */
public class HeaderLineReader extends FilterInputStream {
    private final int maxLineSize;
    private final byte[] lineBuffer;
    
    private Sequence sequence;

    public HeaderLineReader(InputStream in) {
	this(in, 128);
    }

    public HeaderLineReader(InputStream in, int maxLineSize) {
	super(in);
	this.maxLineSize = maxLineSize;
	lineBuffer = new byte[maxLineSize];
    }

    public void setHeaderSequence(Sequence headerSequence) {
	this.sequence = headerSequence;
    }
    
    public boolean isHeaderSequenceFound() {
	return sequence != null && sequence.isSequence();
    }

    public String readHeaderOrLine() throws IOException {
	//Read the first byte and return null if EOF
	int b = read();
	if(b == -1)
	    return null;

	//Initialize sequence and byte counter
	boolean head = sequence.reset();
	int i;

	//Read b is not EOF, nor we have passed the max line size
	for (i = 0; b >= 0 && i < maxLineSize; i++) {
	    lineBuffer[i] = (byte) b;

	    if (b == '\n') {
		//Found new line char, break and return lineBuffer
		//i will not increase so '\n' is not included
		break;
	    }
	    //All header chars have matched, check next one
	    else if(head) {
		//Doesn't match, stop searching:
		if(sequence.isSequenceChar(b) == false)
		    head = false;
		//Matches, if it's the last char, then we succeed:
		else if(sequence.isSequence())
		    return sequence.getSequence();
	    }

	    b = read();
	}
	
	return new String(lineBuffer, 0, i);
    }

    public int readFully(byte b[], int off, int len) throws IOException {
	if(len <= 0)
	    return 0;

	int remaining = len;
	int total = 0;
	int read;

	do {
	    read = read(b, total, remaining);
	    total += read;
	    remaining = len - total;
	} while (remaining > 0 && read > 0);
	
	return total;
    }

}
