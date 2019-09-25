package com.stayprime.util.task;

import java.lang.reflect.InvocationTargetException;
import javax.swing.SwingUtilities;

public class EDTTask implements Task {
    private final Task task;
    private final Runnable edtStart;
    private final Runnable edtRun;

    public EDTTask(Task triggeredOperation) {
	this.task = triggeredOperation;
	edtStart = new EDTStart();
	edtRun = new EDTRun();
    }

    public void startTask() {
	try { SwingUtilities.invokeAndWait(edtStart); }
	catch(InvocationTargetException ex) {}
	catch(InterruptedException ex) {}
    }

    public void runTask() throws Exception {
	try { SwingUtilities.invokeAndWait(edtRun); }
	catch(InterruptedException ex) {}
    }

    private class EDTStart implements Runnable {
	public void run() {
	    try {
		task.startTask();
	    }
	    catch (Exception ex) {
		throw new RuntimeException(ex);
	    }
	}
    }

    private class EDTRun implements Runnable {
	public void run() {
	    try {
		task.runTask();
	    }
	    catch (Exception ex) {
		throw new RuntimeException(ex);
	    }
	}
    }

}
