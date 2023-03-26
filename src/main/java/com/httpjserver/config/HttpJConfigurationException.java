package com.httpjserver.config;

/**
 * Exception thrown in the event of any error while parsing the configuration file(s)
 */
public class HttpJConfigurationException extends RuntimeException {
    public HttpJConfigurationException(Exception ex) {
        super(ex);
    }
}
