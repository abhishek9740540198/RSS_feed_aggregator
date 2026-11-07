package com.rssserver.filter;

import com.google.common.util.concurrent.RateLimiter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Equivalent of middleware_ratelimit.go (golang.org/x/time/rate.NewLimiter(10, 20):
 * 10 requests/sec sustained). Guava's RateLimiter approximates the sustained
 * rate; burst behavior differs slightly from Go's token bucket but is a
 * reasonable equivalent for a single-instance rate limiter.
 */
@Component
public class RateLimitFilter extends OncePerRequestFilter implements Ordered {

    private static final Logger log = LoggerFactory.getLogger(RateLimitFilter.class);
    private final RateLimiter limiter = RateLimiter.create(10.0);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        if (!limiter.tryAcquire()) {
            log.warn("Too many requests path={} method={}", request.getRequestURI(), request.getMethod());
            response.setStatus(HttpServletResponse.SC_TOO_MANY_REQUESTS);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Too many requests\"}");
            return;
        }
        chain.doFilter(request, response);
    }

    @Override
    public int getOrder() {
        return 10;
    }
}
