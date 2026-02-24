package org.swp391_group4_backend.ecosolution.auth.domain.entity;

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
@Table(name = "complaint_resolution")
public class ComplaintResolution {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(nullable = false, updatable = false)
  private UUID id;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "complaint_id", nullable = false)
  private Complaint complaint;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "admin_id", nullable = false)
  private User admin;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private ResolutionResult result;

  @Column(columnDefinition = "TEXT")
  private String note;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;
}

