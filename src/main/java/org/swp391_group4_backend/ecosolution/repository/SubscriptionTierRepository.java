package org.swp391_group4_backend.ecosolution.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.swp391_group4_backend.ecosolution.entity.SubscriptionTier;

@Repository
public interface SubscriptionTierRepository extends JpaRepository<SubscriptionTier, Long> {
}
