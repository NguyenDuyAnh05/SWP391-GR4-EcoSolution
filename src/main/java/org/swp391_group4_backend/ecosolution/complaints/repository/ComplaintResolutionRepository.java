package org.swp391_group4_backend.ecosolution.complaints.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.swp391_group4_backend.ecosolution.complaints.domain.entity.ComplaintResolution;
import org.swp391_group4_backend.ecosolution.complaints.domain.entity.ResolutionResult;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ComplaintResolutionRepository extends JpaRepository<ComplaintResolution, UUID> {

  // Find resolution by complaint ID
  Optional<ComplaintResolution> findByComplaintId(UUID complaintId);

  // Find resolutions by admin
  List<ComplaintResolution> findByAdminId(UUID adminId);

  // Find resolutions by result
  List<ComplaintResolution> findByResult(ResolutionResult result);

  // Find resolutions created within date range
  List<ComplaintResolution> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

  // Count resolutions by admin
  long countByAdminId(UUID adminId);

  // Count resolutions by result
  long countByResult(ResolutionResult result);

  // Check if complaint has resolution
  boolean existsByComplaintId(UUID complaintId);
}



