/*
 * 
 */
package com.stayprime.util.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author benjamin
 */
public class FileUtils {
    private static final Logger log = LoggerFactory.getLogger(FileUtils.class);
    
    public static long copyFile(InputStream from, File file) throws IOException {
        FileOutputStream to = null;
        long totalBytes = 0;
        //Open temp file
        //Copy
        //Rename
        //Delete
        try {
            if(file.exists() && file.delete() == false)
                log.error("Error in file system while deleting file '" + file.getPath() + "'");

	    to = new FileOutputStream(file);

            byte[] buffer = new byte[4096]; // To hold file contents
            int bytes_read; // How many bytes in buffer

            // Read a chunk of bytes into the buffer, then write them out,
            // looping until we reach the end of the file (when read() returns
            // -1). Note the combination of assignment and comparison in this
            // while loop. This is a common I/O programming idiom.
            while ((bytes_read = from.read(buffer)) != -1) {// Read until EOF
                to.write(buffer, 0, bytes_read); // write
                totalBytes += bytes_read;
            }
            
            to.close();
        }
        // Always close the streams, even if exceptions were thrown
        finally {
            IOUtils.closeQuietly(from);
            IOUtils.closeQuietly(to);
        }
        return totalBytes;
    }

    public static void setReadableForAllUsers(File file) {
        file.setReadable(true, false);
        file.getParentFile().setReadable(true, false);
        file.getParentFile().setExecutable(true, false);
    }

}
