package com.rssserver.service;

import com.rssserver.config.AppProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

/** Equivalent of auth.go's GenerateJWT/ValidateJWT. */
@Service
public class JwtService {

    private static final Duration TOKEN_TTL = Duration.ofMinutes(15);

    private final AppProperties appProperties;

    public JwtService(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    private SecretKey key() {
        return Keys.hmacShaKeyFor(appProperties.getJwtSecret().getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(String userId, String email, String role) {
        Instant now = Instant.now();
        return Jwts.builder()
                .claim("user_id", userId)
                .claim("email", email)
                .claim("role", role)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(TOKEN_TTL)))
                .signWith(key())
                .compact();
    }

    /** Returns null if the token is missing, malformed, or expired. */
    public Claims validateToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(key())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            return null;
        }
    }
}
