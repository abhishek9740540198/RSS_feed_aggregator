package com.rssserver.controller;

import com.rssserver.exception.ApiException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Equivalent of handler_health.go. */
@RestController
@RequestMapping("/v1/health")
public class HealthController {

    @PersistenceContext
    private EntityManager entityManager;

    @GetMapping("/live")
    public Map<String, String> live() {
        return Map.of("status", "alive");
    }

    @GetMapping("/ready")
    public Map<String, String> ready() {
        try {
            entityManager.createNativeQuery("SELECT 1").getSingleResult();
        } catch (Exception e) {
            throw new ApiException(HttpStatus.SERVICE_UNAVAILABLE, "Database is not reachable");
        }
        return Map.of("status", "ready");
    }
}
