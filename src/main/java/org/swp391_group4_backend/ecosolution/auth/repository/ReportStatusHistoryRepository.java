package org.swp391_group4_backend.ecosolution.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.swp391_group4_backend.ecosolution.auth.domain.entity.ReportStatusHistory;
import org.swp391_group4_backend.ecosolution.auth.domain.entity.ReportStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ReportStatusHistoryRepository extends JpaRepository<ReportStatusHistory, UUID> {

  // Find history for a specific report, ordered by change date
  List<ReportStatusHistory> findByReportIdOrderByChangedAtDesc(UUID reportId);

  // Find history by status transition
  List<ReportStatusHistory> findByStatusFromAndStatusTo(ReportStatus from, ReportStatus to);

  // Find history by user who made the change
  List<ReportStatusHistory> findByChangedById(UUID userId);

  // Find recent status changes
  List<ReportStatusHistory> findByChangedAtAfterOrderByChangedAtDesc(LocalDateTime date);

  // Get latest status change for a report
  @Query("SELECT h FROM ReportStatusHistory h WHERE h.report.id = :reportId " +
         "ORDER BY h.changedAt DESC LIMIT 1")
  ReportStatusHistory findLatestByReportId(UUID reportId);
}

