package com.httpjserver.core.socket;

import com.httpjserver.config.HttpJConfiguration;
import com.httpjserver.core.worker.HttpJServerWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class HttpJServer implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(HttpJServer.class.getName());
    private static final String THREAD_NAME = "httpjserver-worker-";
    private final AtomicInteger threadCount = new AtomicInteger(1);
    private final ServerSocket serverSocket;
    private final HttpJConfiguration configuration;

    public HttpJServer(ServerSocket serverSocket, HttpJConfiguration configuration) {
        this.serverSocket = serverSocket;
        this.configuration = configuration;
    }

    public void startServer() {
        int poolSize = configuration.threadPoolSize() == null ? Runtime.getRuntime().availableProcessors() * 2 + 1 :
                configuration.threadPoolSize();

        ExecutorService executorService = Executors.newFixedThreadPool(poolSize, r -> new Thread(r, THREAD_NAME
                + threadCount.getAndIncrement()));

        try {
            LOG.info("Started server on port " + configuration.port() + "...");
            while (serverSocket.isBound() && !serverSocket.isClosed()) {
                if (!serverSocket.isClosed()) {
                    Socket socket = serverSocket.accept();
                    executorService.execute(new HttpJServerWorker(socket));
                }
            }
        } catch (IOException e) {
            LOG.error("IO Exception occurred in accepting connections from Client", e);
        }
    }


    @Override
    public void run() {
        LOG.info("Initializing server with properties size of Port :{}; threadPoolSize: {}", configuration.port(), configuration.threadPoolSize());
        startServer();
    }
}
