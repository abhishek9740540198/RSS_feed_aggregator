package com.rssserver.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

/** Request body for POST /v1/feedfollows. */
public class CreateFeedFollowRequest {

    @JsonProperty("feed_id")
    private UUID feedId;

    public UUID getFeedId() {
        return feedId;
    }

    public void setFeedId(UUID feedId) {
        this.feedId = feedId;
    }
}
