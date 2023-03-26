package com.httpjserver.core.worker;

import com.httpjserver.core.parser.HttpJParser;
import com.httpjserver.http.HttpParsingException;
import com.httpjserver.http.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class HttpJServerWorker implements Runnable {

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
        OutputStream outputStream = null;
        InputStream inputStream = null;
        try {
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();

            HttpRequest httpRequest = httpJParser.parseRequest(inputStream);

            // TODO: hard coded response, use the response from the FrameWork
            String responseBody = "";
            if (httpRequest.getMethod() != null)
                responseBody = httpRequest.getRequestBody() != null ? httpRequest.getRequestBody() : "<h1> Helloo !!</h1>";


            var response = "HTTP/1.1 200 OK" + CRLF +
                    "Content-Type: " + httpRequest.getHeaders().get("Content-Type") + CRLF +
                    "Content-Length: " + responseBody.getBytes().length + CRLF + CRLF
                    + responseBody + CRLF + CRLF;

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (HttpParsingException ex) {
            LOG.error("Error Occurred in serving the request {}", ex.getMessage(), ex);
            handleException(ex, outputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            closeAllStreams(outputStream, inputStream);
        }
    }

    private void closeAllStreams(OutputStream outputStream, InputStream inputStream) {
        try {
            if (socket != null) socket.close();
            if (inputStream != null) inputStream.close();
            if (outputStream != null) outputStream.close();
        } catch (IOException e) {
            // TODO: handle IO exceptions
            LOG.error("Failed to close the socket due to  {} ", e.getMessage(), e);
            throw new RuntimeException("Failed to close the socket", e);
        }
    }

    private void handleException(HttpParsingException ex, OutputStream os) {
        String response;
        String httpVersion = "HTTP/1.1";
        response = httpVersion + " " + ex.getErrorCode() + " " + ex.getMessage() + CRLF + "Content-Length: 0" + CRLF + CRLF;
        try {
            os.write(response.getBytes());
            os.flush();
        } catch (IOException e) {
            // TODO:  handle IO exceptions
            throw new RuntimeException(e);
        }
    }
}
