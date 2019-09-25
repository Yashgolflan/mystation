/*
 * 
 */

package com.stayprime.util.file;

import com.stayprime.util.file.FileFilters.Dirs;
import java.io.File;
import javax.swing.filechooser.FileFilter;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author benjamin
 */
public class PrefixFileFilter extends FileFilter implements java.io.FileFilter {
    private String prefixes[];
    private String type;
    private Dirs dirs;

    public PrefixFileFilter(String type, Dirs dirs, String ... prefixes) {
	this.prefixes = prefixes;
	this.type = type;
	this.dirs = dirs;
    }

    @Override
    public boolean accept(File f) {
	if(f.isDirectory()) {
	    if(dirs == Dirs.ALL)
		return true;
	    else if(dirs == Dirs.NONE)
		return false;
	}

	String name = f.getName();
	if(StringUtils.startsWithAny(name, prefixes)) {
	    return true;
	}

	return false;
    }

    @Override
    public String getDescription() {
	return type;
    }
}
