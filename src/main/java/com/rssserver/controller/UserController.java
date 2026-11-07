package com.rssserver.controller;

import com.rssserver.dto.CreateUserRequest;
import com.rssserver.exception.ApiException;
import com.rssserver.model.User;
import com.rssserver.repository.UserRepository;
import com.rssserver.security.AuthUser;
import com.rssserver.service.PasswordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.UUID;

/** Equivalent of handlerUser.go's handlerCreateUser/handlerGetUsersByApiKey. */
@RestController
@RequestMapping("/v1/users")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final UserRepository userRepository;
    private final PasswordService passwordService;

    public UserController(UserRepository userRepository, PasswordService passwordService) {
        this.userRepository = userRepository;
        this.passwordService = passwordService;
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody CreateUserRequest req) {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setCreatedAt(Instant.now());
        user.setUpdatedAt(Instant.now());
        user.setName(req.getName());
        user.setEmail(req.getEmail());
        user.setPasswordHash(passwordService.hash(req.getPassword()));
        user.setApiKey(generateApiKey());
        user.setRole("user");

        try {
            user = userRepository.save(user);
        } catch (Exception e) {
            log.error("failed creating user", e);
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @GetMapping
    public User getCurrentUser(@AuthUser User user) {
        return user;
    }

    // Postgres previously generated this via encode(sha256(random()::text::bytea), 'hex');
    // replicated here with a cryptographically secure random source + SHA-256.
    private String generateApiKey() {
        try {
            SecureRandom random = new SecureRandom();
            byte[] bytes = new byte[32];
            random.nextBytes(bytes);
            byte[] hash = MessageDigest.getInstance("SHA-256").digest(bytes);
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
