package org.swp391_group4_backend.ecosolution.reporting.repository;

import org.swp391_group4_backend.ecosolution.reporting.domain.entity.WasteReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ReportRepository extends JpaRepository<WasteReport, UUID> {
    @Query("select r from WasteReport r " +
            "where r.createdBy.id = :createdById and lower(r.address) = :address and r.ward.id = :wardId")
    List<WasteReport> findByCreatedByIdAndAddressAndWardId(@Param("createdById") UUID createdById,
                                                          @Param("address") String address,
                                                          @Param("wardId") Long wardId);

    @Query("select r from WasteReport r " +
            "where r.createdBy.id = :createdById and lower(r.address) = :address and r.ward.id = :wardId and r.createdAt > :since")
    List<WasteReport> findRecentByCreatedByAndAddressAndWard(@Param("createdById") UUID createdById,
                                                            @Param("address") String address,
                                                            @Param("wardId") Long wardId,
                                                            @Param("since") OffsetDateTime since);

    List<WasteReport> findAllByCreatedByIdOrderByCreatedAtDesc(UUID createdById);

    // find reports by status
    java.util.List<WasteReport> findAllByStatus(org.swp391_group4_backend.ecosolution.reporting.domain.ReportStatus status);

    java.util.List<WasteReport> findAllByStatusOrderByCreatedAtDesc(org.swp391_group4_backend.ecosolution.reporting.domain.ReportStatus status);

    // find reports assigned to a specific collector
    java.util.List<WasteReport> findAllByAssignedToOrderByCreatedAtDesc(UUID assignedTo);
}


