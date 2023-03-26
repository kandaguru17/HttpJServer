package com.httpjserver.intg;

import com.httpjserver.core.HttpJServerApplication;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class HttpJServerApplicationTest {

    public static final String HOST = "http://localhost:8080/";

    ExecutorService service = Executors.newFixedThreadPool(5);


    @BeforeAll
    void setUp() {
        HttpJServerApplication.start();
    }

    /**
     * Testing concurrent HTTP requests to the server
     *
     * @throws InterruptedException throws thread interruption exception
     */
    @Test
    void concurrentHttpRequest_shouldServeAllTheRequests() throws InterruptedException, ExecutionException {

        // prepare
        CountDownLatch latch = new CountDownLatch(5);
        String reqBody = "{\"hello\": \"World\"}";
        final List<String> responseListToAssert = new ArrayList<>();
        Callable<HttpResponse<String>> httpCallable = createHttpInvocationCallable(latch, reqBody);

        // execute
        for (int i = 0; i < 10; i++) {
            Future<HttpResponse<String>> future = service.submit(httpCallable);
            responseListToAssert.add(future.get().body());
        }
        // wait for all the threads to finish execution
        latch.await();

        //assert
        assertEquals(10, responseListToAssert.size());
        for (var res : responseListToAssert) {
            assertEquals(reqBody, res);
        }
    }

    private static Callable<HttpResponse<String>> createHttpInvocationCallable(CountDownLatch latch, String reqBody) {
        return () -> {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(HOST))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(reqBody))
                    .build();
            try {
                HttpClient httpClient = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build();
                return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            } catch (IOException | InterruptedException e) {
                fail();
            } finally {
                latch.countDown();
            }
            return null;
        };
    }
}
