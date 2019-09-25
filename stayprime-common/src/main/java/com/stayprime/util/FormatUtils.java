/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.stayprime.util;

import java.awt.Dimension;
import java.io.File;
import java.io.FileInputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Properties;

/**
 *
 * @author benjamin
 */
public class FormatUtils {
    public static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    public static final DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
    public static final DateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static boolean loadProperties(Properties properties, Class className, String filename) {
        try {
            File file;
            if ((file = new File(filename)).exists() ||
                    (file = new File(ClassUtilities.getCodeSource(className), filename)).exists()) {

                properties.load(new FileInputStream(file));
                return true;
            }
        }
        catch (Exception ex) {
            throw new RuntimeException("Error loading properties file " + filename, ex);
        }
        return false;
    }

    public static Dimension parseDimension(String dim) {
        String s[] = dim.split(",");
        return new Dimension(Integer.parseInt(s[0].trim()), Integer.parseInt(s[1].trim()));
    }

    public static String toHexString(byte b[]) {
	return toHexString(b, " ");
    }

    public static String toHexString(byte b[], int len) {
	return toHexString(b, len, " ");
    }

    public static String toHexString(byte b[], String delimiter) {
	return toHexString(b, b.length, delimiter);
    }

    public static String toHexString(byte b[], int len,  String delimiter) {
	if(b==null)
	    return null;
	
	StringBuilder str = new StringBuilder();
	for(int i = 0; i < b.length && i < len; i++) {
	    str.append(Integer.toHexString((int) b[i] & 0xFF | 0x100).substring(1));

	    if(i < b.length - 1)
		str.append(delimiter);
	}

	return str.toString();
    }

    public static String getValue(String line, String prefix, String separator, int skip) {
	if(line == null)
	    return null;

	String value = null;
	int index1 = line.indexOf(prefix);

	if(index1 >= 0) {
	    int prefixLength = prefix.length() + skip;
	    int index2 = separator == null? -1 :
		    line.indexOf(separator, index1 + prefixLength);

	    if(index2 >= 0)
		value = line.substring(index1 + prefixLength, index2);
	    else
		value = line.substring(index1 + prefixLength);
	}

	return value;
    }

    public static String getValue(String line, String prefix, String separator) {
	return getValue(line, prefix, separator, 0);
    }

}
