package org.swp391_group4_backend.ecosolution.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.swp391_group4_backend.ecosolution.dto.request.CompleteTaskRequest;
import org.swp391_group4_backend.ecosolution.dto.response.PickupTaskResponse;
import org.swp391_group4_backend.ecosolution.service.PickupTaskService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
public class PickupTaskController {
    private final PickupTaskService taskService;

    // API: GET /api/v1/tasks/collector/5?date=2026-03-26
    @GetMapping("/collector/{collectorId}")
    public ResponseEntity<List<PickupTaskResponse>> getDailyTasks(
            @PathVariable Long collectorId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        // Nếu Frontend không gửi ngày lên, mặc định lấy ngày hôm nay
        LocalDate queryDate = (date != null) ? date : LocalDate.now();

        List<PickupTaskResponse> tasks = taskService.getTasksForCollector(collectorId, queryDate);
        return ResponseEntity.ok(tasks);
    }

    @PatchMapping("/{taskId}/complete")
    public ResponseEntity<String> completePickupTask(
            @PathVariable Long taskId,
            @RequestParam Long collectorId,
            @Valid @RequestBody CompleteTaskRequest request) {

        taskService.completeTask(taskId, collectorId, request);

        return ResponseEntity.ok("Complete task successfully and saved proof image!");
    }

    @GetMapping("/citizen/{citizenId}")
    public ResponseEntity<List<PickupTaskResponse>> getCitizenTasks(@PathVariable Long citizenId) {
        List<PickupTaskResponse> tasks = taskService.getTasksForCitizen(citizenId);
        return ResponseEntity.ok(tasks);
    }
}
