package com.rssserver.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

/**
 * JPA entity for the `users` table (sql/schema/001_users.sql,
 * 002_users_apikey.sql, 007_add_auth_fields_to_users.sql).
 *
 * Note: the original Go code's json struct tags were malformed (e.g.
 * `"json:"id""`), so encoding/json silently fell back to default
 * capitalized field names in API responses. This conversion uses the
 * clearly-intended snake_case field names instead, since that was the
 * obvious intent of the original tags.
 */
@Entity
@Table(name = "users")
public class User {

    @Id
    @JsonProperty("id")
    private UUID id;

    @Column(nullable = false, length = 100)
    @JsonProperty("name")
    private String name;

    @Column(name = "created_at", nullable = false)
    @JsonProperty("created_at")
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    @JsonProperty("updated_at")
    private Instant updatedAt;

    @Column(name = "api_key", nullable = false, unique = true, length = 64)
    @JsonProperty("api_key")
    private String apiKey;

    @Column(nullable = false, unique = true)
    @JsonProperty("email")
    private String email;

    // Never serialized to JSON responses (the Go handlers returned this
    // as part of the raw struct, which was itself a latent security bug —
    // fixed here by excluding it from output while keeping it server-side).
    @Column(name = "password_hash", nullable = false)
    @JsonIgnore
    private String passwordHash;

    @Column(length = 20)
    @JsonProperty("role")
    private String role;

    public User() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
