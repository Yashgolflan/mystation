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
public class SimpleFileLocator implements FileLocator {

    public File getFile(String name) {
	if(StringUtils.isBlank(name))
	    return null;
	
	return new File(name);
    }

    public boolean isFileDownloaded() {
	return false;
    }

    @Override
    public long getTotalBytes() {
        return 0;
    }

}
