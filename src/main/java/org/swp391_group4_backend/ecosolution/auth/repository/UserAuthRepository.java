package org.swp391_group4_backend.ecosolution.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.swp391_group4_backend.ecosolution.auth.domain.entity.UserAuth;

import java.util.Optional;
import java.util.UUID;

public interface UserAuthRepository extends JpaRepository<UserAuth, UUID> {

  // Find user auth by username
  Optional<UserAuth> findByUsername(String username);

  // Find user auth by Google ID
  Optional<UserAuth> findByGoogleId(String googleId);

  // Find user auth by user ID
  Optional<UserAuth> findByUserId(UUID userId);

  // Check if username exists
  boolean existsByUsername(String username);

  // Check if Google ID exists
  boolean existsByGoogleId(String googleId);
}

