package org.swp391_group4_backend.ecosolution.dto.response;

import lombok.Builder;
import lombok.Data;
import org.swp391_group4_backend.ecosolution.constant.SubscriptionStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class SubscriptionResponse {
    private Long id;
    private Long userId;
    private String tierType;
    private BigDecimal monthlyFee;
    private Integer frequencyDays;
    private SubscriptionStatus status;
    private LocalDate startDate;
    private LocalDate endDate;
}
