package com.httpjserver.core.parser;

import com.httpjserver.http.HttpRequest;
import com.httpjserver.http.HttpStatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * The core parser class that pareses the incoming request (input stream).
 */
public class HttpJParser {

    private static final Logger LOG = LoggerFactory.getLogger(HttpJParser.class.getName());
    public static final String CRLF = "\r\n";
    public static final String SP = " ";


    /**
     * Method to parse the input Stream of the incoming request
     *
     * @param is the inputStream from the client {@link java.net.Socket} object
     * @return instance of pared {@link HttpRequest}
     * @throws HttpJParsingException thrown when there is any parsing error
     */
    public HttpRequest parseRequest(InputStream is) throws HttpJParsingException {
        // using input stream reader as it deals well with converting byte to character
        HttpRequest httpRequest = new HttpRequest();
        parseInputStream(is, httpRequest);
        return httpRequest;
    }


    private void parseInputStream(InputStream is, HttpRequest httpRequest) throws HttpJParsingException {
        try {
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
            final StringBuilder reqBuilder = new StringBuilder();

            // parse request line
            String request = bufferedReader.readLine();
            parseRequestLine(request, httpRequest);

            LOG.debug("Request Line parsed {} {} {}", httpRequest.getMethod(),
                    httpRequest.getResourcePath(),
                    httpRequest.getVersion());

            // parse the headers
            while (request != null && !request.isEmpty()) {
                reqBuilder.append(request).append(CRLF);
                request = bufferedReader.readLine();
            }
            String[] requestParts = reqBuilder.toString().split(CRLF);
            parseRequestHeader(requestParts, httpRequest);

            LOG.debug("Request Headers parsed {}", httpRequest.getHeaders());

            // parse request body
            parseRequestBody(bufferedReader, httpRequest);
            LOG.debug("Request Body parsed {}", httpRequest.getRequestBody() == null ? "" : httpRequest.getRequestBody());


        } catch (IOException e) {
            LOG.error("Error occurred in parsing the request line", e);
            throw new HttpJParsingException(HttpStatusCode.INTERNAL_SERVER_ERROR);
        }
    }

    private void parseRequestLine(String requestLine, HttpRequest httpRequest) throws HttpJParsingException {
        String[] requestLineParts;
        if (requestLine == null || requestLine.isBlank() || (requestLineParts = requestLine.split(SP)).length > 3) {
            LOG.error("RequestLine is malformed, it could be either null, empty or not as per RFC spec");
            throw new HttpJParsingException(HttpStatusCode.BAD_REQUEST);
        }

        httpRequest.method(requestLineParts[0])
                .resourcePath(requestLineParts[1])
                .version(requestLineParts[2]);
    }

    private void parseRequestHeader(String[] requestParts, HttpRequest httpRequest) {
        final Map<String, String> headerMap = new HashMap<>();
        for (int i = 1; i < requestParts.length; i++) {
            if (requestParts[i].isBlank()) {
                break;
            }
            String[] header = requestParts[i].split(":");
            headerMap.put(header[0].trim(), header[1].trim());
        }
        httpRequest.headers(headerMap);
    }

    private void parseRequestBody(BufferedReader bufferedReader, HttpRequest httpRequest) {
        StringBuilder body = new StringBuilder();
        int count = 0;
        int contentLength = Integer.parseInt(httpRequest.getHeaders().getOrDefault("Content-Length", "0"));
        while (count < contentLength) {
            char[] buffer = new char[contentLength - count];
            int read;
            try {
                read = bufferedReader.read(buffer);
                count += read;
                body.append(buffer, 0, read);
            } catch (IOException e) {
                LOG.error("Error Occurred in parsing the request body");
            }
            httpRequest.requestBody(body.toString());
        }
    }
}
