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
public interface UpdateDescription {
    public Integer getUpdateId();
    public String getUpdateName();
    public String getUpdateDescription();
    public UpdateMethodInstance getUpdateMethodInstance(AccessSystem accessSystem);

    public boolean canUpdate(UpdateTarget target);
}
