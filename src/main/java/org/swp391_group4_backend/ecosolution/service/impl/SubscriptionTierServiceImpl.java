package org.swp391_group4_backend.ecosolution.service.impl;

import org.springframework.stereotype.Service;
import org.swp391_group4_backend.ecosolution.entity.SubscriptionTier;
import org.swp391_group4_backend.ecosolution.repository.SubscriptionTierRepository;
import org.swp391_group4_backend.ecosolution.service.SubscriptionTierService;

import java.util.List;

@Service
public class SubscriptionTierServiceImpl implements SubscriptionTierService {
    private final SubscriptionTierRepository subscriptionTierRepository;

    public SubscriptionTierServiceImpl(SubscriptionTierRepository subscriptionTierRepository) {
        this.subscriptionTierRepository = subscriptionTierRepository;
    }

    @Override
    public List<SubscriptionTier> getAllTiers() {
        return subscriptionTierRepository.findAll();
    }

    @Override
    public SubscriptionTier getTierById(Long id) {
        return subscriptionTierRepository.findById(id).orElseThrow(() -> new RuntimeException("Tier not found"));
    }
}
