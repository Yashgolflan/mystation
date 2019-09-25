/*
 * 
 */
package com.oncourseads;

/**
 *
 * @author benjamin
 */
public interface ImpressionSequence {
    public enum ImpressionStatus {STOPPED, PRELOADED, READY, PLAYING, READYTOSTOP};
    
    public ImpressionStatus getStatus();
    
    public void preload();
    public void prepareToPlay();
    public void play();
    public void prepareToStop();
    public void stop();
    
    public interface Observer {
	public void readyToPlay();
	public void readyToStop();
    }
}
