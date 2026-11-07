package com.rssserver.repository;

import com.rssserver.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/** Equivalent of internal/database/users.sql.go queries. */
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByApiKey(String apiKey);
    Optional<User> findByEmail(String email);
}
