package org.swp391_group4_backend.ecosolution.core.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.swp391_group4_backend.ecosolution.reporting.service.ReportService;
import org.swp391_group4_backend.ecosolution.reporting.domain.entity.WasteReport;
import org.springframework.core.env.Environment;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/internal/debug")
public class DebugController {
    private final ReportService reportService;
    private final Environment env;

    public DebugController(ReportService reportService, Environment env) {
        this.reportService = reportService;
        this.env = env;
    }

    @GetMapping("/reports")
    public List<ReportSummary> reports() {
        List<WasteReport> reports = reportService.getReportsForCurrentUser();
        return reports.stream().map(r -> new ReportSummary(r.getId(), r.getAddress(), r.getStatus().name(), r.getImagePath())).collect(Collectors.toList());
    }

    @GetMapping("/reports/all")
    public List<ReportSummary> reportsAll() {
        List<WasteReport> reports = reportService.findAllReports();
        return reports.stream().map(r -> new ReportSummary(r.getId(), r.getAddress(), r.getStatus() == null ? "" : r.getStatus().name(), r.getImagePath())).collect(Collectors.toList());
    }

    @GetMapping("/env")
    public java.util.Map<String,String> env() {
        java.util.Map<String,String> m = new java.util.HashMap<>();
        m.put("DB_USERNAME_env", System.getenv("DB_USERNAME"));
        m.put("DB_PASSWORD_env_present", System.getenv("DB_PASSWORD") != null ? "yes" : "no");
        m.put("DEV_USER_ID_env", System.getenv("DEV_USER_ID"));
        m.put("spring.datasource.url", env.getProperty("spring.datasource.url"));
        return m;
    }

    public static class ReportSummary {
        public java.util.UUID id;
        public String address;
        public String status;
        public String imagePath;

        public ReportSummary(java.util.UUID id, String address, String status, String imagePath) {
            this.id = id;
            this.address = address;
            this.status = status;
            this.imagePath = imagePath;
        }
    }
}

