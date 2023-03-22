package com.httpjserver.runner;

import com.httpjserver.config.HttpJConfigurationManager;
import com.httpjserver.socket.HttpJServerSocket;

import java.io.IOException;

public class MainRunner {

    public static void main(String[] args) throws IOException {
        HttpJConfigurationManager httpJConfigurationManager = HttpJConfigurationManager.getHttpJConfigurationManager();
        httpJConfigurationManager.loadConfigurations("application.yaml");
        HttpJServerSocket httpJServerSocket = new HttpJServerSocket(httpJConfigurationManager.getConfiguration());
        httpJServerSocket.startServer();

    }
}
