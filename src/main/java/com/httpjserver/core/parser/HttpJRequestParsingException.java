package com.httpjserver.core.parser;

public class HttpJRequestParsingException extends RuntimeException {

    public HttpJRequestParsingException(String message, Exception e) {
        super(message, e);
    }
}
