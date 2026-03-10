package org.swp391_group4_backend.ecosolution.reporting.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.swp391_group4_backend.ecosolution.reporting.domain.entity.WasteReport;

import java.util.UUID;

/**
 * TODO 03: Create the WasteReportRepository interface.
 *
 * - Spring Data JPA repository for WasteReport persistence.
 * - Provides CRUD operations out of the box.
 * - No custom query methods needed yet.
 *
 * Extends: JpaRepository<WasteReport, UUID>
 */
// TODO 03: Uncomment and complete:
@Repository
 public interface WasteReportRepository extends JpaRepository<WasteReport, UUID> {
}

