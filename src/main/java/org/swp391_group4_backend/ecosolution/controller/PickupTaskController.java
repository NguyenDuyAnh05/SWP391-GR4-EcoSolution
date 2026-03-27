package org.swp391_group4_backend.ecosolution.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.swp391_group4_backend.ecosolution.dto.request.CompleteTaskRequest;
import org.swp391_group4_backend.ecosolution.dto.response.PickupTaskResponse;
import org.swp391_group4_backend.ecosolution.scheduler.DailyTaskScheduler;
import org.swp391_group4_backend.ecosolution.service.PickupTaskService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
public class PickupTaskController {
    private final PickupTaskService taskService;
    private final DailyTaskScheduler dailyTaskScheduler;

    // API: GET /api/v1/tasks/collector/5?date=2026-03-26
    @GetMapping("/collector/{collectorId}")
    public ResponseEntity<List<PickupTaskResponse>> getDailyTasks(
            @PathVariable Long collectorId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        LocalDate queryDate = (date != null) ? date : LocalDate.now();
        List<PickupTaskResponse> tasks = taskService.getTasksForCollector(collectorId, queryDate);
        return ResponseEntity.ok(tasks);
    }

    @PutMapping("/{taskId}/complete")
    public ResponseEntity<String> completePickupTask(
            @PathVariable Long taskId,
            @Valid @RequestBody CompleteTaskRequest request) {

        taskService.completeTask(taskId, request);
        return ResponseEntity.ok("Task completed successfully with proof saved!");
    }

    @GetMapping("/citizen/{citizenId}")
    public ResponseEntity<List<PickupTaskResponse>> getCitizenTasks(@PathVariable Long citizenId) {
        List<PickupTaskResponse> tasks = taskService.getTasksForCitizen(citizenId);
        return ResponseEntity.ok(tasks);
    }

    /**
     * Manual trigger for testing - calls the same logic as the midnight scheduler.
     * Call: POST /api/v1/tasks/trigger-daily
     */
    @PostMapping("/trigger-daily")
    public ResponseEntity<String> triggerDailyTaskGeneration() {
        dailyTaskScheduler.generateDailyPickupTasks();
        return ResponseEntity.ok("Daily task generation triggered manually for: " + LocalDate.now());
    }
}

