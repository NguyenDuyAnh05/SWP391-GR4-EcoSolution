package org.swp391_group4_backend.ecosolution.core.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.swp391_group4_backend.ecosolution.core.domain.UserRole;

import java.util.UUID;

/**
 * TODO 06: Complete the User entity.
 *
 * - The identity foundation — every actor in the system is a User.
 * - Self-referencing FK: COLLECTOR.employer → ENTERPRISE user.
 * - Reference: PROJECT_SPECIFICATION §4.2
 *
 * TODO 06a: Add primary key
 *   UUID id
 *   @Id @GeneratedValue(strategy = GenerationType.UUID)
 *
 * TODO 06b: Add identity fields
 *   String username  — @Column(unique = true, nullable = false)
 *   String email     — @Column(unique = true, nullable = false)
 *   String password  — @Column(nullable = false)
 *
 * TODO 06c: Add role field
 *   UserRole role
 *   @Enumerated(EnumType.STRING)
 *   @Column(nullable = false)
 *
 * TODO 06d: Add points field
 *   int points — default 0 (use @Builder.Default)
 *
 * TODO 06e: Add employer self-reference
 *   @ManyToOne
 *   @JoinColumn(name = "employer_id")
 *   User employer
 *   → Only COLLECTOR has non-null employer
 *   → employer.role must be ENTERPRISE
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class User {
    // TODO 06a: primary key
    // TODO 06b: identity fields
    // TODO 06c: role field
    // TODO 06d: points field
    // TODO 06e: employer self-reference
}

