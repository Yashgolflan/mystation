package com.stayprime.util;


import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import javax.imageio.ImageIO;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author benjamin
 */
public class ClassUtilities {
    private static final Logger log = LoggerFactory.getLogger(ClassUtilities.class);

    public static final String manifestPath = "/META-INF/MANIFEST.MF";
    public static final String builtDateLabel = "Built-Date: ";
    public static final String versionLabel = "Version: ";

    public static void printAppInfo(String title, Class cls) {
        if(log.isInfoEnabled()) {
            log.info(title);
            log.info("Version: 3.0.3" + ClassUtilities.getVersionFromManifest(cls)
                    + "  built: on 19 Sept 2019" + ClassUtilities.getBuiltDateFromManifest(cls));
        }
    }

    public static String getCodeSource(Class clase) {
	File source = getCodeSourceFile(clase);

	if(source != null)
	    return source.getPath();
	else
	    return null;
    }

    public static File getCodeSourceFile(Class clase) {
        try {
            File file = new File(URLDecoder.decode(clase.getProtectionDomain().getCodeSource().getLocation().getPath(), Charset.defaultCharset().name()));
            if(file.isDirectory())
                return file;
            else
                return file.getParentFile();
        } catch (UnsupportedEncodingException ex) {
            
        }
	return null;
    }

    public static String getBuiltDateFromManifest(Class jarclass) {
	return getBuiltDateFromManifest(jarclass.getResourceAsStream(manifestPath));
    }

    public static String getVersionFromManifest(Class jarclass) {
	return getVersionFromManifest(jarclass.getResourceAsStream(manifestPath));
    }

    public static String getBuiltDateFromManifest(InputStream is) {
	return getStringFromManifest(is, builtDateLabel);
    }

    public static String getVersionFromManifest(InputStream is) {
	return getStringFromManifest(is, versionLabel);
    }

    public static String getStringFromManifest(InputStream is, String start) {
	try {
	    BufferedReader reader = new BufferedReader(new InputStreamReader(is));

	    String line;
	    while((line = reader.readLine()) != null) {
		if(line.startsWith(start))
		    return line.substring(start.length());
	    }
	}
	catch (Exception ex) {
	    log.error("Error reading string from manifest file: " + ex);
	}
	finally {
	    IOUtils.closeQuietly(is);
	}

	return null;
    }

    public static BufferedImage loadImageResource(Class cls, String path) {
        try {
            return ImageIO.read(cls.getResource(path));
        } catch (IOException ex) {
            return null;
        }
    }

}
