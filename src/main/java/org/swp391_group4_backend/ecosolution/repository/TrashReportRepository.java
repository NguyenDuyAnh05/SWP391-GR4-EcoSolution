package org.swp391_group4_backend.ecosolution.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.swp391_group4_backend.ecosolution.entity.TrashReport;

@Repository
public interface TrashReportRepository extends JpaRepository<TrashReport, Long> {
}
