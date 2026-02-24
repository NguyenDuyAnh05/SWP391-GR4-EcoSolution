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
@Table(name = "user_auth")
public class UserAuth {
  @Id
  @Column(name = "user_id", nullable = false, updatable = false)
  private UUID userId;

  @OneToOne
  @MapsId
  @JoinColumn(name = "user_id")
  private User user;

  @Column(nullable = false)
  private String username;

  @Column(nullable = false)
  private String passwordHash;

  @Column
  private String googleId;

  @Column(nullable = false)
  private LocalDateTime createdAt;
}
