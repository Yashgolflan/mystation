/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.stayprime.updatesystem.ssh;

import com.stayprime.updatesystem.access.AccessSystem;
import com.stayprime.updatesystem.UpdateDescription;
import com.stayprime.updatesystem.UpdateMethodInstance;
import com.stayprime.updatesystem.BasicUpdateProgress;
import com.stayprime.updatesystem.UpdateTarget;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author benjamin
 */
public class BasicSSHTarget implements UpdateTarget {
    private final List<Observer> targetObservers;

    private String hostName, user, pass;
    private BasicSSHAccessSystem accessSystem;
    private boolean accessDataChanged;

    private final Set<UpdateDescription> appliedUpdates;
    private UpdateDescription runningUpdate;
    private UpdateMethodInstance runningMethod;
    private transient boolean running = false;

    public BasicSSHTarget() {
	this(null, null, null);
    }

    public BasicSSHTarget(String hostName, String user, String pass) {
	this.hostName = hostName;
	this.user = user;
	this.pass = pass;

	accessDataChanged = true;

	appliedUpdates = new HashSet<UpdateDescription>();
	targetObservers = new ArrayList<Observer>();
    }

    public void setHostName(String hostName) {
	this.hostName = hostName;
	accessDataChanged = true;
    }

    public void setUser(String user) {
	this.user = user;
	accessDataChanged = true;
    }

    public void setPass(String pass) {
	this.pass = pass;
	accessDataChanged = true;
    }

    @Override
    public boolean isAvailable() {
	return getRunningUpdate() == null && getAccessSystem().test();
    }

    @Override
    public void startUpdating(UpdateDescription updateDescription) throws Exception {
	UpdateMethodInstance method = null;
	synchronized(this) {
	    if(runningUpdate != null && runningUpdate != updateDescription)
		return;

	    this.runningUpdate = updateDescription;

	    if(runningMethod == null)
		runningMethod = updateDescription.getUpdateMethodInstance(getAccessSystem());

	    method = runningMethod;
	    running = true;
	}

	synchronized(targetObservers) {
	    for(Observer o: targetObservers)
		o.startedUpdating(this, updateDescription);
	}

	if(method != null) {
	    method.startUpdating();
	}

	synchronized(targetObservers) {
	    for(Observer o: targetObservers)
		o.stopedUpdating(this, updateDescription);
	}

	if(method.getProgress().isDone()) {
	    synchronized(appliedUpdates) {
		appliedUpdates.add(runningUpdate);
	    }
	}

	synchronized(this) {
	    running = false;
	    runningUpdate = null;
	}
    }

    public void stopUpdating() {
        stopUpdating(runningUpdate);
    }

    public void stopUpdating(UpdateDescription updateDescription) {
	UpdateMethodInstance method = null;

	synchronized(this) {
	    if(updateDescription == runningUpdate) {
		method = runningMethod;
		runningMethod = null;
		this.runningUpdate = null;
	    }
	}

	if (method != null) {
	    method.stopUpdating();
        }
    }

    public boolean isUpdateRunning(UpdateDescription updateDescription) {
	synchronized(this) {
	    return running && updateDescription == runningUpdate;
	}
    }

    public AccessSystem getAccessSystem() {
	if(accessDataChanged) {
	    accessSystem = new BasicSSHAccessSystem(hostName, user, pass);
	}

	return accessSystem;
    }

    private UpdateDescription getRunningUpdate() {
	synchronized(this) {
	    return runningUpdate;
	}
    }

    public BasicUpdateProgress getUpdateProgress(UpdateDescription updateDescription) {
	UpdateMethodInstance method;
	
	synchronized(this) {
	    method = runningMethod;
	}

	if(method == null)
	    return null;
	else
	    return method.getProgress();
    }

    public boolean isUpdateApplied(UpdateDescription updateDescription) {
	synchronized(appliedUpdates) {
	    return appliedUpdates.contains(updateDescription);
	}
    }

    public void addTargetObserver(Observer observer) {
	synchronized(targetObservers) {
	    targetObservers.add(observer);
	}
    }

    public void removeTargetObserver(Observer observer) {
	synchronized(targetObservers) {
	    targetObservers.remove(observer);
	}
    }

}
