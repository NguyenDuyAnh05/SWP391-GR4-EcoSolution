package org.swp391_group4_backend.ecosolution.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.swp391_group4_backend.ecosolution.entity.Ward;

@Repository
public interface WardRepository extends JpaRepository<Ward, Long> {
}
