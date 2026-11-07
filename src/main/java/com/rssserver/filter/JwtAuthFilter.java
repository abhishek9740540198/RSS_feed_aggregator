package com.rssserver.filter;

import com.rssserver.exception.ApiException;
import com.rssserver.model.User;
import com.rssserver.repository.UserRepository;
import com.rssserver.service.JwtService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * Equivalent of middleware_jwt.go's middlewareJWT. Registered only on
 * GET /v1/posts (see FilterConfig), mirroring the Go route wiring.
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    public JwtAuthFilter(JwtService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || authHeader.isEmpty()) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Missing Authorization header");
        }
        String prefix = "Bearer ";
        if (!authHeader.startsWith(prefix)) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Invalid Authorization header");
        }
        String tokenString = authHeader.substring(prefix.length());

        Claims claims = jwtService.validateToken(tokenString);
        if (claims == null) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Invalid token");
        }

        UUID userId;
        try {
            userId = UUID.fromString(claims.get("user_id", String.class));
        } catch (Exception e) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Invalid user id");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "User not found"));

        request.setAttribute(ApiKeyAuthFilter.USER_ATTRIBUTE, user);
        chain.doFilter(request, response);
    }
}
