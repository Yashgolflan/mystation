/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.stayprime.updatesystem.test;

import com.stayprime.updatesystem.BasicTargetList;
import com.stayprime.updatesystem.BasicUpdateDescription;
import com.stayprime.updatesystem.TargetList;
import com.stayprime.updatesystem.BasicUpdateTask;
import com.stayprime.updatesystem.CopyFiles;
import com.stayprime.updatesystem.UpdateDescription;
import com.stayprime.updatesystem.UpdateMethodFactory;
import com.stayprime.updatesystem.UpdateProgress;
import com.stayprime.updatesystem.UpdateTarget;
import com.stayprime.updatesystem.UpdateTask;
import com.stayprime.updatesystem.ssh.BasicSSHTarget;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author benjamin
 */
public class SimpleTest {
    public static void main(String args[]) {
	final UpdateTask task = new BasicUpdateTask(1, "");

	List<UpdateTarget> targets = new ArrayList<UpdateTarget>();
	targets.add(new BasicSSHTarget("192.168.101.8", "root", "Boundary"));
	
	TargetList targetList = new BasicTargetList(targets);

	task.setTargetList(targetList);

	String files[] = new String[27];
	for(int i = 0; i < 27; i++ )
	    files[i] = "/home/benjamin/Documentos/Bollseye/Courses/Innisfail/Innisfail Holes 1-27/Hole_" + (i+1) + ".jpg";
	
	UpdateMethodFactory factory = new CopyFiles("/root", files);
	UpdateDescription updateDescription = new BasicUpdateDescription(1, "", "", factory);
	//UpdateDescription updateDescription = new SimpleCopyFileUpdate(1, "", "", new File("/sys/class/rtc/rtc0/time"), "/root");

	task.setUpdateDescription(updateDescription);

	task.getProgress().addProgressObserver(new UpdateProgress.Observer() {
	    public void progressUpdated(UpdateProgress progress) {
		System.out.println(progress.getProgress()*100f + "%");

		if(progress.isDone())
		    task.stop();
	    }
	});

	task.start();

	try {
	    System.in.read();
	}
	catch (IOException ex) {
	}

    }
}
