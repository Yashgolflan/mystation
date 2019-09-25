/*
 * 
 */

package com.stayprime.util.file;

import java.io.File;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author benjamin
 */
public class FileExistsLocator implements FileLocator {
    private final FileLocator fileLocator;

    public FileExistsLocator(FileLocator fileLocator) {
	this.fileLocator = fileLocator;
    }

    @Override
    public File getFile(String name) {
	if(StringUtils.isBlank(name)) {
	    return null;
        }
	
	File file = fileLocator.getFile(name);
	
//	if(file == null || file.exists() == false)
//	    throw new RuntimeException("File not found: " + name);

        if(file != null && file.exists()) {
            return file;
        }
        else {
            return null;
        }
    }

    @Override
    public boolean isFileDownloaded() {
	return false;
    }

    @Override
    public long getTotalBytes() {
        return 0;
    }

}
