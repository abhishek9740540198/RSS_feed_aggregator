package com.rssserver.filter;

import com.rssserver.exception.ApiException;
import com.rssserver.model.User;
import com.rssserver.repository.UserRepository;
import com.rssserver.security.ApiKeyExtractor;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Equivalent of middleware_auth.go's middlewareAuth. Registered only on
 * the specific routes that used it in Go (see FilterConfig): POST/GET
 * /v1/users, POST /v1/feeds, and /v1/feedfollows/**.
 */
@Component
public class ApiKeyAuthFilter extends OncePerRequestFilter {

    public static final String USER_ATTRIBUTE = "authUser";

    private final UserRepository userRepository;

    public ApiKeyAuthFilter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        // The filter is registered on whole paths ("/v1/users", "/v1/feeds"),
        // but in the original Go router only specific methods on those paths
        // were wrapped in middlewareAuth (POST /v1/users and GET /v1/feeds
        // were left public). Replicate that exactly here.
        String uri = request.getRequestURI();
        String method = request.getMethod();
        boolean needsAuth =
                (uri.equals("/v1/users") && method.equals("GET"))
                || (uri.equals("/v1/feeds") && method.equals("POST"))
                || uri.startsWith("/v1/feedfollows");
        if (!needsAuth) {
            chain.doFilter(request, response);
            return;
        }

        String apiKey = ApiKeyExtractor.extract(request);
        User user = userRepository.findByApiKey(apiKey)
                .orElseThrow(() -> new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to get user"));
        request.setAttribute(USER_ATTRIBUTE, user);
        chain.doFilter(request, response);
    }
}
