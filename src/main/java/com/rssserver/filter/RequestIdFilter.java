package com.rssserver.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/** Equivalent of middleware_requestid.go. */
@Component
public class RequestIdFilter extends OncePerRequestFilter implements Ordered {

    @Override
    protected void doFilterInternal(jakarta.servlet.http.HttpServletRequest request,
                                     HttpServletResponse response,
                                     FilterChain chain) throws ServletException, IOException {
        String id = UUID.randomUUID().toString();
        response.setHeader("X-Request-ID", id);
        chain.doFilter(request, response);
    }

    @Override
    public int getOrder() {
        return 30;
    }
}
