package com.httpjserver.core;

import com.httpjserver.util.HttpJUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class HttpJServerWorker extends Thread {

    private static final Logger LOG = LoggerFactory.getLogger(HttpJServerWorker.class.getName());

    private final Socket socket;

    public HttpJServerWorker(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (InputStream inputStream = socket.getInputStream();
             OutputStream outputStream = socket.getOutputStream()) {

            final String responseBody = HttpJUtil.writeValueAsStringFromFile("index.html");
            final String CRLF = "\n\r";

            int _byte;
            while ((_byte = inputStream.read()) >= 0) {
                System.err.print((char) _byte);
            }


            var response = "HTTP/1.1 200 OK" + CRLF +
                    "Content-Length: " + responseBody.getBytes().length + CRLF + CRLF
                    + responseBody + CRLF + CRLF;

            outputStream.write(response.getBytes());
        } catch (IOException ex) {
            LOG.error("Error Occurred in serving the request {}", ex.getMessage(), ex);
        } finally {
            try {
                if (socket != null) socket.close();
            } catch (IOException e) {
                LOG.error("Failed to close the socket due to  {} ", e.getMessage(), e);
                throw new RuntimeException("Failed to close the socket", e);
            }
        }
    }
}
