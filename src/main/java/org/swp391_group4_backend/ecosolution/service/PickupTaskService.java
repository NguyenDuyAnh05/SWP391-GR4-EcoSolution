package org.swp391_group4_backend.ecosolution.service;

import org.swp391_group4_backend.ecosolution.dto.request.CompleteTaskRequest;
import org.swp391_group4_backend.ecosolution.dto.response.PickupTaskResponse;

import java.time.LocalDate;
import java.util.List;

public interface PickupTaskService {
    List<PickupTaskResponse> getTasksForCollector(Long collectedId, LocalDate date);
    void completeTask(Long taskId, CompleteTaskRequest request);
    List<PickupTaskResponse> getTasksForCitizen(Long citizenId);
}
