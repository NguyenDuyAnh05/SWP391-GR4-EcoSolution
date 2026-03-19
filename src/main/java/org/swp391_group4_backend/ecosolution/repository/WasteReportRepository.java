package org.swp391_group4_backend.ecosolution.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.swp391_group4_backend.ecosolution.constant.ReportStatus;
import org.swp391_group4_backend.ecosolution.entity.WasteReport;

import java.util.List;
import java.util.Optional;

@Repository
public interface WasteReportRepository extends JpaRepository<WasteReport, Long> {

    List<WasteReport> findByCitizenId(Long citizenId);

    List<WasteReport> findByCollectorId(Long collectorId);

    List<WasteReport> findByReportStatus(ReportStatus status);

    long countByReportStatus(ReportStatus status);

    @org.springframework.data.jpa.repository.Query("SELECT new org.swp391_group4_backend.ecosolution.dto.response.CollectorStatResponse(" +
           "r.collector.id, r.collector.firstName, r.collector.lastName, COUNT(r)) " +
           "FROM WasteReport r WHERE r.reportStatus = 'COLLECTED' " +
           "GROUP BY r.collector.id, r.collector.firstName, r.collector.lastName " +
           "ORDER BY COUNT(r) DESC")
    List<org.swp391_group4_backend.ecosolution.dto.response.CollectorStatResponse> findTopCollectors();
}
