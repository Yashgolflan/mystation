/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.stayprime.updatesystem;

/**
 *
 * @author benjamin
 */
public interface UpdateProgress {
    public float getProgress();
    public boolean isDone();
    public int getTotalItems();
    public void addProgressObserver(Observer observer);
    public void removeProgressObserver(Observer observer);

    public interface Observer {
	public void progressUpdated(UpdateProgress progress);
    }

}
