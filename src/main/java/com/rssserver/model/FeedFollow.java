package com.rssserver.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.Instant;
import java.util.UUID;

/** JPA entity for the `feed_follows` table. */
@Entity
@Table(name = "feed_follows", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "feed_id"}))
public class FeedFollow {

    @Id
    @JsonProperty("id")
    private UUID id;

    @Column(name = "created_at", nullable = false)
    @JsonProperty("created_at")
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    @JsonProperty("updated_at")
    private Instant updatedAt;

    @Column(name = "user_id", nullable = false)
    @JsonProperty("user_id")
    private UUID userId;

    @Column(name = "feed_id", nullable = false)
    @JsonProperty("feed_id")
    private UUID feedId;

    public FeedFollow() {
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public UUID getFeedId() { return feedId; }
    public void setFeedId(UUID feedId) { this.feedId = feedId; }
}
