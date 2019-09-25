/*
 * 
 */

package com.stayprime.util.file;

import java.io.File;

/**
 *
 * @author benjamin
 */
public interface FileLocator {
    public File getFile(String name);
    public boolean isFileDownloaded();
    public long getTotalBytes();
}
