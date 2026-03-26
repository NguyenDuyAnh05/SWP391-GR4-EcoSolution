package org.swp391_group4_backend.ecosolution.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.swp391_group4_backend.ecosolution.constant.ReportStatus;
import org.swp391_group4_backend.ecosolution.constant.SubscriptionStatus;
import org.swp391_group4_backend.ecosolution.entity.CitizenSubscription;
import org.swp391_group4_backend.ecosolution.entity.PickupTask;
import org.swp391_group4_backend.ecosolution.entity.User;
import org.swp391_group4_backend.ecosolution.entity.Ward;
import org.swp391_group4_backend.ecosolution.repository.CitizenSubscriptionRepository;
import org.swp391_group4_backend.ecosolution.repository.PickupTaskRepository;
import org.swp391_group4_backend.ecosolution.repository.WardRepository;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DailyTaskScheduler {

    private final CitizenSubscriptionRepository subscriptionRepository;
    private final PickupTaskRepository taskRepository;


    private final WardRepository wardRepository;

    // Chạy vào 00:01 sáng mỗi ngày
    @Scheduled(cron = "0 1 0 * * *")
    @Transactional
    public void generateDailyPickupTasks() {
        log.info("--- Create Daily Pickup Task for: {} ---", LocalDate.now());
        LocalDate today = LocalDate.now();

        // 1. Tìm tất cả các gói cước đang ACTIVE
        List<CitizenSubscription> activeSubscriptions = subscriptionRepository.findAllByStatus(SubscriptionStatus.ACTIVE);

        int taskCount = 0;

        for (CitizenSubscription sub : activeSubscriptions) {

            // --- BƯỚC BẢO VỆ (Safety Check) ---
            if (sub.getTier() == null || sub.getTier().getFrequencyDays() == null || sub.getTier().getFrequencyDays() <= 0) {
                log.warn("CẢNH BÁO: Gói cước ID {} không có thông tin tần suất thu gom hợp lệ! Bỏ qua.", sub.getId());
                continue;
            }

            int frequency = sub.getTier().getFrequencyDays();
            long daysBetween = ChronoUnit.DAYS.between(sub.getStartDate(), today);

            // Nếu đúng lịch cần thu gom!
            if (daysBetween % frequency == 0) {

                // TỐI ƯU HÓA: Check xem task đã tạo chưa trước khi tốn công đi tìm nhân viên
                if (!taskRepository.existsBySubscriptionIdAndScheduledDate(sub.getId(), today)) {

                    User assignedCollector = null;

                    // Đã fix lỗi 3: Check Null cẩn thận để chống sập Server
                    if (sub.getUser() != null && sub.getUser().getWard() != null) {
                        String citizenWardName = sub.getUser().getWard().getWardName();

                        // Tra cứu Phường trong DB -> Rút ông Collector ra
                        assignedCollector = wardRepository.findByWardName(citizenWardName)
                                .map(Ward::getCollector)
                                .orElse(null);

                        if (assignedCollector == null) {
                            log.warn("Lưu ý: Phường '{}' chưa có nhân viên phụ trách. Task sẽ để trống nhân viên!", citizenWardName);
                        }
                    } else {
                        log.warn("Lưu ý: Citizen ID {} không có thông tin Phường hợp lệ. Không thể gán Collector tự động!", sub.getUser().getId());
                    }

                    PickupTask newTask = PickupTask.builder()
                            .subscription(sub)
                            .scheduledDate(today)
                            .status(ReportStatus.PENDING) // Trạng thái chờ thu gom
                            .collector(assignedCollector) // Đã fix lỗi 2: Bỏ comment và gán đúng nhân viên!
                            .build();

                    taskRepository.save(newTask);
                    taskCount++;
                }
            }
        }

        log.info("Today Task {} created successfully", taskCount);
    }
}