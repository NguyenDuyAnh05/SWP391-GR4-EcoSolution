package org.swp391_group4_backend.ecosolution.service;

import org.swp391_group4_backend.ecosolution.entity.SubscriptionTier;

import java.util.List;

public interface SubscriptionTierService {
    List<SubscriptionTier> getAllTiers();
    SubscriptionTier getTierById(Long id);
}
