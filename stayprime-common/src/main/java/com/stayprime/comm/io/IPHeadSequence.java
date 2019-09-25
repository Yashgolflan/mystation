/*
 * 
 */
package com.stayprime.comm.io;

/**
 *
 * @author benjamin
 */
public class IPHeadSequence implements Sequence {
    private StringBuffer sequence;
    private int buffer, bufferLen;
    private int match = 0;
    private int len = 6;
    private byte ip[] = new byte[4];
    private int port;
    private int bytes;

    public IPHeadSequence() {
	sequence = new StringBuffer(28);
    }

    @Override
    public final boolean reset() {
	sequence.setLength(0);
	buffer = 0;
	bufferLen = 0;
	port = -1;
	bytes = -1;
	match = 0;
	return true;
    }

    @Override
    public boolean isSequenceChar(int c) {
	if (match >= 0 && match < len) {
	    sequence.append(c);

	    if (match < 4) {
		if(c >= '0' && c <= '9') {
		    buffer = buffer * 10 + (c - '0');
		    bufferLen++;
		    return checkBuffer(255, 3);
		}
		else if(match < 3 && c == '.') {
		    return checkBuffer();
		}
		else if(match == 3 && c == ':') {
		    return checkBuffer();
		}
		else {
		    match = -1;
		    return false;
		}
	    }
	    else if (match < 6) {
		if(c >= '0' && c <= '9') {
		    buffer = buffer * 10 + (c - '0');
		    bufferLen++;
		    return checkBuffer(65535, 5);
		}
		else if(c == ',')
		    return checkBuffer();
	    }
	    else {
		match = -1;
		return false;
	    }
	}

	match = -1;
	return false;
    }

    private boolean checkBuffer(int max, int maxLen) {
	if(buffer <= max && bufferLen <= maxLen)
	    return true;

	match = -1;
	return false;
    }

    private boolean checkBuffer() {
	if(bufferLen > 0 ) {
	    if(match < 4) {
		ip[match] = (byte) buffer;
	    }
	    else if(match == 4)
		port = buffer;
	    else if(match == 5)
		bytes = buffer;

	    buffer = 0;
	    bufferLen = 0;
	    match++;
	    return true;
	}

	match = -1;
	return false;
    }

    @Override
    public boolean isSequence() {
	return match == 6;
    }

    public byte[] getIp() {
	return ip;
    }

    public int getPort() {
	return port;
    }

    public int getBytes() {
	return bytes;
    }

    @Override
    public String getSequence() {
	return isSequence()? sequence.toString() : null;
    }

}
