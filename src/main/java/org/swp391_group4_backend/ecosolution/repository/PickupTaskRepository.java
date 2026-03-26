package org.swp391_group4_backend.ecosolution.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.swp391_group4_backend.ecosolution.entity.PickupTask;
import org.swp391_group4_backend.ecosolution.entity.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface PickupTaskRepository extends JpaRepository<PickupTask, Long> {
    List<PickupTask> findByCollectorAndScheduledDate (User collector, LocalDate date);
    boolean existsBySubscriptionIdAndScheduledDate(Long subscriptionId, LocalDate date);
    List<PickupTask> findBySubscriptionUserId(Long userId);
}
