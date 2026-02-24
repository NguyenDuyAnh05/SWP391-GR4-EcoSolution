package org.swp391_group4_backend.ecosolution.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.swp391_group4_backend.ecosolution.auth.domain.entity.WasteReport;
import org.swp391_group4_backend.ecosolution.auth.domain.entity.ReportStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WasteReportRepository extends JpaRepository<WasteReport, UUID> {

  // Find reports by citizen
  List<WasteReport> findByCitizenId(UUID citizenId);

  // Find reports by status
  List<WasteReport> findByCurrentStatus(ReportStatus status);

  // Find reports by citizen and status
  List<WasteReport> findByCitizenIdAndCurrentStatus(UUID citizenId, ReportStatus status);

  // Find reports within date range
  List<WasteReport> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

  // Find reports with SLA deadline before a certain date
  List<WasteReport> findBySlaDeadlineAtBeforeAndCurrentStatus(LocalDateTime deadline, ReportStatus status);

  // Find reports by location proximity (custom query)
  @Query("SELECT r FROM WasteReport r WHERE " +
         "r.latitude BETWEEN :minLat AND :maxLat AND " +
         "r.longitude BETWEEN :minLon AND :maxLon AND " +
         "r.currentStatus = :status")
  List<WasteReport> findByLocationProximityAndStatus(
      @Param("minLat") Double minLat,
      @Param("maxLat") Double maxLat,
      @Param("minLon") Double minLon,
      @Param("maxLon") Double maxLon,
      @Param("status") ReportStatus status
  );

  // Count reports by citizen
  long countByCitizenId(UUID citizenId);

  // Count reports by status
  long countByCurrentStatus(ReportStatus status);

  // Find unverified reports
  @Query("SELECT r FROM WasteReport r WHERE r.verifiedWeight IS NULL")
  List<WasteReport> findUnverifiedReports();
}

