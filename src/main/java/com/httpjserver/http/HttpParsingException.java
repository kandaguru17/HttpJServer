package com.httpjserver.http;

public class HttpParsingException extends Exception {

    private final HttpStatusCode statusCode;

    public HttpParsingException(HttpStatusCode statusCode) {
        super(statusCode.getMessage());
        this.statusCode = statusCode;
    }

    public int getErrorCode() {
        return statusCode.getCode();
    }

    public String getMessage() {
        return statusCode.getMessage();
    }
}
