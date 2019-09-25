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
public class BasicUpdateDescription implements UpdateDescription {

    private Integer updateId;
    private String updateName;
    private String updateDescription;
    private UpdateMethodFactory updateMethodFactory;

    public BasicUpdateDescription(Integer updateId, String updateName, String updateDescription) {
	this(updateId, updateName, updateDescription, null);
    }

    public BasicUpdateDescription(Integer updateId, String updateName, String updateDescription, UpdateMethodFactory factory) {
	this.updateId = updateId;
	this.updateName = updateName;
	this.updateDescription = updateDescription;
	this.updateMethodFactory = factory;
    }

    public Integer getUpdateId() {
	return updateId;
    }

    public String getUpdateDescription() {
	return updateDescription;
    }

    public String getUpdateName() {
	return updateName;
    }

    public boolean canUpdate(UpdateTarget target) {
	return true;
    }

    public UpdateMethodInstance getUpdateMethodInstance(AccessSystem accessSystem) {
	if(updateMethodFactory != null)
	    return updateMethodFactory.getUpdateMethodInstance(accessSystem);

	return null;
    }
}
