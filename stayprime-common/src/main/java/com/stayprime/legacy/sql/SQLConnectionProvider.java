/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.stayprime.legacy.sql;

import java.sql.Connection;

/**
 *
 * @author benjamin
 */
public interface SQLConnectionProvider {
    public static final String PROP_CONNECTED = "connected";
    
    public Connection getConnection();

    public boolean isConnected();
    public boolean connect();
    public boolean disconnect();
    public void notifyError(Throwable error);

    public void addConnectionStatusObserver(Observer observer);
    public void removeConnectionStatusObserver(Observer observer);

    public static interface Observer {
	public void connectionStatusChanged(SQLConnectionProvider connectionProvider);
    }
}
