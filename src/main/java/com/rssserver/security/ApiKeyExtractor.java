package com.rssserver.security;

import com.rssserver.exception.ApiException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;

/** Equivalent of internal/auth/auth.go's GetAPIkey (header "Authorization: ApiKey <key>"). */
public class ApiKeyExtractor {

    private ApiKeyExtractor() {
    }

    public static String extract(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null || header.isEmpty()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Authorization header is missing");
        }
        String[] parts = header.split(" ");
        if (parts.length != 2 || !parts[0].equals("ApiKey")) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid Authorization header format");
        }
        return parts[1];
    }
}
