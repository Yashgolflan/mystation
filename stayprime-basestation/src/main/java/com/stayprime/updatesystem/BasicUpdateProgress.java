/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.stayprime.updatesystem;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author benjamin
 */
public class BasicUpdateProgress implements UpdateProgress {
    private final List<Observer> updateObservers;
    private float progress = 0f;
    private boolean done = false;
    private int totalItems;
    private int doneItems = 0;

    public BasicUpdateProgress(int totalItems) {
	this.totalItems = totalItems;
	updateObservers = new ArrayList<Observer>();
    }

    public BasicUpdateProgress() {
	this(1);
    }


    public synchronized float getProgress() {
	return progress;
    }

    public boolean isDone() {
	return done;
    }

    public void setProgress(float progress) {
	synchronized(this) {
	    if(progress < 0f || progress > 1f)
		throw new IllegalArgumentException("Progress must be 0.0 to 1.0");

	    this.progress = progress;

	    this.done = progress == 1f;
	}

	synchronized(updateObservers) {
	    for(Observer o: updateObservers)
		o.progressUpdated(this);
	}
    }

    public void setTotalItems(Integer totalItems) {
	this.totalItems = totalItems;

	setDoneItems(doneItems);
    }

    public int getTotalItems() {
	return totalItems;
    }

    public void setDoneItems(int items) {
	doneItems = items;

	if(totalItems > 0) {
	    
	    if(items >= totalItems) {
		setProgress(1f);
		done = true;
	    }
	    else
		setProgress((float) items / totalItems);
	}
    }

    public int getDoneItems() {
	return doneItems;
    }

    public void addProgressObserver(Observer observer) {
	synchronized(updateObservers) {
	    updateObservers.add(observer);
	}
    }

    public void removeProgressObserver(Observer observer) {
	synchronized(updateObservers) {
	    updateObservers.remove(observer);
	}
    }
}
