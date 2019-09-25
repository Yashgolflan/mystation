/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.stayprime.updatesystem.ssh;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;
import com.stayprime.updatesystem.access.Command;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author benjamin
 */
class BasicSSHCommand implements Command {
    private static final Logger log = LoggerFactory.getLogger(BasicSSHCommand.class);

    private Connection conn;
    private Session session;
    private String command;
    private StreamGobbler stdout, stderr;

    public BasicSSHCommand(Connection conn, Session session, String command) throws IOException {
	this.conn = conn;
	this.session = session;
	this.command = command;

	session.execCommand(command);
	stdout = new StreamGobbler(session.getStdout());
	stderr = new StreamGobbler(session.getStderr());
    }



    public String getCommand() {
	return command;
    }

    public String getStdout() throws IOException {
	log.debug("BasicSSHCommand.getStdeut()");
	return readFullStream(stdout);
    }

    public String getStderr() throws IOException {
	log.debug("BasicSSHCommand.getStderr()");
	return readFullStream(stderr);
    }

    public void writeStdin(String data) throws IOException {
	OutputStream stdin = session.getStdin();
	stdin.write(data.getBytes());
    }

    public Integer getExitStatus() {
	return session.getExitStatus();
    }

    public String getExitSignal() {
	return session.getExitSignal();
    }

    private String readFullStream(StreamGobbler stdout) throws IOException {
	StringBuilder string = new StringBuilder();
	try {
	    BufferedReader br = new BufferedReader(new InputStreamReader(stdout));

	    while(true) {
		String line = br.readLine();

		if(line == null)
		    break;
		else
		    string.append(line);
	    }
	}
	finally {
	    tryCloseConnection();
	}

	return string.toString();
    }

    private void tryCloseConnection() {
	if(session != null)
	    session.close();

	if(conn != null)
	    conn.close();
    }
}
