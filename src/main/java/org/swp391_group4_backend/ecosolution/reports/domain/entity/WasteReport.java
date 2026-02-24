package org.swp391_group4_backend.ecosolution.reports.domain.entity;
import org.swp391_group4_backend.ecosolution.auth.domain.entity.User;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "waste_report")
public class WasteReport {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(nullable = false, updatable = false)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "citizen_id", nullable = false)
  private User citizen;

  @Column(name = "declared_weight", nullable = false, precision = 10, scale = 2)
  private BigDecimal declaredWeight;

  @Column(name = "verified_weight", precision = 10, scale = 2)
  private BigDecimal verifiedWeight;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String description;

  @Column(nullable = false, precision = 10, scale = 6)
  private BigDecimal latitude;

  @Column(nullable = false, precision = 10, scale = 6)
  private BigDecimal longitude;

  @Column(name = "current_status", nullable = false)
  @Enumerated(EnumType.STRING)
  private ReportStatus currentStatus;

  @Column(name = "cancel_reason_code")
  @Enumerated(EnumType.STRING)
  private CancelReasonCode cancelReasonCode;

  @Column(name = "sla_deadline_at")
  private LocalDateTime slaDeadlineAt;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;
}



