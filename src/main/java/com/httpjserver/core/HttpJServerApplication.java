package com.httpjserver.core;

import com.httpjserver.config.HttpJConfiguration;
import com.httpjserver.config.HttpJConfigurationManager;
import com.httpjserver.core.socket.HttpJServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpJServerApplication {

    private static final Logger LOG = LoggerFactory.getLogger(HttpJServerApplication.class);

    private static void startHttpJServer(HttpJConfigurationManager httpJConfigurationManager) throws IOException {
        ExecutorService executorService = Executors.newFixedThreadPool(1, r -> new Thread(r, "httpjserver-thread"));
        HttpJConfiguration configuration = httpJConfigurationManager.getConfiguration();
        ServerSocket serverSocket = new ServerSocket(configuration.port(),10);
        initializeShutDownHook(serverSocket);
        executorService.execute(new HttpJServer(serverSocket, configuration));
        executorService.shutdown();
    }

    private static void initializeShutDownHook(ServerSocket serverSocket) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            LOG.info("Received Shut down signal");
            LOG.info("closing the server socket");
            try {
                if (serverSocket != null) serverSocket.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }));
    }

    private static HttpJConfigurationManager loadApplicationConfig() {
        HttpJConfigurationManager httpJConfigurationManager = HttpJConfigurationManager.getHttpJConfigurationManager();
        httpJConfigurationManager.loadConfigurations("application.yaml");
        return httpJConfigurationManager;
    }

    public static void start() {
        HttpJConfigurationManager httpJConfigurationManager = loadApplicationConfig();
        try {
            startHttpJServer(httpJConfigurationManager);
        } catch (IOException e) {
            LOG.error("Error Occurred in starting the server due to {}", e.getMessage());
        }
    }
}
