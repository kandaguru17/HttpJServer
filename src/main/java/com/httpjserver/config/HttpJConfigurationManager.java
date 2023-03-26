package com.httpjserver.config;

import com.httpjserver.util.HttpJUtil;

/**
 * Configuration manager singleton class that loads the configuration into context and
 * expose it via {@link HttpJConfiguration}
 */
public class HttpJConfigurationManager {

    private static HttpJConfiguration configuration;

    private static HttpJConfigurationManager configurationManager;

    private HttpJConfigurationManager() {
    }

    public static HttpJConfigurationManager getHttpJConfigurationManager() {
        if (configurationManager == null) {
            configurationManager = new HttpJConfigurationManager();
        }
        return configurationManager;
    }

    /**
     * Load the configuration from the file path passed as argument.
     *
     * @param filePath location of the property file
     */
    public void loadConfigurations(String filePath) {
        configuration = HttpJUtil.readValueFromFile(filePath, HttpJConfiguration.class);
    }

    public HttpJConfiguration getConfiguration() {
        return configuration;
    }
}
