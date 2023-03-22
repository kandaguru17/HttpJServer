package com.httpjserver.http;

import java.util.HashMap;
import java.util.Map;

public class HttpRequest {


    public enum HttpMethod {
        GET, POST, HEAD;
        public static final int MAX_LENGTH;

        static {
            int length = Integer.MIN_VALUE;
            for (var method : HttpRequest.HttpMethod.values()) {
                length = Math.max(length, method.name().length());
            }
            MAX_LENGTH = length;
        }
    }

    private HttpMethod method;
    private String resourcePath;
    private String version;
    private HttpVersion compatibleVersion;
    private Map<String, String> headers;
    private String requestBody;
    private String host;


    public HttpMethod getMethod() {
        return method;
    }

    public String getResourcePath() {
        return resourcePath;
    }

    public String getVersion() {
        return version;
    }

    public HttpVersion getCompatibleVersion() {
        return compatibleVersion;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getHost() {
        return host;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public HttpRequest method(String method) throws HttpParsingException {
        try {
            this.method = HttpMethod.valueOf(method);
            return this;
        } catch (Exception e) {
            throw new HttpParsingException(HttpStatusCode.NOT_IMPLEMENTED);
        }
    }

    public HttpRequest resourcePath(String resourcePath) throws HttpParsingException {
        if (resourcePath == null || resourcePath.isBlank()) {
            throw new HttpParsingException(HttpStatusCode.BAD_REQUEST);
        }
        this.resourcePath = resourcePath;
        return this;
    }

    public HttpRequest version(String version) throws HttpParsingException {
        this.version = version;
        this.compatibleVersion = HttpVersion.getCompatibleHttpVersion(version);
        if (compatibleVersion == null) {
            throw new HttpParsingException(HttpStatusCode.HTTP_VERSION_NOT_SUPPORTED);
        }
        return this;
    }

    public HttpRequest headers(Map<String, String> headers) {
        if (headers == null) {
            headers = new HashMap<>();
        }
        this.headers = headers;
        return this;
    }

    public HttpRequest requestBody(String requestBody) {
        this.requestBody = requestBody;
        return this;
    }

    public HttpRequest host(String host) {
        this.host = host;
        return this;
    }

}
