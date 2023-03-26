package com.httpjserver.http;

/**
 * Response Status codes of the HttpResponse
 */
public enum HttpStatusCode {

    BAD_REQUEST(400, "Bad Request"),
    METHOD_NOT_ALLOWED(405, "Method Not Allowed"),
    URI_TOO_LONG(414, "URI too long"),

    INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
    NOT_IMPLEMENTED(501, "Not Implemented"),
    HTTP_VERSION_NOT_SUPPORTED(505, "HTTP Version Not Supported");


    private final int code;
    private final String message;

    HttpStatusCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public int getCode() {
        return code;
    }
}
