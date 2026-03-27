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
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PickupTaskServiceImpl implements PickupTaskService {
    private final PickupTaskRepository taskRepository;
    private final UserRepository userRepository;

    @Override
    public List<PickupTaskResponse> getTasksForCollector(Long collectedId, LocalDate date) {
        User collector = userRepository.findById(collectedId)
                .orElseThrow(() -> new RuntimeException("Collector not found!"));

        List<PickupTask> tasks = taskRepository.findByCollectorAndScheduledDate(collector, date);

        return tasks.stream().map(task -> {
            // Address & coordinates come from the citizen's user profile
            User citizen = task.getSubscription().getUser();
            return PickupTaskResponse.builder()
                    .taskId(task.getId())
                    .id(task.getId()) // Alias for frontend compatibility
                    .citizenName(citizen.getFullName())
                    .phone(citizen.getPhone())
                    .address(citizen.getAddress())
                    // Use citizen's registered coordinates (more reliable than task-level lat/lng)
                    .latitude(citizen.getLatitude())
                    .longitude(citizen.getLongitude())
                    .scheduledDate(task.getScheduledDate())
                    .status(task.getStatus())
                    .tierType(task.getSubscription().getTier().getTierType())
                    .proofImageUrl(task.getProofImageUrl())
                    .build();
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void completeTask(Long taskId, CompleteTaskRequest request) {
        PickupTask task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found!"));

        if (ReportStatus.COMPLETED.equals(task.getStatus())) {
            throw new RuntimeException("This task is already completed!");
        }

        task.setProofImageUrl(request.getProofImageUrl());
        task.setStatus(ReportStatus.COMPLETED);

        taskRepository.save(task);
    }

    @Override
    public List<PickupTaskResponse> getTasksForCitizen(Long citizenId) {
        List<PickupTask> tasks = taskRepository.findBySubscriptionUserId(citizenId);
        return tasks.stream().map(task -> {
            User citizen = task.getSubscription().getUser();
            return PickupTaskResponse.builder()
                    .taskId(task.getId())
                    .id(task.getId())
                    .citizenName(citizen.getFullName())
                    .phone(citizen.getPhone())
                    .address(citizen.getAddress())
                    .latitude(citizen.getLatitude())
                    .longitude(citizen.getLongitude())
                    .scheduledDate(task.getScheduledDate())
                    .status(task.getStatus())
                    .tierType(task.getSubscription().getTier().getTierType())
                    .proofImageUrl(task.getProofImageUrl())
                    .build();
        }).collect(Collectors.toList());
    }
}
