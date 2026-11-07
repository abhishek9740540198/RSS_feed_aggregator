package com.rssserver.controller;

import com.rssserver.dto.CreateFeedFollowRequest;
import com.rssserver.exception.ApiException;
import com.rssserver.model.FeedFollow;
import com.rssserver.model.User;
import com.rssserver.repository.FeedFollowRepository;
import com.rssserver.security.AuthUser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/** Equivalent of handler_feed_follows.go. */
@RestController
@RequestMapping("/v1/feedfollows")
public class FeedFollowController {

    private final FeedFollowRepository feedFollowRepository;

    public FeedFollowController(FeedFollowRepository feedFollowRepository) {
        this.feedFollowRepository = feedFollowRepository;
    }

    @PostMapping
    public ResponseEntity<FeedFollow> createFeedFollow(@RequestBody CreateFeedFollowRequest req, @AuthUser User user) {
        FeedFollow feedFollow = new FeedFollow();
        feedFollow.setId(UUID.randomUUID());
        feedFollow.setCreatedAt(Instant.now());
        feedFollow.setUpdatedAt(Instant.now());
        feedFollow.setUserId(user.getId());
        feedFollow.setFeedId(req.getFeedId());

        try {
            feedFollow = feedFollowRepository.save(feedFollow);
        } catch (Exception e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create feed follow");
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(feedFollow);
    }

    @GetMapping
    public List<FeedFollow> getFeedFollows(@AuthUser User user) {
        try {
            return feedFollowRepository.findByUserId(user.getId());
        } catch (Exception e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to get feed follows");
        }
    }

    @DeleteMapping("/{feedFollowID}")
    public Map<String, String> deleteFeedFollow(@PathVariable String feedFollowID, @AuthUser User user) {
        UUID id;
        try {
            id = UUID.fromString(feedFollowID);
        } catch (IllegalArgumentException e) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid feed follow ID");
        }

        try {
            feedFollowRepository.deleteByIdAndUserId(id, user.getId());
        } catch (Exception e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to delete feed follow");
        }

        return Map.of("message", "Feed follow deleted successfully");
    }
}
