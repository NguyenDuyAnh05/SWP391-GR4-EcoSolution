package org.swp391_group4_backend.ecosolution.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.swp391_group4_backend.ecosolution.constant.SubscriptionName;

import java.math.BigDecimal;
@Entity
@Table(name = "subscription_tiers")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionTier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private SubscriptionName tierName; // HOUSEHOLD, BUSINESS

    @Column(name = "monthly_fee")
    private BigDecimal monthlyFee;

    @Column(name = "frequency_days")
    private Integer frequencyDays; // Tần suất thu gom (ví dụ: 1 hoặc 2 ngày/lần)

    @Column(columnDefinition = "TEXT")
    private String description;
}
