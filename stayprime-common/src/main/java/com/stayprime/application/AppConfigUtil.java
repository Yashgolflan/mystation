/*
 *
 */
package com.stayprime.application;

import com.stayprime.util.ClassUtilities;
import java.io.File;

/**
 *
 * @author benjamin
 */
public class AppConfigUtil {
    /**
     * Look for base path in args, use the jar folder as base path by default.
     *
     * @param args Not null arguments string, array
     * @param sourceClass Source class for default path
     * @param argIndex Argument index for the base path
     * @return path arguments,
     */
    public static File getBasePath(String args[], Class sourceClass, int argIndex) {
        String arg0 = getArgIndex(args, argIndex);

        if (arg0 != null) {
            return new File(arg0);
        }

        return new File(ClassUtilities.getCodeSource(sourceClass));
    }

    public static String getArgIndex(String[] args, int i) {
        if (args != null && i >= 0) {
            int j = 0;
            for (String arg : args) {
                if (arg.startsWith("-") == false) {
                    if (j < i)
                        j++;
                    else if (j == i)
                        return arg;
                }
            }
        }

        return null;
    }

    public static String getArgOption(String[] args, String option) {
        if (args != null) {
            for (String arg : args) {
                if (arg.startsWith(option)) {
                    return arg.substring(option.length());
                }
            }
        }

        return null;
    }

}
