/*
 * 
 */

package com.stayprime.util;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Properties;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;

/**
 *
 * @author benjamin
 */
public class ConfigUtil {
    public static PropertiesConfiguration load(File configFile) throws ConfigurationException {
        PropertiesConfiguration config = new PropertiesConfiguration();
        return load(config, configFile, false);
    }

    public static PropertiesConfiguration load(File configFile, Logger log) {
        PropertiesConfiguration config = new PropertiesConfiguration();
        return load(config, configFile, log, false);
    }

    public static PropertiesConfiguration load(PropertiesConfiguration config, File configFile, Logger log, boolean delimiterParsing) {
        try {
            load(config, configFile, delimiterParsing);
        }
        catch (ConfigurationException ex) {
            log.warn(ex.toString());
        }

        return config;
    }

    public static PropertiesConfiguration load(PropertiesConfiguration config, File configFile, boolean delimiterParsing) throws ConfigurationException {
        config.setDelimiterParsingDisabled(!delimiterParsing);
        config.setFile(configFile);
        config.load();

        return config;
    }

    //http://stackoverflow.com/questions/9672327/parsing-string-as-properties
    public static Properties loadPropertiesString(String s) {
        final Properties p = new Properties();

        if (s != null) {
            try {
                p.load(new StringReader(s));
            }
            catch (IOException ex) {
            }
        }

        return p;
    }

    public static String getPropertiesString(Properties p) {
        StringWriter sw = new StringWriter();
        try {
            p.store(sw, null);
        }
        catch (IOException ex) {
        }

        return sw.toString();
    }

}
