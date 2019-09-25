/*
 * 
 */
package com.stayprime.util;

import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author benjamin
 */
public class SystemUtil {

    private static final String[] langEnv = {"LANG=C"};

    public enum OS {
        WINDOWS,
        LINUX,
        MAC,
        UNKNOWN,;
    }

    public static Process execWithLangC(String command) throws IOException {
        return Runtime.getRuntime().exec(command, langEnv);
    }

    public static boolean runCommands(String... commands) {
        String command = null;
        try {
            for (String comm : commands) {
                command = comm;
                Runtime.getRuntime().exec(command).waitFor();
            }
            return true;
        }
        catch (Exception ex) {
            return false;
        }
    }

    /**
     * Gets the default configuration directory. The path have a trailing /
     *
     * @return The config file directory.
     */
    public static String getConfigDirectory() {
        String result = System.getProperties().getProperty("user.home");
        result += "/.stayprime/";

        return result;
    }

    /**
     * Determines the type of operating system.
     *
     * @return One of the constants:
     */
    public static OS getOS() {
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.indexOf("window") != -1) {
            return OS.WINDOWS;
        }
        else if (osName.indexOf("linux") != -1) {
            return OS.LINUX;
        }
        else if (osName.indexOf("mac") != -1) {
            return OS.LINUX;
        }

        try {
            Class.forName("sun.print.Win32PrintJob");
            return OS.WINDOWS;
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();;
        }

        return OS.UNKNOWN;
    }

    public static String getLinuxProcessCommand(String pid) {
        try {
            return FileUtils.readFileToString(new File("/proc/" + pid.trim() + "/comm"));
        }
        catch (IOException ex) {
            return null;
        }
    }
}
