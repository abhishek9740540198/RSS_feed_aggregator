package com.rssserver.controller;

import com.rssserver.dto.LoginRequest;
import com.rssserver.dto.LoginResponse;
import com.rssserver.exception.ApiException;
import com.rssserver.model.User;
import com.rssserver.repository.UserRepository;
import com.rssserver.service.JwtService;
import com.rssserver.service.PasswordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Equivalent of handler_login.go. */
@RestController
@RequestMapping("/v1/login")
public class LoginController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    private final UserRepository userRepository;
    private final PasswordService passwordService;
    private final JwtService jwtService;

    public LoginController(UserRepository userRepository, PasswordService passwordService, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordService = passwordService;
        this.jwtService = jwtService;
    }

    @PostMapping
    public LoginResponse login(@RequestBody LoginRequest req) {
        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));

        if (!passwordService.matches(req.getPassword(), user.getPasswordHash())) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }

        String token;
        try {
            token = jwtService.generateToken(user.getId().toString(), user.getEmail(), user.getRole());
        } catch (Exception e) {
            log.error("failed generating jwt", e);
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");
        }

        return new LoginResponse(token);
    }
}
