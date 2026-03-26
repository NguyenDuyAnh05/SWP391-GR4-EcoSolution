package org.swp391_group4_backend.ecosolution.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.swp391_group4_backend.ecosolution.constant.SubscriptionStatus;
import org.swp391_group4_backend.ecosolution.entity.CitizenSubscription;

import java.util.List;
import java.util.Optional;

@Repository
public interface CitizenSubscriptionRepository extends JpaRepository<CitizenSubscription, Long> {
    List<CitizenSubscription>  findAllByStatus(SubscriptionStatus status);
    Optional<CitizenSubscription> findByUserId(Long userId);
    boolean existsByUserIdAndStatus(Long userId, SubscriptionStatus status);
}
