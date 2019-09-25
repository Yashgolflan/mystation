/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.stayprime.updatesystem;

import com.stayprime.updatesystem.access.AccessSystem;
import java.io.IOException;

/**
 *
 * @author benjamin
 */
public class CopyFiles implements UpdateMethodFactory {
    private final String updateFiles[];
    private final String remoteDir;


    public CopyFiles(String remoteDir, String ... updateFiles) {
	if(updateFiles == null)
	    throw new NullPointerException("Update files == null!");
	if(remoteDir == null)
	    throw new NullPointerException("Remote dir == null!");

	this.updateFiles = updateFiles;
	this.remoteDir = remoteDir;
    }

    public int getUpdateFileCount() {
	return updateFiles.length;
    }

    public UpdateMethodInstance getUpdateMethodInstance(AccessSystem accessSystem) {
	return new CopyFileMethodInstance(accessSystem);
    }

    private class CopyFileMethodInstance implements UpdateMethodInstance {
	private final AccessSystem accessSystem;
	private final BasicUpdateProgress progress;

	public CopyFileMethodInstance(AccessSystem accessSystem) {
	    this.accessSystem = accessSystem;
	    progress = new BasicUpdateProgress(updateFiles.length);
	}

	public int getUpdateFileCount() {
	    return updateFiles == null? 0 : updateFiles.length;
	}

	public void startUpdating() throws IOException {
	    for (int i = progress.getDoneItems(); i < progress.getTotalItems(); i++) {
		String file = updateFiles[i];
		accessSystem.sendFile(file, remoteDir);
		progress.setDoneItems(i + 1);
	    }
	}

	public void stopUpdating() {
            accessSystem.stop();
	}

	public BasicUpdateProgress getProgress() {
	    return progress;
	}
    }

}
