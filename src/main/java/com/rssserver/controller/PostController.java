package com.rssserver.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rssserver.exception.ApiException;
import com.rssserver.model.Post;
import com.rssserver.model.User;
import com.rssserver.repository.PostRepository;
import com.rssserver.security.AuthUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.List;

/** Equivalent of handlerUser.go's handlerGetPostForUser (JWT-protected, Redis-cached). */
@RestController
@RequestMapping("/v1/posts")
public class PostController {

    private static final Logger log = LoggerFactory.getLogger(PostController.class);

    private final PostRepository postRepository;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    public PostController(PostRepository postRepository, StringRedisTemplate redisTemplate) {
        this.postRepository = postRepository;
        this.redisTemplate = redisTemplate;
    }

    @GetMapping
    public List<Post> getPosts(@AuthUser User user,
                                @RequestParam(required = false, defaultValue = "") String search,
                                @RequestParam(required = false, defaultValue = "10") int limit,
                                @RequestParam(required = false, defaultValue = "0") int offset) {

        String cacheKey = "posts:%s:%s:%d:%d".formatted(user.getId(), search, limit, offset);

        try {
            String cached = redisTemplate.opsForValue().get(cacheKey);
            if (cached != null) {
                log.info("Cache Hit key={}", cacheKey);
                return objectMapper.readValue(cached, objectMapper.getTypeFactory()
                        .constructCollectionType(List.class, Post.class));
            } else {
                log.info("Cache Miss key={}", cacheKey);
            }
        } catch (Exception e) {
            log.error("Redis GET failed", e);
        }

        List<Post> posts;
        try {
            posts = postRepository.findForUser(user.getId(), search, PageRequest.of(offset / Math.max(limit, 1), limit));
        } catch (Exception e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to get posts");
        }

        try {
            String json = objectMapper.writeValueAsString(posts);
            redisTemplate.opsForValue().set(cacheKey, json, Duration.ofMinutes(5));
            log.info("Cached response key={}", cacheKey);
        } catch (Exception e) {
            log.error("Redis SET failed", e);
        }

        return posts;
    }
}
