/*
 * 
 */

package com.stayprime.util.file;

import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author benjamin
 */
public class ExtensionFileFilter extends FileFilter implements java.io.FileFilter {

    private String extensions[];
    private String type;

    public ExtensionFileFilter(String type, String ... extensions) {
	this(extensions, type);
    }

    public ExtensionFileFilter(String[] extensions, String type) {
	this.extensions = extensions;
	this.type = type;
    }

    @Override
    public boolean accept(File f) {
	if (f.isDirectory()) {
	    return true;
	}
	int dot = f.getName().lastIndexOf('.');
	if (dot != -1) {
	    String suffix = f.getName().substring(dot + 1);
	    for (String ext : extensions) {
		if (suffix.equalsIgnoreCase(ext)) {
		    return true;
		}
	    }
	}
	return false;
    }

    @Override
    public String getDescription() {
	return type;
    }
}
