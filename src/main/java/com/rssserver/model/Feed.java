package com.rssserver.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

/** JPA entity for the `feeds` table. */
@Entity
@Table(name = "feeds")
public class Feed {

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

    @Column(nullable = false, unique = true)
    @JsonProperty("url")
    private String url;

    @Column(name = "user_id", nullable = false)
    @JsonProperty("user_id")
    private UUID userId;

    @Column(name = "last_fetched_at")
    @JsonProperty("last_fetched_at")
    private Instant lastFetchedAt;

    public Feed() {
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public Instant getLastFetchedAt() { return lastFetchedAt; }
    public void setLastFetchedAt(Instant lastFetchedAt) { this.lastFetchedAt = lastFetchedAt; }
}
