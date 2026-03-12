package com.ecosolution.reporting.repository;

import com.ecosolution.reporting.domain.entity.WasteReport;
import com.ecosolution.reporting.domain.ReportStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WasteReportRepository extends JpaRepository<WasteReport, UUID> {
    Optional<WasteReport> findTopByCreatedByIdAndAddressAndWardIdAndStatusIn(UUID citizenId, String address, Long wardId, List<ReportStatus> statuses);
}

