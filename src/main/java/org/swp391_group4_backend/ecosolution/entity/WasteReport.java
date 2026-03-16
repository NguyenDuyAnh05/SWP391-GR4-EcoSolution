package org.swp391_group4_backend.ecosolution.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.swp391_group4_backend.ecosolution.constant.ReportStatus;
import org.swp391_group4_backend.ecosolution.constant.WasteType;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Table(name = "waste_reports")
public class WasteReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "description")
    private String description;

    @Column(name = "image_url")
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    private WasteType wasteType;

    @Enumerated(EnumType.STRING)
    private ReportStatus reportStatus;
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.reportStatus == null) this.reportStatus = ReportStatus.PENDING;
    }
    @ManyToOne
    @JoinColumn(name = "citizen_id")
    private User citizen;

    @ManyToOne
    @JoinColumn(name = "collector_id")
    private User collector;
}
