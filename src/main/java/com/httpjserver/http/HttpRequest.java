package com.httpjserver.http;

import com.httpjserver.core.parser.HttpJParsingException;

import java.util.HashMap;
import java.util.Map;

/**
 * DeSerialized Http request object to pass to downstream layers.
 */
public class HttpRequest {

    /**
     * Http method of the incoming request.
     */
    public enum HttpMethod {
        GET, POST, HEAD, OPTIONS, PUT, PATCH, DELETE;
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

    public String getRequestBody() {
        return requestBody;
    }

    public HttpRequest method(String method) throws HttpJParsingException {
        try {
            if (method.length() > HttpMethod.MAX_LENGTH) {
                throw new HttpJParsingException(HttpStatusCode.NOT_IMPLEMENTED);
            }
            this.method = HttpMethod.valueOf(method);
            return this;
        } catch (Exception e) {
            throw new HttpJParsingException(HttpStatusCode.NOT_IMPLEMENTED);
        }
    }

    public HttpRequest resourcePath(String resourcePath) throws HttpJParsingException {
        if (resourcePath == null || resourcePath.isBlank()) {
            throw new HttpJParsingException(HttpStatusCode.BAD_REQUEST);
        }
        this.resourcePath = resourcePath;
        return this;
    }

    public HttpRequest version(String version) throws HttpJParsingException {
        this.version = version;
        this.compatibleVersion = HttpVersion.getCompatibleHttpVersion(version);
        if (compatibleVersion == null) {
            throw new HttpJParsingException(HttpStatusCode.HTTP_VERSION_NOT_SUPPORTED);
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

}
