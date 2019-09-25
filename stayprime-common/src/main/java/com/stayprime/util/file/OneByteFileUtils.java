/*
 * 
 */

package com.stayprime.util.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author benjamin
 */
public class OneByteFileUtils {
    public static boolean writeByteSilent(String file, int b) {
        try {
            writeByte(file, b);
            return true;
        }
        catch (Exception ex) {
            return false;
        }
    }

    public static void writeByte(String file, int b) throws Exception {
	FileOutputStream out = null;
	try {
	    File f = new File(file);

	    if(f.exists() == false || f.canWrite() == false)
		throw new FileNotFoundException(file);

	    out = new FileOutputStream(file);
	    out.write(b);
	    out.flush();
	}
	finally {
            IOUtils.closeQuietly(out);
	}
    }

    public static int readByte(String file) throws Exception {
	FileInputStream in = null;
	try {
	    in = new FileInputStream(file);
	    return in.read();
	}
//	catch (Exception ex) {
//	    log.error("Error reading to file: " + file);
//	}
	finally {
	    IOUtils.closeQuietly(in);
	}
    }
}
