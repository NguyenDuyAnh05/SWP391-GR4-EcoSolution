package org.swp391_group4_backend.ecosolution.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.swp391_group4_backend.ecosolution.dto.response.CollectorStatResponse;
import org.swp391_group4_backend.ecosolution.dto.response.StatsSummaryResponse;
import org.swp391_group4_backend.ecosolution.service.AdminService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/stats/summary")
    public ResponseEntity<StatsSummaryResponse> getSummary() {
        return ResponseEntity.ok(adminService.getSummaryStats());
    }

    @GetMapping("/stats/top-collectors")
    public ResponseEntity<List<CollectorStatResponse>> getTopCollectors() {
        return ResponseEntity.ok(adminService.getTopCollectors());
    }
}
