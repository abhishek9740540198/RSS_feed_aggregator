package com.rssserver.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/** Response body for POST /v1/login. */
public class LoginResponse {

    @JsonProperty("token")
    private final String token;

    public LoginResponse(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
