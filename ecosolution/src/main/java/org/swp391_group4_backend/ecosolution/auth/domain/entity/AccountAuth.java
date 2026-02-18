package org.swp391_group4_backend.ecosolution.auth.domain.entity;

import jakarta.persistence.*;


import java.util.UUID;

@Entity
public class AccountAuth {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(nullable = false)
  private String hashPassword;

  @Column
  private String googleId;

}
