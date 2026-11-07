package com.rssserver.config;

import com.rssserver.filter.ApiKeyAuthFilter;
import com.rssserver.filter.JwtAuthFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Registers the API-key and JWT auth filters only on the specific routes
 * that used them in Go's setupRouter (main.go), instead of applying them
 * globally -- this mirrors chi's per-route middleware wrapping:
 *   apiCfg.middlewareAuth(...): GET /v1/users, POST /v1/feeds, /v1/feedfollows/**
 *   apiCfg.middlewareJWT(...):  GET /v1/posts
 */
@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<ApiKeyAuthFilter> apiKeyAuthFilterRegistration(ApiKeyAuthFilter filter) {
        FilterRegistrationBean<ApiKeyAuthFilter> reg = new FilterRegistrationBean<>(filter);
        reg.addUrlPatterns("/v1/users", "/v1/feeds", "/v1/feedfollows/*");
        reg.setOrder(20);
        return reg;
    }

    @Bean
    public FilterRegistrationBean<JwtAuthFilter> jwtAuthFilterRegistration(JwtAuthFilter filter) {
        FilterRegistrationBean<JwtAuthFilter> reg = new FilterRegistrationBean<>(filter);
        reg.addUrlPatterns("/v1/posts");
        reg.setOrder(20);
        return reg;
    }
}
