package com.httpjserver.socket;

import com.httpjserver.config.HttpJConfiguration;
import com.httpjserver.core.HttpJServerWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpJServerSocket {

    private static final Logger LOG = LoggerFactory.getLogger(HttpJServerSocket.class.getName());

    private final HttpJConfiguration configuration;
    private final ServerSocket serverSocket;

    public HttpJServerSocket(HttpJConfiguration configuration) throws IOException {
        this.configuration = configuration;
        this.serverSocket = new ServerSocket(configuration.port());
    }

    public void startServer() {
        try {
            LOG.info("Started server on port " + configuration.port() + "...");
            while (serverSocket.isBound() && !serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                new HttpJServerWorker(socket).start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    LOG.error("Error occurred in closing the server socket", e);
                }
            }
        }
    }
}
