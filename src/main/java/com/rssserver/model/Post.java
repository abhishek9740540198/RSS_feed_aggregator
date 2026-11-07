package com.rssserver.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

/** JPA entity for the `posts` table. */
@Entity
@Table(name = "posts")
public class Post {

    @Id
    @JsonProperty("id")
    private UUID id;

    @Column(nullable = false, length = 255)
    @JsonProperty("title")
    private String title;

    @Column(columnDefinition = "TEXT")
    @JsonProperty("description")
    private String description;

    @Column(name = "created_at", nullable = false)
    @JsonProperty("created_at")
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    @JsonProperty("updated_at")
    private Instant updatedAt;

    @Column(name = "published_at", nullable = false)
    @JsonProperty("published_at")
    private Instant publishedAt;

    @Column(nullable = false, unique = true, columnDefinition = "TEXT")
    @JsonProperty("url")
    private String url;

    @Column(name = "feed_id", nullable = false)
    @JsonProperty("feed_id")
    private UUID feedId;

    public Post() {
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
    public Instant getPublishedAt() { return publishedAt; }
    public void setPublishedAt(Instant publishedAt) { this.publishedAt = publishedAt; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public UUID getFeedId() { return feedId; }
    public void setFeedId(UUID feedId) { this.feedId = feedId; }
}
