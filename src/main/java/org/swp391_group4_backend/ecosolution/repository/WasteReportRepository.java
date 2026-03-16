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
}
