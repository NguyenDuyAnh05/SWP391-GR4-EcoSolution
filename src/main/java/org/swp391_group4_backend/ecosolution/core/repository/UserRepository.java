package org.swp391_group4_backend.ecosolution.core.repository;

import org.swp391_group4_backend.ecosolution.core.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
}

