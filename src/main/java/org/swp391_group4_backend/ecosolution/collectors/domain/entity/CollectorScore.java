package org.swp391_group4_backend.ecosolution.collectors.domain.entity;
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
@Table(name = "collector_score")
public class CollectorScore {
  @Id
  @Column(name = "collector_id", nullable = false, updatable = false)
  private UUID collectorId;

  @OneToOne(fetch = FetchType.LAZY)
  @MapsId
  @JoinColumn(name = "collector_id")
  private User collector;

  @Column(name = "response_rate", nullable = false, precision = 5, scale = 2)
  private BigDecimal responseRate;

  @Column(name = "completion_rate", nullable = false, precision = 5, scale = 2)
  private BigDecimal completionRate;

  @Column(name = "complaint_rate", nullable = false, precision = 5, scale = 2)
  private BigDecimal complaintRate;

  @Column(name = "reliability_score", nullable = false, precision = 5, scale = 2)
  private BigDecimal reliabilityScore;

  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;
}



