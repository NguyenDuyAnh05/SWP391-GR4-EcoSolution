package org.swp391_group4_backend.ecosolution.reports.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.swp391_group4_backend.ecosolution.reports.domain.entity.ReportStatusHistory;
import org.swp391_group4_backend.ecosolution.reports.domain.entity.ReportStatus;

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
  ReportStatusHistory findTopByReportIdOrderByChangedAtDesc(UUID reportId);
}
