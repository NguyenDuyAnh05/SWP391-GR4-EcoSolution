package org.swp391_group4_backend.ecosolution.wasterequest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.swp391_group4_backend.ecosolution.wasterequest.domain.entity.RequestStatus;
import org.swp391_group4_backend.ecosolution.wasterequest.domain.entity.WasteRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WasteRequestRepository extends JpaRepository<WasteRequest, UUID> {

    // BR07: Duplicate detection — same citizen + address + date + non-terminal status
    boolean existsByCitizenIdAndAddressAndPreferredDateAndStatusNotIn(
            UUID citizenId, String address, LocalDate preferredDate, java.util.Collection<RequestStatus> statuses
    );

    // BR01: Max 1 request per citizen per calendar day
    @Query("SELECT COUNT(w) > 0 FROM WasteRequest w WHERE w.citizen.id = :citizenId " +
            "AND w.createdAt >= :startOfDay AND w.createdAt < :endOfDay " +
            "AND w.status NOT IN :terminalStatuses")
    boolean existsByCitizenIdAndCreatedToday(
            @Param("citizenId") UUID citizenId,
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay,
            @Param("terminalStatuses") java.util.Collection<RequestStatus> terminalStatuses
    );

    // BR34: Find the most recent cancelled request by citizen for cooldown check
    Optional<WasteRequest> findTopByCitizenIdAndStatusOrderByUpdatedAtDesc(UUID citizenId, RequestStatus status);

    // Citizen: view own requests
    List<WasteRequest> findByCitizenIdOrderByCreatedAtDesc(UUID citizenId);

    // Assignor: view all requests
    List<WasteRequest> findAllByOrderByCreatedAtDesc();

    // Collector: view assigned requests
    List<WasteRequest> findByAssignedCollectorIdOrderByCreatedAtDesc(UUID collectorId);
}
