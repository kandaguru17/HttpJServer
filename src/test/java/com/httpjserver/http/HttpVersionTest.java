package com.httpjserver.http;

import com.httpjserver.core.parser.HttpJParsingException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class HttpVersionTest {

    @ParameterizedTest
    @ValueSource(strings = {"HTTP/1.1", "HTTP/1.8"})
    void getCompatibleHttpVersion_returnCorrectHttpVersion(String versionLiteral) throws HttpJParsingException {
        HttpVersion compatibleHttpVersion = HttpVersion.getCompatibleHttpVersion(versionLiteral);
        assertEquals(HttpVersion.HTTP_1_1, compatibleHttpVersion);
    }

    @Test
    void getCompatibleHttpVersion_returnNull() throws HttpJParsingException {
        assertNull(HttpVersion.getCompatibleHttpVersion("HTTP/2.8"));
    }
}