package org.swp391_group4_backend.ecosolution.dto.response;

import lombok.Builder;
import org.swp391_group4_backend.ecosolution.constant.TierType;
import org.swp391_group4_backend.ecosolution.constant.SubscriptionStatus;

@Builder
public record ActivationResponse(
        Long userId,
        String fullName,
        String address,
        String wardName,
        TierType tierType,
        SubscriptionStatus subscriptionStatus,
        String message,
        String paymentUrl
) {
}
