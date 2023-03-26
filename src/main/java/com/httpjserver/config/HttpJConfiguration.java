package com.httpjserver.config;

/**
 * Configuration class that maps to the properties in the application.yaml
 *
 * @param port           the port the server listing to
 * @param webRoot        the webroot location to serve static files
 * @param threadPoolSize size of the thread pool
 */
public record HttpJConfiguration(Integer port, String webRoot, Integer threadPoolSize) {

}
