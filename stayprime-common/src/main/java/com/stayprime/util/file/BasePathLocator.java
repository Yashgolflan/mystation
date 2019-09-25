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
public class BasePathLocator implements FileLocator {
    private File basePath;

    public BasePathLocator(File basePath) {
	setBasePath(basePath);
    }
    
    public BasePathLocator(String basePath) {
	setBasePath(basePath);
    }

    public final void setBasePath(File basePath) {
	this.basePath = basePath;
    }
    
    public final void setBasePath(String basePath) {
	setBasePath(new File(basePath));
    }
    
    public File getFile(String name) {
	if(StringUtils.isBlank(name))
	    return null;
	else
	    return new File(basePath, name);
    }

    public boolean isFileDownloaded() {
	return false;
    }

    @Override
    public long getTotalBytes() {
        return 0;
    }

}
