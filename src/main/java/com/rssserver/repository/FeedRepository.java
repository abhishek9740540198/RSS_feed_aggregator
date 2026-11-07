package com.rssserver.repository;

import com.rssserver.model.Feed;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

/** Equivalent of internal/database/feeds.sql.go queries. */
public interface FeedRepository extends JpaRepository<Feed, UUID> {

    // Equivalent of GetNextFeedsToFetch: ORDER BY last_fetched_at ASC NULLS FIRST LIMIT $1
    @Query("SELECT f FROM Feed f ORDER BY f.lastFetchedAt ASC NULLS FIRST")
    List<Feed> findNextFeedsToFetch(Pageable pageable);
}
