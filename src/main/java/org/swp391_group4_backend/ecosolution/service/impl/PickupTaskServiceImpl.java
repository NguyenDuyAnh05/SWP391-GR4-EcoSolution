package org.swp391_group4_backend.ecosolution.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.swp391_group4_backend.ecosolution.constant.ReportStatus;
import org.swp391_group4_backend.ecosolution.dto.request.CompleteTaskRequest;
import org.swp391_group4_backend.ecosolution.dto.response.PickupTaskResponse;
import org.swp391_group4_backend.ecosolution.entity.PickupTask;
import org.swp391_group4_backend.ecosolution.entity.User;
import org.swp391_group4_backend.ecosolution.repository.PickupTaskRepository;
import org.swp391_group4_backend.ecosolution.repository.UserRepository;
import org.swp391_group4_backend.ecosolution.service.PickupTaskService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PickupTaskServiceImpl implements PickupTaskService {
    private final PickupTaskRepository taskRepository;
    private final UserRepository userRepository;

    @Override
    public List<PickupTaskResponse> getTasksForCollector(Long collectedId, LocalDate date) {
        // 1. Kiểm tra nhân viên có tồn tại không
        User collector = userRepository.findById(collectedId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên thu gom!"));

        // 2. Lấy danh sách việc từ Database
        List<PickupTask> tasks = taskRepository.findByCollectorAndScheduledDate(collector, date);

        // 3. Chuyển đổi (Map) từ Entity sang DTO để trả về cho Frontend
        return tasks.stream().map(task -> PickupTaskResponse.builder()
                .taskId(task.getId())
                .citizenName(task.getSubscription().getUser().getFullName()) // Giả sử User có getFullName()
                .phone(task.getSubscription().getUser().getPhone()) // Giả sử User có getPhoneNumber()
                // Chú ý: Cần lấy address từ đâu đó, giả sử User có thuộc tính address
                .address(task.getSubscription().getUser().getAddress())
                .latitude(task.getLatitude())
                .longitude(task.getLongitude())
                .scheduledDate(task.getScheduledDate())
                .status(task.getStatus())
                .tierType(task.getSubscription().getTier().getTierType())
                .build()
        ).collect(Collectors.toList());
    }

    @Transactional
    public void completeTask(Long taskId, Long collectorId, CompleteTaskRequest request) {
        // 1. Tìm tác vụ trong DB
        PickupTask task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Cannot find this task!"));

        // 2. Bảo mật: Kiểm tra xem task này có đúng là của ông nhân viên đang đăng nhập không
        if (task.getCollector() == null || !task.getCollector().getId().equals(collectorId)) {
            throw new RuntimeException("You are not allowed to complete this task!");
        }

        // 3. Kiểm tra trạng thái: Nếu đã hoàn thành rồi thì báo lỗi tránh spam
        if (ReportStatus.COMPLETED.equals(task.getStatus())) {
            throw new RuntimeException("This task has already been completed!");
        }

        // 4. Cập nhật thông tin
        task.setProofImageUrl(request.getProofImageUrl());
        task.setStatus(ReportStatus.COMPLETED); // Chuyển trạng thái

        // [GIỮ CHỖ Ở ĐÂY CHO LUỒNG SỐ 2: CỘNG ĐIỂM ECO-POINTS]
        // Ví dụ: userService.addEcoPoints(task.getSubscription().getUser().getId(), 10);

        taskRepository.save(task);
    }

    @Override
    public List<PickupTaskResponse> getTasksForCitizen(Long citizenId) {
        List<PickupTask> tasks = taskRepository.findBySubscriptionUserId(citizenId);
        return tasks.stream().map(task -> PickupTaskResponse.builder()
                .taskId(task.getId())
                .citizenName(task.getSubscription().getUser().getFullName())
                .phone(task.getSubscription().getUser().getPhone())
                .address(task.getSubscription().getUser().getAddress())
                .latitude(task.getLatitude())
                .longitude(task.getLongitude())
                .scheduledDate(task.getScheduledDate())
                .status(task.getStatus())
                .tierType(task.getSubscription().getTier().getTierType())
                .proofImageUrl(task.getProofImageUrl())
                .build()
        ).collect(Collectors.toList());
    }
}
