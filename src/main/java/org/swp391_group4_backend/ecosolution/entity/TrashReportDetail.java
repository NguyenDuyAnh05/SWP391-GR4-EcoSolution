package org.swp391_group4_backend.ecosolution.entity;

import jakarta.persistence.*;
import lombok.*;
import org.swp391_group4_backend.ecosolution.constant.WasteType;

@Entity
@Table(name = "trash_report_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrashReportDetail {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id", nullable = false)
    private TrashReport report;

    @Enumerated(EnumType.STRING)
    @Column(name = "trash_category", nullable = false)
    private WasteType trashCategory;

    @Column(name = "weight_in_kg", nullable = false)
    private Double weightInKg;

    @Column(name = "points_calculated")
    private Integer pointsCalculated;
}
