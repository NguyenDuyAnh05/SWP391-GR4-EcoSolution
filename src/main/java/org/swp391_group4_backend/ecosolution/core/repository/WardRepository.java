package org.swp391_group4_backend.ecosolution.core.repository;

import org.swp391_group4_backend.ecosolution.core.domain.entity.Ward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WardRepository extends JpaRepository<Ward, Long> {
}

