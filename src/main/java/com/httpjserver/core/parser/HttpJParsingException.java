package com.httpjserver.core.parser;

import com.httpjserver.http.HttpStatusCode;

public class HttpJParsingException extends Exception {

    private final HttpStatusCode statusCode;

    public HttpJParsingException(HttpStatusCode statusCode) {
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
