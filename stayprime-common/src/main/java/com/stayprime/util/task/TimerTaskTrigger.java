package com.stayprime.util.task;

import java.util.TimerTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimerTaskTrigger extends TimerTask {

    private final Logger log = LoggerFactory.getLogger(TimerTaskTrigger.class);
    private Task task;

    public TimerTaskTrigger(Task task) {
	this.task = task;
    }

    @Override
    public void run() {
	try {
	    task.runTask();
	}
	catch (Throwable t) {
	    if (log.isTraceEnabled())
		log.trace(t.toString(), t);
	    else if (log.isDebugEnabled())
		log.debug(t.toString());
	}
    }
}
