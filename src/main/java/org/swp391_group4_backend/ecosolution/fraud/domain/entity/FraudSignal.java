package org.swp391_group4_backend.ecosolution.fraud.domain.entity;
import org.swp391_group4_backend.ecosolution.auth.domain.entity.User;
import org.swp391_group4_backend.ecosolution.reports.domain.entity.WasteReport;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "fraud_signal")
public class FraudSignal {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(nullable = false, updatable = false)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "citizen_id", nullable = false)
  private User citizen;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "report_id", nullable = false)
  private WasteReport report;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private FraudType type;

  @Column(nullable = false)
  private Integer score;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;
}




