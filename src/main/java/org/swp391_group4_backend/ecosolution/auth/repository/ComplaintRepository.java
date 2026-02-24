package org.swp391_group4_backend.ecosolution.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.swp391_group4_backend.ecosolution.auth.domain.entity.Complaint;
import org.swp391_group4_backend.ecosolution.auth.domain.entity.ComplaintStatus;
import org.swp391_group4_backend.ecosolution.auth.domain.entity.ComplaintType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ComplaintRepository extends JpaRepository<Complaint, UUID> {

  // Find complaints by report
  List<Complaint> findByReportId(UUID reportId);

  // Find complaints by citizen
  List<Complaint> findByCitizenId(UUID citizenId);

  // Find complaints by status
  List<Complaint> findByStatus(ComplaintStatus status);

  // Find complaints by type
  List<Complaint> findByType(ComplaintType type);

  // Find complaints by citizen and status
  List<Complaint> findByCitizenIdAndStatus(UUID citizenId, ComplaintStatus status);

  // Find unresolved complaints
  @Query("SELECT c FROM Complaint c WHERE c.status IN ('OPEN', 'IN_REVIEW')")
  List<Complaint> findUnresolvedComplaints();

  // Find complaints created within date range
  List<Complaint> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

  // Count complaints by report
  long countByReportId(UUID reportId);

  // Count complaints by status
  long countByStatus(ComplaintStatus status);

  // Find overdue complaints (open for more than X days)
  @Query("SELECT c FROM Complaint c WHERE c.status = 'OPEN' AND c.createdAt < :deadline")
  List<Complaint> findOverdueComplaints(LocalDateTime deadline);
}

