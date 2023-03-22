package httpJserver.core.parser;

import com.httpjserver.core.parser.HttpJParser;
import com.httpjserver.http.HttpParsingException;
import com.httpjserver.http.HttpRequest;
import com.httpjserver.http.HttpVersion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static com.httpjserver.http.HttpStatusCode.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class HttpJParserTest {

    private static final String CRLF = "\r\n";
    private HttpJParser httpJParser;

    private InputStream is;

    @BeforeEach
    void setUp() {
        httpJParser = new HttpJParser();
    }

    @Test
    void parseHttpJParser_validRequest() throws HttpParsingException {
        is = new ByteArrayInputStream(generateTestCase("GET", "HTTP/1.1"));
        HttpRequest httpRequest = httpJParser.parseRequest(is);
        assertEquals(HttpRequest.HttpMethod.GET, httpRequest.getMethod());
        assertEquals("/", httpRequest.getResourcePath());
        assertEquals("HTTP/1.1", httpRequest.getVersion());
        assertEquals(HttpVersion.HTTP_1_1, httpRequest.getCompatibleVersion());
        assertEquals("localhost", httpRequest.getHost());
        assertEquals(13, httpRequest.getHeaders().size());
    }

    @ParameterizedTest
    @ValueSource(strings = {"OPTIONS", "LONG_METHOD_NAME"})
    void parseHttpJParser_notImplemented(String method) {
        is = new ByteArrayInputStream(generateTestCase(method, "HTTP/1.1"));
        final var ex = assertThrows(HttpParsingException.class, () -> httpJParser.parseRequest(is));
        assertEquals(501, ex.getErrorCode());
        assertEquals(NOT_IMPLEMENTED.getMessage(), ex.getMessage());
    }


    @Test
    void parseHttpJParser_badRequest() {
        is = new ByteArrayInputStream(generateInvalidRequestLineItems("GET"));
        final var ex = assertThrows(HttpParsingException.class, () -> httpJParser.parseRequest(is));
        assertEquals(400, ex.getErrorCode());
        assertEquals(BAD_REQUEST.getMessage(), ex.getMessage());
    }

    @Test
    void parseHttpJParser_emptyRequestLine_badRequest() {
        is = new ByteArrayInputStream(generateEmptyRequestLineItems());
        final var ex = assertThrows(HttpParsingException.class, () -> httpJParser.parseRequest(is));
        assertEquals(400, ex.getErrorCode());
        assertEquals(BAD_REQUEST.getMessage(), ex.getMessage());
    }

    @Test
    void parseHttpJParser_badHttpVersion() {
        is = new ByteArrayInputStream(generateTestCase("GET", "HTTP/5.1"));
        final var ex = assertThrows(HttpParsingException.class, () -> httpJParser.parseRequest(is));
        assertEquals(505, ex.getErrorCode());
        assertEquals(HTTP_VERSION_NOT_SUPPORTED.getMessage(), ex.getMessage());
    }

    @Test
    void parseHttpJParser_HigherMinorVersion() throws HttpParsingException {
        is = new ByteArrayInputStream(generateTestCase("GET", "HTTP/1.7"));
        HttpRequest httpRequest = httpJParser.parseRequest(is);
        assertEquals(HttpRequest.HttpMethod.GET, httpRequest.getMethod());
        assertEquals("/", httpRequest.getResourcePath());
        assertEquals(HttpVersion.HTTP_1_1, httpRequest.getCompatibleVersion());
    }


    @Test
    void parseHttpJParser_withRequestBody() throws HttpParsingException {
        var httpRequestBody = "{\"Hello\" : \"World\"}";
        is = new ByteArrayInputStream(generateTestCaseWithBody("POST", "HTTP/1.1", httpRequestBody));
        HttpRequest httpRequest = httpJParser.parseRequest(is);
        assertEquals(HttpRequest.HttpMethod.POST, httpRequest.getMethod());
        assertEquals("/", httpRequest.getResourcePath());
        assertEquals(HttpVersion.HTTP_1_1, httpRequest.getCompatibleVersion());
        assertEquals("localhost", httpRequest.getHost());
        assertEquals(13, httpRequest.getHeaders().size());
        assertEquals(httpRequestBody, httpRequest.getRequestBody());
    }

    private byte[] generateTestCaseWithBody(String method, String httpVersion, String body) {
        return (new String(generateTestCase(method, httpVersion)) + CRLF
                + CRLF
                + body).getBytes();
    }


    private byte[] generateTestCase(String method, String httpVersion) {
        final var str = method + " / " + httpVersion + CRLF +
                "Host: localhost:8080" + CRLF +
                "Connection: keep-alive" + CRLF +
                "sec-ch-ua: \"Google Chrome\";v=\"111\", \"Not(A:Brand\";v=\"8\", \"Chromium\";v=\"111\"" + CRLF +
                "sec-ch-ua-mobile: ?0" + CRLF +
                "sec-ch-ua-platform: \"macOS\"" + CRLF +
                "Upgrade-Insecure-Requests: 1" + CRLF +
                "User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/111.0.0.0 Safari/537.36" + CRLF +
                "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7" + CRLF +
                "Sec-Fetch-Site: none" + CRLF +
                "Sec-Fetch-Mode: navigate" + CRLF +
                "Sec-Fetch-User: ?1" + CRLF +
                "Sec-Fetch-Dest: document" + CRLF +
                "Accept-Encoding: gzip, deflate, br" + CRLF +
                "Accept-Language: en-GB,en-US;q=0.9,en;q=0.8";
        return str.getBytes();
    }

    private byte[] generateInvalidRequestLineItems(String method) {
        final var str = method + " INVALID_ITEM / HTTP/1.1" + CRLF +
                "Host: localhost:8080" + CRLF +
                "Connection: keep-alive" + CRLF +
                "sec-ch-ua: \"Google Chrome\";v=\"111\", \"Not(A:Brand\";v=\"8\", \"Chromium\";v=\"111\"" + CRLF +
                "sec-ch-ua-mobile: ?0" + CRLF +
                "sec-ch-ua-platform: \"macOS\"" + CRLF +
                "Upgrade-Insecure-Requests: 1" + CRLF +
                "User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/111.0.0.0 Safari/537.36" + CRLF +
                "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7" + CRLF +
                "Sec-Fetch-Site: none" + CRLF +
                "Sec-Fetch-Mode: navigate" + CRLF +
                "Sec-Fetch-User: ?1" + CRLF +
                "Sec-Fetch-Dest: document" + CRLF +
                "Accept-Encoding: gzip, deflate, br" + CRLF +
                "Accept-Language: en-GB,en-US;q=0.9,en;q=0.8";
        return str.getBytes();
    }


    private byte[] generateEmptyRequestLineItems() {
        final var str = CRLF +
                "Host: localhost:8080" + CRLF +
                "Connection: keep-alive" + CRLF +
                "sec-ch-ua: \"Google Chrome\";v=\"111\", \"Not(A:Brand\";v=\"8\", \"Chromium\";v=\"111\"" + CRLF +
                "sec-ch-ua-mobile: ?0" + CRLF +
                "sec-ch-ua-platform: \"macOS\"" + CRLF +
                "Upgrade-Insecure-Requests: 1" + CRLF +
                "User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/111.0.0.0 Safari/537.36" + CRLF +
                "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7" + CRLF +
                "Sec-Fetch-Site: none" + CRLF +
                "Sec-Fetch-Mode: navigate" + CRLF +
                "Sec-Fetch-User: ?1" + CRLF +
                "Sec-Fetch-Dest: document" + CRLF +
                "Accept-Encoding: gzip, deflate, br" + CRLF +
                "Accept-Language: en-GB,en-US;q=0.9,en;q=0.8";
        return str.getBytes();
    }

}
