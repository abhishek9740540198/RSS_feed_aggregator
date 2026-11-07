package com.rssserver.controller;

import com.rssserver.dto.CreateFeedRequest;
import com.rssserver.exception.ApiException;
import com.rssserver.model.Feed;
import com.rssserver.model.User;
import com.rssserver.repository.FeedRepository;
import com.rssserver.security.AuthUser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/** Equivalent of handler_feed.go. */
@RestController
@RequestMapping("/v1/feeds")
public class FeedController {

    private final FeedRepository feedRepository;

    public FeedController(FeedRepository feedRepository) {
        this.feedRepository = feedRepository;
    }

    @PostMapping
    public ResponseEntity<Feed> createFeed(@RequestBody CreateFeedRequest req, @AuthUser User user) {
        Feed feed = new Feed();
        feed.setId(UUID.randomUUID());
        feed.setCreatedAt(Instant.now());
        feed.setUpdatedAt(Instant.now());
        feed.setName(req.getName());
        feed.setUrl(req.getUrl());
        feed.setUserId(user.getId());

        try {
            feed = feedRepository.save(feed);
        } catch (Exception e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create feed");
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(feed);
    }

    @GetMapping
    public List<Feed> getFeeds() {
        try {
            return feedRepository.findAll();
        } catch (Exception e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to get feeds");
        }
    }
}
