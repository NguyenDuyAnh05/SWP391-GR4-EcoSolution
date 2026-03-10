package org.swp391_group4_backend.ecosolution.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.swp391_group4_backend.ecosolution.core.domain.entity.User;

import java.util.UUID;

/**
 * TODO 07: Create the UserRepository interface.
 *
 * - Spring Data JPA repository for User persistence.
 * - Provides CRUD operations out of the box.
 * - No custom query methods needed yet.
 *
 * Extends: JpaRepository<User, UUID>
 */
// TODO 07: Uncomment and complete:
 public interface UserRepository extends JpaRepository<User, UUID> {
 }


