package org.swp391_group4_backend.ecosolution.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.swp391_group4_backend.ecosolution.constant.WasteType;
import org.swp391_group4_backend.ecosolution.entity.WasteTypeRate;

import java.util.Optional;

@Repository
public interface WasteTypeRateRepository extends JpaRepository<WasteTypeRate, Long> {
    Optional<WasteTypeRate> findByWasteType(WasteType wasteType);
}
