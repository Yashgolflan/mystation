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
public class BasicUpdateTask extends UpdateTask {
    private final List<UpdateThread> activeThreads;
    private final Object updateTargetSync = new Object();
    private UpdateProgress.Observer targetProgressObserver;
    private UpdateTarget.Observer targetObserver;

    public BasicUpdateTask(Integer taskId, String taskDescription) {
	super(taskId, taskDescription);
	activeThreads = new ArrayList<UpdateThread>();
	targetProgressObserver = new TargetProgressObserver();
	targetObserver = new TargetObserver();
    }

    @Override
    public void start() {
	synchronized(activeThreads) {
	    if(activeThreads.isEmpty()) {
		startUpdateThread();
	    }
	}
    }

    @Override
    public void stop() {
	synchronized(activeThreads) {
	    if(activeThreads.size() > 0) {
		for(UpdateThread updateThread: activeThreads) {
		    updateThread.stopUpdating();
		}
	    }
	}
    }

    @Override
    public boolean isRunning() {
	synchronized(activeThreads) {
	    return activeThreads.size() > 0;
	}
    }

    @Override
    protected void startTrackingTarget(UpdateTarget target) {
	target.addTargetObserver(targetObserver);
    }

    @Override
    protected void stopTrackingTarget(UpdateTarget target) {
	target.removeTargetObserver(targetObserver);
    }

    @Override
    public void targetListUpdated() {
	progress.setTotalItems(getTargetList().size());
	calculateProgress();
    }

    /*
     * Utility methods
     */
    private void calculateProgress() {
	synchronized(updateTargetSync) {
	    progress.setProgress(addTargetsProgress(getUpdateDescription()));
	}
    }

    private float addTargetsProgress(UpdateDescription updateDescription) {
	float applied = 0;

	synchronized(getTargetList()) {
	    for(int i = 0; i < getTargetList().size(); i++) {
		UpdateTarget target = getTargetList().get(i);
		if(target.isUpdateApplied(updateDescription))
		    applied += 1f;
		else {
		    UpdateProgress targetProgress = target.getUpdateProgress(updateDescription);

		    if(targetProgress != null)
			applied += targetProgress.getProgress();
		}
	    }
	}

	return applied;
    }

    private void startUpdateThread() {
	UpdateThread updateThread = new UpdateThread();
	updateThread.startUpdating();

	synchronized(activeThreads) {
	    activeThreads.add(updateThread);
	}
    }

    private void removeUpdateThread(UpdateThread updateThread) {
	synchronized(activeThreads) {
	    activeThreads.remove(updateThread);
	}
    }

    /*
     * Worker thread
     */
    private class UpdateThread extends Thread {
	private UpdateTarget updatingTarget;
	private volatile boolean keepUpdating = false, started = false, isUpdatingTarget = false;

	public UpdateThread() {
	    super("UpdateTaskThread-" + BasicUpdateTask.this.getId());
	}

	@Override
	public void run() {
	    try {
		while(keepUpdating) {
		    boolean noTargetsAvailable = true;

		    synchronized(updateTargetSync) {
			if(keepUpdating == false)
			    continue;
			
			updatingTarget = getTargetList().getNextAvailableTarget(getUpdateDescription());
			
			isUpdatingTarget = true; //Lock updating target
		    }

		    if(updatingTarget != null) {
			noTargetsAvailable = false;
			startTrackingTarget(updatingTarget);

			try {
			    updatingTarget.startUpdating(getUpdateDescription());
			}
			catch(Exception ex) { ex.printStackTrace(); }

			stopTrackingTarget(updatingTarget);
		    }

		    isUpdatingTarget = false; //Unlock updating taget

		    synchronized(updateTargetSync) {
			updatingTarget = null;
		    }

		    try {
			if(noTargetsAvailable)
			    Thread.sleep(1000);
			else
			    Thread.sleep(100);
		    }
		    catch (InterruptedException ex) {}
		}
	    }
	    finally {
		synchronized(updateTargetSync) {
		    if(updatingTarget != null)
			stopTrackingTarget(updatingTarget);
		}

		isUpdatingTarget = false;
		removeUpdateThread(this);
	    }
	}

	public synchronized void startUpdating() {
	    if(started == false) {
		started = true;
		keepUpdating = true;
		super.start();
	    }

	    interrupt();
	}

	public synchronized void stopUpdating() {
	    if(isUpdatingTarget) {
		ensureUpdatingTargetRunning();
		updatingTarget.stopUpdating(getUpdateDescription());
	    }
	    
	    keepUpdating = false;
	    
	    interrupt();
	}

	private void ensureUpdatingTargetRunning() {
	    while(true) {
		synchronized(updateTargetSync) {
		    if(isUpdatingTarget == false)
			return;

		    if(updatingTarget.isUpdateRunning(getUpdateDescription())
			    || updatingTarget.isUpdateApplied(getUpdateDescription()))
			return;
		}
	    }
	}

    }

    private class TargetProgressObserver implements UpdateProgress.Observer {
	public void progressUpdated(UpdateProgress progress) {
	    calculateProgress();
	}
    }

    private class TargetObserver implements UpdateTarget.Observer {
	public void startedUpdating(UpdateTarget target, UpdateDescription updateDescription) {
	    if(updateDescription == getUpdateDescription()) {
		UpdateProgress targetProgress = target.getUpdateProgress(getUpdateDescription());

		if(targetProgress != null)
		    targetProgress.addProgressObserver(targetProgressObserver);
	    }
	}

	public void stopedUpdating(UpdateTarget target, UpdateDescription updateDescription) {
	    if(updateDescription == getUpdateDescription()) {
		UpdateProgress targetProgress = target.getUpdateProgress(getUpdateDescription());

		if(targetProgress != null)
		    targetProgress.removeProgressObserver(targetProgressObserver);
	    }
	}

    }

}
