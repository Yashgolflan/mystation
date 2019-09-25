/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.stayprime.updatesystem;

/**
 *
 * @author benjamin
 */
public interface TargetList {
    public void addTargetListObserver(Observer observer);
    public void removeTargetListObserver(Observer observer);

    public UpdateTarget getNextAvailableTarget(UpdateDescription description);
    public void setAvailableCondition(UpdateCondition condition);

    public boolean add(UpdateTarget target);
    public boolean remove(UpdateTarget target);
    public void clear();
    public boolean contains(UpdateTarget target);
    public UpdateTarget get(int index);
    public int size();
    public boolean isEmpty();

    public interface Observer {
	public void targetListUpdated();
    }
}
