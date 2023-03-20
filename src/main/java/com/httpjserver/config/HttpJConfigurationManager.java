package com.httpjserver.config;

import com.httpjserver.util.HttpJUtil;

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

    public void loadConfigurations(String filePath) {
        configuration = HttpJUtil.readValueFromFile(filePath, HttpJConfiguration.class);
    }

    public HttpJConfiguration getConfiguration() {
        return configuration;
    }
}
