package com.rssserver.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;

/**
 * Equivalent of middleware_recovery.go's recovery middleware: catches
 * anything that escapes a controller, logs it (mirroring the
 * "Recovered from panic" slog.Error call with path/method/stack), and
 * returns a uniform 500 Internal Server Error JSON body instead of
 * leaking a stack trace to the client.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<Map<String, String>> handleApiException(ApiException ex, WebRequest request) {
        if (ex.getStatus().is5xxServerError()) {
            log.error("HTTP {}: {}", ex.getStatus().value(), ex.getMessage());
        }
        return ResponseEntity.status(ex.getStatus()).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleUnexpected(Exception ex, WebRequest request) {
        log.error("Recovered from panic path={} error={}",
                request.getDescription(false), ex.toString(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Internal Server Error"));
    }
}
