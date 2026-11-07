package com.rssserver.exception;

import org.springframework.http.HttpStatus;

/**
 * Carries an HTTP status + message so any layer can throw and have
 * GlobalExceptionHandler turn it into the same {"error": "..."} JSON
 * shape that Go's respondWithError() produced.
 */
public class ApiException extends RuntimeException {

    private final HttpStatus status;

    public ApiException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
