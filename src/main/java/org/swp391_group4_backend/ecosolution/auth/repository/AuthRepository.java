package org.swp391_group4_backend.ecosolution.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.swp391_group4_backend.ecosolution.auth.domain.entity.User;

import java.util.UUID;

public interface AuthRepository extends JpaRepository<User, UUID> {
  boolean existsByEmail(String email);
}
