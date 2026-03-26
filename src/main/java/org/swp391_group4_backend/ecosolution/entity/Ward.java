package org.swp391_group4_backend.ecosolution.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "wards")
@Data
public class Ward {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ward_name", nullable = false, unique = true)
    private String wardName;
}
