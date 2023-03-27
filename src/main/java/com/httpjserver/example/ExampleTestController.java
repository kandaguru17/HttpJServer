package com.httpjserver.example;


import com.httpjserver.http.HttpRequest;
import com.httpjserver.processors.annoatations.HttpJ;


public class ExampleTestController {

    @HttpJ(method = HttpRequest.HttpMethod.GET, path = "/")
    public void handleGetRequest(HttpRequest request) {

    }
}
