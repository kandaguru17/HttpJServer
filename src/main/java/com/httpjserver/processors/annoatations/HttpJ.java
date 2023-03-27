package com.httpjserver.processors.annoatations;

import com.httpjserver.http.HttpRequest;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface HttpJ {

    HttpRequest.HttpMethod method();

    String path();
}
