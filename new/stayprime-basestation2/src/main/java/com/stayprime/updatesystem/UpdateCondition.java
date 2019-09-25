/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.stayprime.updatesystem;

/**
 *
 * @author benjamin
 */
public interface UpdateCondition {
    public boolean canUpdate(UpdateTarget target);
}
