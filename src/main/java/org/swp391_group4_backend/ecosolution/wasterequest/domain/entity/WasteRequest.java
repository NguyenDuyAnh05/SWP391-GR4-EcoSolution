package org.swp391_group4_backend.ecosolution.wasterequest.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.swp391_group4_backend.ecosolution.auth.domain.entity.User;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "waste_requests")
public class WasteRequest {
    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "citizen_id", nullable = false, updatable = false)
    private User citizen;

    // --- Citizen's original report (immutable after creation) ---

    @Column(name = "waste_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private WasteType wasteType;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal quantity;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(name = "preferred_date", nullable = false)
    private LocalDate preferredDate;

    // --- Status & assignment ---

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RequestStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_collector_id")
    private User assignedCollector;

    // --- Collector's adjustment (BR18: set at completion, separate from original) ---

    @Column(name = "actual_waste_type")
    @Enumerated(EnumType.STRING)
    private WasteType actualWasteType;

    @Column(name = "actual_quantity", precision = 10, scale = 2)
    private BigDecimal actualQuantity;

    // --- Evidence (BR19: required at COMPLETED, stored as BLOB for MVP) ---

    @Lob
    @Column(name = "evidence_image")
    private byte[] evidenceImage;

    // --- Timestamps ---

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Version
    private Long version;

    @PrePersist
    protected void onCreate() {
        if (id == null) {
            id = UUID.randomUUID();
        }
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) {
            createdAt = now;
        }
        updatedAt = now;
        if (status == null) {
            status = RequestStatus.PENDING;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
