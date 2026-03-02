package org.swp391_group4_backend.ecosolution.wasterequest.domain.dto.response;

import org.swp391_group4_backend.ecosolution.wasterequest.domain.entity.RequestStatus;
import org.swp391_group4_backend.ecosolution.wasterequest.domain.entity.WasteType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record WasteRequestResponseDto(
    UUID id,
    UUID citizenId,
    String citizenName,
    WasteType wasteType,
    BigDecimal quantity,
    String address,
    Double latitude,
    Double longitude,
    LocalDate preferredDate,
    RequestStatus status,
    UUID assignedCollectorId,
    String assignedCollectorName,
    WasteType actualWasteType,
    BigDecimal actualQuantity,
    boolean hasEvidenceImage,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
