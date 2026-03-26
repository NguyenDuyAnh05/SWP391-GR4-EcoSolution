package org.swp391_group4_backend.ecosolution.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.swp391_group4_backend.ecosolution.constant.SubscriptionStatus;

import java.time.LocalDate;
@Entity
@Table(name = "citizen_subscriptions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CitizenSubscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne // Mỗi user tại một thời điểm thường chỉ có 1 subscription active
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "tier_id")
    private SubscriptionTier tier;

    @Enumerated(EnumType.STRING)
    private SubscriptionStatus status;

    private LocalDate startDate = LocalDate.now();
    private LocalDate endDate;
}
