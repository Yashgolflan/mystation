/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.stayprime.updatesystem;

/**
 *
 * @author benjamin
 */
public interface UpdateMethodInstance {
    public void startUpdating() throws Exception;
    public void stopUpdating();
    public BasicUpdateProgress getProgress();
}
