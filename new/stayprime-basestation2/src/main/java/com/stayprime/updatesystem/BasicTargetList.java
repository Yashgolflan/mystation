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
public class BasicTargetList implements TargetList {
    private final List<Observer> targetListObservers;
    private final List<UpdateTarget> updateTargets;
    private UpdateCondition condition;
    private int nextIndex = 0;

    public BasicTargetList(List<UpdateTarget> updateTargets) {
	this.updateTargets = updateTargets;
	this.targetListObservers = new ArrayList<Observer>();
    }


    public void addTargetListObserver(Observer observer) {
	synchronized(targetListObservers) {
	    targetListObservers.add(observer);
	}
    }

    public void removeTargetListObserver(Observer observer) {
	synchronized (targetListObservers) {
	    targetListObservers.remove(observer);
	}
    }

    public void setAvailableCondition(UpdateCondition condition) {
	this.condition = condition;
    }

    public UpdateTarget getNextAvailableTarget(UpdateDescription description) {
	synchronized(this) {
	    if(updateTargets.isEmpty())
		return null;

	    if(nextIndex >= updateTargets.size())
		nextIndex = 0;

	    int currentIndex = nextIndex;

	    do {
		UpdateTarget target = updateTargets.get(currentIndex);

		if(target.isAvailable() && (condition == null || condition.canUpdate(target)))
		    return target;

		currentIndex++;

		if(currentIndex >= updateTargets.size())
		    currentIndex = 0;
		
	    } while(currentIndex != nextIndex);

	    nextIndex = currentIndex;
	}

	return null;
    }

    public synchronized UpdateTarget get(int index) {
	return updateTargets.get(index);
    }

    public synchronized boolean add(UpdateTarget target) {
	return updateTargets.add(target);
    }

    public synchronized boolean remove(UpdateTarget target) {
	return updateTargets.remove(target);
    }

    public synchronized void clear() {
	updateTargets.clear();
    }

    public synchronized boolean contains(UpdateTarget target) {
	return updateTargets.contains(target);
    }

    public synchronized int size() {
	return updateTargets.size();
    }

    public synchronized boolean isEmpty() {
	return updateTargets.isEmpty();
    }

}
