/*
 * 
 */
package com.oncourseads;

/**
 *
 * @author benjamin
 */
public interface AdPlayList {
    public ImpressionSequence getCurrentImpression();
    public ImpressionSequence getNextImpression();
    public void advance();
}
