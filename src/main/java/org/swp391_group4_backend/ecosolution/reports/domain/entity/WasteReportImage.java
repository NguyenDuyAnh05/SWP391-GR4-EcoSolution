package org.swp391_group4_backend.ecosolution.reports.domain.entity;

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
@Table(name = "waste_report_image")
public class WasteReportImage {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(nullable = false, updatable = false)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "report_id", nullable = false)
  private WasteReport report;

  @Column(name = "image_url", nullable = false)
  private String imageUrl;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;
}


