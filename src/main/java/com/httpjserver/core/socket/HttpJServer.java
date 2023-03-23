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

    private final HttpJConfiguration configuration;
    private final ServerSocket serverSocket;
    private final AtomicInteger threadCount = new AtomicInteger(1);

    public HttpJServer(HttpJConfiguration configuration) throws IOException {
        this.configuration = configuration;
        this.serverSocket = new ServerSocket(configuration.port());
    }

    public void startServer() {
        int poolSize = configuration.threadPoolSize() == null ? Runtime.getRuntime().availableProcessors() * 2 + 1 :
                configuration.threadPoolSize();


        ExecutorService executorService = Executors.newFixedThreadPool(poolSize, r -> new Thread(r, THREAD_NAME
                + threadCount.getAndIncrement()));

        try {
            LOG.info("Started server on port " + configuration.port() + "...");
            while (serverSocket.isBound() && !serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                executorService.execute(new HttpJServerWorker(socket));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            shutDownService(executorService);
        }
    }

    private void shutDownService(ExecutorService executorService) {
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                LOG.error("Error occurred in closing the server socket", e);
            } finally {
                executorService.shutdown();
            }
        }
    }

    @Override
    public void run() {
        LOG.info("Initializing server with properties size of Port :{}; threadPoolSize: {}", configuration.port(), configuration.threadPoolSize());
        startServer();
    }
}
