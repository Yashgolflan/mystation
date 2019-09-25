/*
 * 
 */

package com.stayprime.legacy.sql;

import com.stayprime.legacy.sql.SQLConnectionProvider.Observer;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author benjamin
 */
public abstract class AbstractConnectionProvider implements SQLConnectionProvider {
    private List<Observer> observers;
    private boolean connected;

    public AbstractConnectionProvider() {
	observers = new ArrayList<Observer>();
    }

    public void addConnectionStatusObserver(Observer observer) {
	observers.add(observer);
    }

    public void removeConnectionStatusObserver(Observer observer) {
	observers.remove(observer);
    }

    public boolean isConnected() {
	return connected;
    }

    protected void setConnected(boolean connected) {
	if(this.connected != connected) {
	    this.connected = connected;
	    notifyConnectionStatusChanged();
	}
    }

   protected void notifyConnectionStatusChanged() {
	for(Observer o: observers) {
	    o.connectionStatusChanged(this);
	}
    }

}
