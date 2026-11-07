package com.rssserver.config;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * Equivalent of Go's Config struct + LoadConfig()/Validate() in config.go.
 * Spring Boot binds these from application.yml (which itself reads env vars),
 * and @PostConstruct performs the same validation the Go Validate() did.
 */
@Component
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    private String jwtSecret;
    private Duration scrapeInterval = Duration.ofMinutes(1);
    private int maxConcurrency = 5;
    private String redisAddr;

    @PostConstruct
    public void validate() {
        if (jwtSecret == null || jwtSecret.isBlank()) {
            throw new IllegalStateException("JWT_SECRET is not set");
        }
        if (maxConcurrency <= 0) {
            throw new IllegalStateException("MAX_CONCURRENCY must be greater than 0");
        }
        if (maxConcurrency > 100) {
            throw new IllegalStateException("MAX_CONCURRENCY cannot exceed 100");
        }
        if (scrapeInterval.compareTo(Duration.ofSeconds(1)) < 0) {
            throw new IllegalStateException("SCRAPE_INTERVAL must be at least 1 second");
        }
    }

    public String getJwtSecret() {
        return jwtSecret;
    }

    public void setJwtSecret(String jwtSecret) {
        this.jwtSecret = jwtSecret;
    }

    public Duration getScrapeInterval() {
        return scrapeInterval;
    }

    public void setScrapeInterval(Duration scrapeInterval) {
        this.scrapeInterval = scrapeInterval;
    }

    public int getMaxConcurrency() {
        return maxConcurrency;
    }

    public void setMaxConcurrency(int maxConcurrency) {
        this.maxConcurrency = maxConcurrency;
    }

    public String getRedisAddr() {
        return redisAddr;
    }

    public void setRedisAddr(String redisAddr) {
        this.redisAddr = redisAddr;
    }
}
