/*
 * 
 */
package com.stayprime.comm.io;

/**
 *
 * @author benjamin
 */
public class StringSequence implements Sequence {
    private String sequence;
    private int match = 0;

    public StringSequence(String sequence) {
	this.sequence = sequence;
    }

    @Override
    public boolean reset() {
	match = 0;
	return true;
    }

    @Override
    public boolean isSequenceChar(int character) {
	if (match >= 0 && match < sequence.length()) {
	    if (character == sequence.charAt(match)) {
		match++;
		return true;
	    }
	    else {
		match = -1;
		return false;
	    }
	}

	match = -1;
	return false;
    }

    @Override
    public boolean isSequence() {
	return match == sequence.length();
    }

    @Override
    public String getSequence() {
	return sequence;
    }
    
}
