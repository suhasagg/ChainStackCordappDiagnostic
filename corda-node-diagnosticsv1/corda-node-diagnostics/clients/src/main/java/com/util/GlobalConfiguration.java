package com.util;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.apache.log4j.Logger;

/**
 * A non-instancable class which manages global configuration, loads data from
 * config.properties.
 */
public final class GlobalConfiguration {

    private GlobalConfiguration() {
    }

    private static PropertiesConfiguration config = null;

    private static final Logger logger = Logger
            .getLogger(GlobalConfiguration.class);

    static {

    }

    public static synchronized void loadConfiguration() {
        if (config != null) {
            return;
        }
        try {
            config = new PropertiesConfiguration(GlobalConfiguration.class
                    .getResource("/config.properties"));
            config.setReloadingStrategy(new FileChangedReloadingStrategy());
        } catch (ConfigurationException e) {
            logger.fatal(e.getMessage(), e);
        }
    }

    public static String get(String key) {
        if (config == null) {
            loadConfiguration();
        }
        return config.getString(key, null);
    }

    public static Float getFloat(String key) {
        if (config == null) {
            loadConfiguration();
        }
        return config.getFloat(key, null);
    }

    public static Integer getInt(String key) {
        if (config == null) {
            loadConfiguration();
        }
        return config.getInteger(key, null);
    }

    public static Boolean getBoolean(String key) {
        if (config == null) {
            loadConfiguration();
        }
        return config.getBoolean(key, null);
    }

    public static String[] getList(String key) {
        if (config == null) {
            loadConfiguration();
        }
        return config.getStringArray(key);
    }

}

