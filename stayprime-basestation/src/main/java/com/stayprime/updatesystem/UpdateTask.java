/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.stayprime.updatesystem;

/**
 *
 * @author benjamin
 */
public abstract class UpdateTask implements TargetList.Observer {
    private Integer id;
    private String taskDescription;

    private TargetList targetList;
    private UpdateDescription updateDescription;
    protected final BasicUpdateProgress progress;

    /*
     * Initialization methods
     */

    public UpdateTask(Integer taskId, String taskDescription) {
	this.id = taskId;
	this.taskDescription = taskDescription;
	progress = new BasicUpdateProgress();
    }

    /*
     * Not thread safe.
     */
    public void setTargetList(TargetList targetList) {
	testRunning("Cannot set target list while running");

	if(this.getTargetList() != null)
	    targetList.removeTargetListObserver(this);

	if(targetList != null) {
	    targetList.addTargetListObserver(this);
	}

	this.targetList = targetList;
	progress.setTotalItems(targetList.size());
    }

    /*
     * Not thread safe.
     */
    public void setUpdateDescription(UpdateDescription updateDescription) {
	testRunning("Cannot set update description while running");
	
	this.updateDescription = updateDescription;
    }


    /*
     * Public operation methods, (these should be thread safe)
     */

    public UpdateProgress getProgress() {
	return progress;
    }

    public abstract void start();

    public abstract void stop();

    public abstract boolean isRunning();

    /*
     * Protected operation methods, (these should be thread safe)
     */

    //

    protected abstract void startTrackingTarget(UpdateTarget target);

    protected abstract void stopTrackingTarget(UpdateTarget target);

    /*
     * Implements TargetListObserver
     * Si no hay tareas ejecutandose y hay un nuevo target disponible,
     * iniciar una nueva tarea.
     * Si se elimino un target que esta actualizandose, detener la ejecución
     * del target.
     */

    public abstract void targetListUpdated();

    /*
     * Utility methods
     */

    protected void testRunning(String reason) {
	if(isRunning())
	    throw new IllegalStateException("UpdateTask: " + reason);
    }

    public Integer getId() {
	return id;
    }

    public String getTaskDescription() {
	return taskDescription;
    }

    public void setTaskDescription(String taskDescription) {
	this.taskDescription = taskDescription;
    }

    public UpdateDescription getUpdateDescription() {
	return updateDescription;
    }

    /**
     * @return the targetList
     */
    public TargetList getTargetList() {
	return targetList;
    }

}
