package org.swp391_group4_backend.ecosolution.entity;

import jakarta.persistence.*;
import lombok.*;
import org.swp391_group4_backend.ecosolution.constant.WasteType;

@Entity
@Table(name = "waste_type_rates")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WasteTypeRate {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private WasteType wasteType;

    @Column(name = "points_per_kg", nullable = false)
    private Integer pointsPerKg;
}
