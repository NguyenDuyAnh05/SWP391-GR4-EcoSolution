package org.swp391_group4_backend.ecosolution.reporting.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.swp391_group4_backend.ecosolution.core.domain.entity.User;
import org.swp391_group4_backend.ecosolution.reporting.domain.ReportStatus;
import org.swp391_group4_backend.ecosolution.reporting.domain.WasteType;

import java.util.UUID;

/**
 * TODO 02: Implement the WasteReport entity.
 *
 * - The central domain object — a citizen's waste pickup request.
 * - Tracks lifecycle via ReportStatus (state machine in the service layer).
 * - Links to three User roles via foreign keys.
 * - Reference: PROJECT_SPECIFICATION §4.4
 *
 * Required annotations:
 *   @Entity, @Table(name = "waste_reports")
 *   @Data, @NoArgsConstructor, @AllArgsConstructor, @Builder  (Lombok)
 *
 * TODO 02a: Add primary key
 *   UUID id
 *   @Id @GeneratedValue(strategy = GenerationType.UUID)
 *
 * TODO 02b: Add content fields
 *   String locationDistrict  — where the waste is (not null)
 *   String imagePath         — citizen's photo of the waste (nullable)
 *   String proofImagePath    — collector's proof of pickup (nullable)
 *
 * TODO 02c: Add status field
 *   ReportStatus status
 *   @Enumerated(EnumType.STRING)
 *   @Column(nullable = false)
 *
 * TODO 02d: Add User relationships (all @ManyToOne)
 *   User createdBy    — @JoinColumn(name = "citizen_id", nullable = false)
 *   User acceptedBy   — @JoinColumn(name = "enterprise_id")
 *   User collectedBy  — @JoinColumn(name = "collector_id")
 */
@Entity
@Table(name = "waste_reports")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WasteReport {
    // TODO 02a: primary key
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    // TODO 02b: content fields
    @Enumerated(EnumType.STRING)
    private WasteType wasteType;
    private String locationDistrict;
    @Column(nullable = false)
    private String address;
    private String description;
    @Column(nullable = false)
    private String imagePath;
    private String proofImagePath;

    // TODO 02c: status field
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportStatus status;
    // TODO 02d: user relationships
    @ManyToOne
    @JoinColumn(name = "citizen_id", nullable = false)
    private User createdBy;

    @ManyToOne
    @JoinColumn(name = "enterprise_id")
    private User acceptedBy;

    @ManyToOne
    @JoinColumn(name = "collector_id")
    private User collectedBy;
}

