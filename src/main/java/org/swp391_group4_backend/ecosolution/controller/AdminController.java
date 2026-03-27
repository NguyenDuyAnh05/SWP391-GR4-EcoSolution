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
import org.swp391_group4_backend.ecosolution.dto.request.CreateUserRequest;
import org.swp391_group4_backend.ecosolution.dto.response.AdminStatsResponse;
import org.swp391_group4_backend.ecosolution.dto.response.TransactionResponse;
import org.swp391_group4_backend.ecosolution.dto.response.AdminStatsResponse;
import org.swp391_group4_backend.ecosolution.dto.response.UserResponse;
import org.swp391_group4_backend.ecosolution.constant.UserRole;
import org.swp391_group4_backend.ecosolution.entity.Ward;
import org.swp391_group4_backend.ecosolution.service.AdminService;
import org.swp391_group4_backend.ecosolution.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AdminController {
    private final WardServiceImpl wardService;
    private final UserService userService;
    private final AdminService adminService;

    @PutMapping("/wards/{wardId}/assign-collector/{collectorId}")
    public ResponseEntity<String> assignCollector(
            @PathVariable Long wardId,
            @PathVariable Long collectorId) {

        wardService.assignCollectorToWard(wardId, collectorId);
        return ResponseEntity.ok("Assigned Collector successfully !");
    }

    @PutMapping("/wards/{wardId}/assign-receiver/{receiverId}")
    public ResponseEntity<String> assignReceiver(
            @PathVariable Long wardId,
            @PathVariable Long receiverId) {

        wardService.assignReceiverToWard(wardId, receiverId);
        return ResponseEntity.ok("Assigned Receiver successfully !");
    }

    @GetMapping("/stats")
    public ResponseEntity<AdminStatsResponse> getStats() {
        return ResponseEntity.ok(adminService.getStats());
    }

    @GetMapping("/collectors")
    public ResponseEntity<List<UserResponse>> getCollectors() {
        return ResponseEntity.ok(adminService.getCollectors());
    }

    @GetMapping("/receivers")
    public ResponseEntity<List<UserResponse>> getReceivers() {
        return ResponseEntity.ok(adminService.getReceivers());
    }

    @GetMapping("/transactions")
    public ResponseEntity<List<TransactionResponse>> getAllTransactions() {
        return ResponseEntity.ok(adminService.getAllTransactions());
    }

    @PostMapping("/wards")
    public ResponseEntity<Ward> createWard(@RequestBody Ward ward) {
        return ResponseEntity.ok(wardService.createWard(ward));
    }

    @PutMapping("/wards/{wardId}")
    public ResponseEntity<Ward> updateWard(@PathVariable Long wardId, @RequestBody Ward ward) {
        return ResponseEntity.ok(wardService.updateWard(wardId, ward));
    }

    @PostMapping("/users")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        return ResponseEntity.ok(userService.createUserByAdmin(request));
    }
}
