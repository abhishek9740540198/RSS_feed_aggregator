package com.rssserver.repository;

import com.rssserver.model.FeedFollow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/** Equivalent of internal/database/feed_follow.sql.go queries. */
public interface FeedFollowRepository extends JpaRepository<FeedFollow, UUID> {
    List<FeedFollow> findByUserId(UUID userId);

    @Transactional
    void deleteByIdAndUserId(UUID id, UUID userId);
}
