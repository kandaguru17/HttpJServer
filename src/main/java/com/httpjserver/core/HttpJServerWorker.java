package com.httpjserver.core;

import com.httpjserver.core.parser.HttpJParser;
import com.httpjserver.http.HttpParsingException;
import com.httpjserver.http.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class HttpJServerWorker extends Thread {

    private static final Logger LOG = LoggerFactory.getLogger(HttpJServerWorker.class.getName());
    public static final String CRLF = "\r\n";

    private final Socket socket;

    private final HttpJParser httpJParser;

    public HttpJServerWorker(Socket socket) {
        this.socket = socket;
        httpJParser = new HttpJParser();
    }

    @Override
    public void run() {
        try (InputStream inputStream = socket.getInputStream();
             OutputStream outputStream = socket.getOutputStream()) {

            HttpRequest httpRequest = httpJParser.parseRequest(inputStream);

            final String responseBody = "Received " + httpRequest.getVersion()
                    + " " + httpRequest.getHost()
                    + " " + httpRequest.getResourcePath()
                    + " " + httpRequest.getRequestBody();


            var response = "HTTP/1.1 200 OK" + CRLF +
                    "Content-Length: " + responseBody.getBytes().length + CRLF + CRLF
                    + responseBody + CRLF + CRLF;

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | HttpParsingException ex) {
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
