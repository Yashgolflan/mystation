/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.stayprime.updatesystem;

import com.stayprime.updatesystem.access.AccessSystem;

/**
 *
 * @author benjamin
 */
public interface UpdateTarget {

    public void startUpdating(UpdateDescription updateDescription) throws Exception;
    public void stopUpdating(UpdateDescription updateDescription);
    public void stopUpdating();
    public AccessSystem getAccessSystem();

    /*
     * These methods are thread safe and called for information,
     * they should not block for long
     */
    public boolean isAvailable();
    public boolean isUpdateApplied(UpdateDescription updateDescription);
    public boolean isUpdateRunning(UpdateDescription updateDescription);
    public UpdateProgress getUpdateProgress(UpdateDescription updateDescription);

    public void addTargetObserver(Observer observer);
    public void removeTargetObserver(Observer observer);

    public interface Observer {
	public void startedUpdating(UpdateTarget target, UpdateDescription updateDescription);
	public void stopedUpdating(UpdateTarget target, UpdateDescription updateDescription);
    }
}
