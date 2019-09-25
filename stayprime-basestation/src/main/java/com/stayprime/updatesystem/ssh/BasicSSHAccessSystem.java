/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.stayprime.updatesystem.ssh;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.SCPClient;
import ch.ethz.ssh2.Session;
import com.stayprime.updatesystem.access.AccessSystem;
import com.stayprime.updatesystem.access.Command;
import java.io.IOException;

/**
 *
 * @author benjamin
 */
public class BasicSSHAccessSystem implements AccessSystem {
    private String hostname, user, password;
    private long timeout = 10000;
    private Connection connection;

    public BasicSSHAccessSystem(String hostname, String user, String password) {
	this.hostname = hostname;
	this.user = user;
	this.password = password;
    }

    public void setHostname(String hostname) {
	this.hostname = hostname;
    }

    public String getHostname() {
	return hostname;
    }

    public void setUser(String user) {
	this.user = user;
    }

    public void setPassword(String password) {
	this.password = password;
    }

    public void getFile(String remoteFile, String localDirectory) {
	throw new UnsupportedOperationException("Not supported yet.");
    }

    public void sendFile(String localFile, String remoteDirectory) throws IOException {
	try {
            Connection conn = getConnection();
            connectAndAuthenticate(conn);

	    SCPClient scp = new SCPClient(conn);

	    scp.put(localFile, remoteDirectory);
	}
	finally {
	    stop();
	}
    }

    public Command runCommand(String command) throws IOException {
	Connection conn = getConnection();
        connectAndAuthenticate(conn);

	Session sess = conn.openSession();

	return new BasicSSHCommand(conn, sess, command);
    }

    public boolean test() {
	boolean success = false;
	try {
	    Connection conn = new Connection(hostname);
	    conn.connect(null, 10000, 10000);
	    success = conn.authenticateWithPassword(user, password);
	    conn.close();
	}
	catch (IOException ex) {
	}

	return success;
    }

    public synchronized void stop() {
        Connection conn = this.connection;
        if (conn != null) {
            conn.close();
            this.connection = null;
        }
    }

    private synchronized Connection getConnection() throws IOException {
        Connection conn = this.connection;
        if (conn != null) {
            conn.close();
        }

        this.connection = new Connection(hostname);

        return this.connection;
    }

    private void connectAndAuthenticate(Connection conn) throws IOException {
        conn.connect(null, 5000, 5000);
        boolean isAuthenticated = conn.authenticateWithPassword(user, password);

        if (isAuthenticated == false) {
            throw new IOException("Authentication failed.: " + hostname);
        }
    }
}
