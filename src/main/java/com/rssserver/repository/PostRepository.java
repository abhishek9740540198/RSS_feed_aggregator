package com.rssserver.repository;

import com.rssserver.model.Post;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/** Equivalent of internal/database/posts.sql.go's GetPostsForUser/CreatePost queries. */
public interface PostRepository extends JpaRepository<Post, UUID> {

    @Query("""
        SELECT p FROM Post p
        JOIN FeedFollow ff ON p.feedId = ff.feedId
        WHERE ff.userId = :userId
        AND LOWER(p.title) LIKE CONCAT('%', LOWER(:search), '%')
        ORDER BY p.publishedAt DESC
        """)
    List<Post> findForUser(@Param("userId") UUID userId, @Param("search") String search,
                            org.springframework.data.domain.Pageable pageable);
}
