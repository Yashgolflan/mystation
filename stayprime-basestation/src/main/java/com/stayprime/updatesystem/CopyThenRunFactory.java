/*
 * 
 */

package com.stayprime.updatesystem;

import com.stayprime.updatesystem.access.AccessSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author benjamin
 */
public class CopyThenRunFactory implements UpdateMethodFactory {
    private static final Logger log = LoggerFactory.getLogger(CopyThenRunFactory.class);

    private CopyFiles copy;
    private RunCommands run;

    public CopyThenRunFactory(CopyFiles copy, RunCommands run) {
	log.debug("Creating CopyThenRunFactory: " + copy + " then " + run);
	this.copy = copy;
	this.run = run;
    }

    public UpdateMethodInstance getUpdateMethodInstance(AccessSystem accessSystem) {
	log.debug("BasicSSHCommand.getStdOut()");
	return new CopyThenRunInstance(accessSystem, copy, run);
    }

    private class CopyThenRunInstance implements UpdateMethodInstance, UpdateProgress.Observer {
	private final BasicUpdateProgress progress;
	private final UpdateMethodInstance copy;
	private final UpdateMethodInstance run;
        private volatile boolean running = true;

	public CopyThenRunInstance(AccessSystem accessSystem, 
		CopyFiles copyFiles, RunCommands runCommands) {
	    copy = copyFiles.getUpdateMethodInstance(accessSystem);
	    run = runCommands.getUpdateMethodInstance(accessSystem);
	    progress = new BasicUpdateProgress(
		    copy.getProgress().getTotalItems() +
		    run.getProgress().getTotalItems());
	    copy.getProgress().addProgressObserver(this);
	    run.getProgress().addProgressObserver(this);
	}

	public void startUpdating() throws Exception {
	    log.debug("CopyThenRunInstance.startUpdating()");

	    if(running && copy.getProgress().isDone() == false) {
		log.debug("CopyThenRun.copy.startUpdating()");
		copy.startUpdating();
	    }

	    log.debug("CopyThenRun.copy progress: " + copy.getProgress());

	    if(running && copy.getProgress().isDone() && run.getProgress().isDone() == false) {
		log.debug("CopyThenRun.run.startUpdating()");
		run.startUpdating();
	    }

	    log.debug("CopyThenRun.run progress: " + copy.getProgress());

	    progressUpdated(null);
	}

	public void stopUpdating() {
	    log.debug("CopyThenRunInstance.startUpdating()");

            running = false;
	    copy.stopUpdating();
	    run.stopUpdating();
	}

	public BasicUpdateProgress getProgress() {
	    return progress;
	}

	public void progressUpdated(UpdateProgress updateProgress) {
	    progress.setDoneItems(copy.getProgress().getDoneItems() +
		    run.getProgress().getDoneItems());
	}
    }
}
