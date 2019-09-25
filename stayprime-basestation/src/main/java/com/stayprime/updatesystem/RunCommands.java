/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.stayprime.updatesystem;

import com.stayprime.updatesystem.access.AccessSystem;
import com.stayprime.updatesystem.access.Command;
import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author benjamin
 */
public class RunCommands implements UpdateMethodFactory {
    private static final Logger log = LoggerFactory.getLogger(RunCommands.class);
    private final String commands[];

    public RunCommands(String ... commands) {
	log.debug("Creating RunCommands update method: " +
		ArrayUtils.toString(commands));

	if(commands == null)
	    throw new NullPointerException("Commands == null!");
	this.commands = commands;
    }

    @Override
    public UpdateMethodInstance getUpdateMethodInstance(AccessSystem accessSystem) {
	return new RunCommandMethodInstance(accessSystem);
    }

    private class RunCommandMethodInstance implements UpdateMethodInstance {
	private AccessSystem accessSystem;
	private BasicUpdateProgress progress;

	public RunCommandMethodInstance(AccessSystem accessSystem) {
	    log.debug("Creating RunCommands instance.");

	    this.accessSystem = accessSystem;
	    progress = new BasicUpdateProgress(commands.length);
	}

	public void startUpdating() throws Exception {
	    log.debug("RunCommands.startUpdating()");

	    for (int i = progress.getDoneItems(); i < commands.length; i++) {
		String command = commands[i];
		log.debug("Running command: " + command);

		Command result = accessSystem.runCommand(command);
		log.debug("Running command exit signal: " + result.getExitSignal());
		log.debug("Running command exit status: " + result.getExitStatus());

		String stdout = result.getStdout();
		String stderr = result.getStderr();
		log.debug("Running command stdout: " + stdout);
		log.debug("Running command stderr: " + stderr);

		if(result.getExitStatus() != null && result.getExitStatus() > 0)
		    throw new Exception("Error " + result.getExitStatus() +
			    ": " + command + ". " + stderr);
		else
		    progress.setDoneItems(i + 1);
	    }
	}

	public void stopUpdating() {
	    log.debug("RunCommands.stopUpdating()");
            accessSystem.stop();
	}

	public BasicUpdateProgress getProgress() {
	    log.debug("RunCommands.getProgress(): " + 
		    (progress == null? null : progress.getProgress()));
	    return progress;
	}
    }

}
