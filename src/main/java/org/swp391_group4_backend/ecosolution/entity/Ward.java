package org.swp391_group4_backend.ecosolution.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "wards")
@Getter
@Setter
public class Ward {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ward_name", nullable = false, unique = true)
    private String wardName;

    @ManyToOne
    @JoinColumn(name = "collector_id")
    private User collector;
}
