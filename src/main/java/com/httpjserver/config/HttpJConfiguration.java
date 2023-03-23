package com.httpjserver.config;

public record HttpJConfiguration(Integer port, String webRoot, Integer threadPoolSize) {

}
