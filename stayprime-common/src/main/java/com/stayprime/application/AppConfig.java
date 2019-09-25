/*
 * 
 */
package com.stayprime.application;

import com.stayprime.util.ClassUtilities;
import com.stayprime.util.file.FileLocator;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author benjamin
 */
public class AppConfig {
    private static final Logger log = LoggerFactory.getLogger(AppConfig.class);

    public static final String PARAM_LOGLEVEL = "-loglevel=";

    //Lists of configuration categories

    private final List<File> paths; //App specific paths

    private final List<FileLocator> fileLocators; //App FileLocator objects

    private final List<PropertiesConfiguration> configs; //App configs

    //Command line startup arguments
    protected String[] args = new String[0];

    private AppConfigObserver observer;

    public AppConfig() {
        paths = new ArrayList<File>();
        fileLocators = new ArrayList<FileLocator>();
        configs = new ArrayList<PropertiesConfiguration>();
    }

    public void setObserver(AppConfigObserver observer) {
        this.observer = observer;
    }

    public void setArgs(String[] args) {
        this.args = args;
    }

    public void printAppInfo(String title, Class cls) {
        ClassUtilities.printAppInfo(title, cls);
        log.info("Command line arguments: " + StringUtils.join(args, ' '));
    }

    public int addPath(File f) {
        paths.add(f);
        return paths.size() - 1;
    }

    public File getPath(int i) {
        return paths.get(i);
    }

    public int addFileLocator(FileLocator fl) {
        fileLocators.add(fl);
        return fileLocators.size() - 1;
    }

    public FileLocator getFileLocator(int i) {
        return fileLocators.get(i);
    }

    public int addConfig(PropertiesConfiguration c) {
        configs.add(c);
        return configs.size() - 1;
    }

    public PropertiesConfiguration getConfig(int i) {
        return configs.get(i);
    }

    protected void notifyLoaded() {
        if (observer != null) {
            observer.appConfigLoaded();
        }
    }
}
