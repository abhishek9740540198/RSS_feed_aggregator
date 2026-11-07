package com.rssserver.controller;

import com.rssserver.exception.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Equivalent of handlerErr.go: GET /v1/error always returns 400. */
@RestController
@RequestMapping("/v1")
public class ErrorTestController {

    @GetMapping("/error")
    public void error() {
        throw new ApiException(HttpStatus.BAD_REQUEST, "Bad request");
    }
}
