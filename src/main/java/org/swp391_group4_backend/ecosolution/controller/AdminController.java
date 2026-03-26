package org.swp391_group4_backend.ecosolution.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.swp391_group4_backend.ecosolution.service.impl.WardServiceImpl;
import org.swp391_group4_backend.ecosolution.repository.UserRepository;
import org.swp391_group4_backend.ecosolution.repository.PickupTaskRepository;
import org.swp391_group4_backend.ecosolution.dto.response.AdminStatsResponse;
import org.swp391_group4_backend.ecosolution.dto.response.UserResponse;
import org.swp391_group4_backend.ecosolution.constant.UserRole;
import org.swp391_group4_backend.ecosolution.constant.ReportStatus;
import org.swp391_group4_backend.ecosolution.entity.User;
import org.swp391_group4_backend.ecosolution.entity.PickupTask;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AdminController {
    private final WardServiceImpl wardService;
    private final UserRepository userRepository;
    private final PickupTaskRepository pickupTaskRepository;

    @PutMapping("/wards/{wardId}/assign-collector/{collectorId}")
    public ResponseEntity<String> assignCollector(
            @PathVariable Long wardId,
            @PathVariable Long collectorId) {

        wardService.assignCollectorToWard(wardId, collectorId);
        return ResponseEntity.ok("Đã phân công nhân viên phụ trách phường thành công!");
    }

    @GetMapping("/stats")
    public ResponseEntity<AdminStatsResponse> getStats() {
        long citizens = userRepository.findByRole(UserRole.CITIZEN).orElse(List.of()).size();
        long collectors = userRepository.findByRole(UserRole.COLLECTOR).orElse(List.of()).size();
        
        List<PickupTask> allTasks = pickupTaskRepository.findAll();
        long pending = allTasks.stream().filter(t -> t.getStatus() == ReportStatus.PENDING).count();
        long completed = allTasks.stream().filter(t -> t.getStatus() == ReportStatus.COMPLETED).count();

        return ResponseEntity.ok(new AdminStatsResponse(citizens, collectors, pending, completed));
    }

    @GetMapping("/collectors")
    public ResponseEntity<List<UserResponse>> getCollectors() {
        List<User> list = userRepository.findByRole(UserRole.COLLECTOR).orElse(List.of());
        List<UserResponse> res = list.stream()
            .map(u -> new UserResponse(u.getId(), u.getUsername(), u.getLastName() + " " + u.getFirstName(), u.getRole()))
            .collect(Collectors.toList());
        return ResponseEntity.ok(res);
    }
}
