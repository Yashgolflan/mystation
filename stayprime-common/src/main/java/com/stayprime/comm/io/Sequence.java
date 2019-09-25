/*
 * 
 */
package com.stayprime.comm.io;

/**
 *
 * @author benjamin
 */
public interface Sequence {

    public boolean reset();

    public boolean isSequenceChar(int character);

    public boolean isSequence();

    public String getSequence();
    
}
