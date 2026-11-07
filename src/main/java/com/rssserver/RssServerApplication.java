package com.rssserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point. Equivalent of Go's main.go — Spring Boot wires up the
 * embedded HTTP server, dependency injection, and graceful shutdown
 * (SIGTERM/SIGINT handling is built in) for us, so this class only
 * needs to boot the context.
 */
@SpringBootApplication
public class RssServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(RssServerApplication.class, args);
    }
}
