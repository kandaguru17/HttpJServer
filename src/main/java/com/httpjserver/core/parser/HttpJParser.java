package com.httpjserver.core.parser;

import com.httpjserver.http.HttpParsingException;
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

public class HttpJParser {

    private static final Logger LOG = LoggerFactory.getLogger(HttpJParser.class.getName());
    public static final String CRLF = "\r\n";
    public static final String SP = " ";


    public HttpRequest parseRequest(InputStream is) throws HttpParsingException {
        // using input stream reader as it deals well with converting byte to character
        HttpRequest httpRequest = new HttpRequest();
        parseInputStream(is, httpRequest);
        return httpRequest;
    }

    private void parseInputStream(InputStream is, HttpRequest httpRequest) throws HttpParsingException {
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
        try {
            int request;
            final StringBuilder reqBuilder = new StringBuilder();
            while (bufferedReader.ready() && (request = bufferedReader.read()) >= 0) {
                reqBuilder.append((char) request);
            }

            LOG.debug("Http request received : \n{}", reqBuilder);

            String[] requestParts = reqBuilder.toString().split(CRLF);

            parseRequestLine(requestParts, httpRequest);
            parseRequestHeader(requestParts, httpRequest);
            parseRequestBody(requestParts, httpRequest);

        } catch (IOException e) {
            LOG.error("Error occurred in parsing the request line", e);
            throw new HttpJRequestParsingException(e.getMessage(), e);
        }
    }

    private void parseRequestLine(String[] requestParts, HttpRequest httpRequest) throws HttpParsingException {
        String requestLine = requestParts[0];
        String[] requestLineParts;
        if (requestLine == null || requestLine.isBlank() || (requestLineParts = requestLine.split(SP)).length > 3) {
            LOG.error("RequestLine is malformed, it could be either null, empty or not as per RFC spec");
            throw new HttpParsingException(HttpStatusCode.BAD_REQUEST);
        }

        httpRequest.method(requestLineParts[0])
                .resourcePath(requestLineParts[1])
                .version(requestLineParts[2])
                .host(requestParts[1].split(":")[1].trim());
    }


    private void parseRequestHeader(String[] requestParts, HttpRequest httpRequest) {
        final Map<String, String> headerMap = new HashMap<>();
        for (int i = 2; i < requestParts.length; i++) {
            if (requestParts[i].isBlank()) {
                break;
            }
            String[] header = requestParts[i].split(":");
            headerMap.put(header[0].trim(), header[1].trim());
        }
        httpRequest.headers(headerMap);
    }

    private void parseRequestBody(String[] requestParts, HttpRequest httpRequest) throws HttpParsingException {
        if (httpRequest.getMethod().equals(HttpRequest.HttpMethod.GET)) {
            return;
        }
        // a CRLF expected before the request body and after the
        if (requestParts[requestParts.length - 2].isBlank()) {
            httpRequest.requestBody(requestParts[requestParts.length - 1]);
        } else {
            LOG.error("Error in parsing the request body");
            throw new HttpParsingException(HttpStatusCode.BAD_REQUEST);
        }
    }
}
