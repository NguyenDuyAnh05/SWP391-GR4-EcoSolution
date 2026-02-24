package org.swp391_group4_backend.ecosolution.reports.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.swp391_group4_backend.ecosolution.reports.domain.entity.WasteReportImage;

import java.util.List;
import java.util.UUID;

public interface WasteReportImageRepository extends JpaRepository<WasteReportImage, UUID> {

  // Find all images for a specific report
  List<WasteReportImage> findByReportId(UUID reportId);

  // Count images for a report
  long countByReportId(UUID reportId);

  // Delete all images for a report
  void deleteByReportId(UUID reportId);
}



