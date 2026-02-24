package org.swp391_group4_backend.ecosolution.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.swp391_group4_backend.ecosolution.auth.domain.entity.User;
import org.swp391_group4_backend.ecosolution.auth.domain.entity.UserRole;
import org.swp391_group4_backend.ecosolution.auth.domain.entity.UserStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

  // Find user by email
  Optional<User> findByEmail(String email);

  // Check if email exists
  boolean existsByEmail(String email);

  // Find users by role
  List<User> findByRole(UserRole role);

  // Find users by status
  List<User> findByStatus(UserStatus status);

  // Find users by role and status
  List<User> findByRoleAndStatus(UserRole role, UserStatus status);

  // Find all active collectors
  @Query("SELECT u FROM User u WHERE u.role = 'COLLECTOR' AND u.status = 'ACTIVE'")
  List<User> findActiveCollectors();

  // Find all active citizens
  @Query("SELECT u FROM User u WHERE u.role = 'CITIZEN' AND u.status = 'ACTIVE'")
  List<User> findActiveCitizens();

  // Count users by role
  long countByRole(UserRole role);

  // Count users by status
  long countByStatus(UserStatus status);
}

