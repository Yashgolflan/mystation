/*
 * 
 */

package com.stayprime.util.file;

import java.io.File;
import java.io.FileFilter;

/**
 *
 * @author benjamin
 */
public class FileFilters {
    public static enum Dirs {ALL, NONE, FILTER};

    public static FileFilter and(FileFilter a, FileFilter b) {
	return new LogicFileFilter(a, b, true);
    }

    public static FileFilter or(FileFilter a, FileFilter b) {
	return new LogicFileFilter(a, b, false);
    }

    public static class LogicFileFilter implements FileFilter {
	private FileFilter a, b;
	boolean and;

	public LogicFileFilter(FileFilter a, FileFilter b, boolean and) {
	    this.a = a;
	    this.b = b;
	    this.and = and;
	}

	public boolean accept(File pathname) {
	    if(and)
		return a.accept(pathname) && b.accept(pathname);
	    else
		return a.accept(pathname) || b.accept(pathname);
	}

    }
}
