package org.swp391_group4_backend.ecosolution.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
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
    @JsonIgnoreProperties("ward")
    private User collector;

    @ManyToOne
    @JoinColumn(name = "receiver_id")
    @JsonIgnoreProperties("ward")
    private User receiver;
}
