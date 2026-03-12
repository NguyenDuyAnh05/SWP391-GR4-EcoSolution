package com.ecosolution.reporting.controller;

import com.ecosolution.core.service.UserService;
import com.ecosolution.core.repository.WardRepository;
import com.ecosolution.reporting.domain.request.ReportRequest;
import com.ecosolution.reporting.service.ReportService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
@RequestMapping("/citizen")
public class CitizenReportController {

    private final WardRepository wardRepository;
    private final ReportService reportService;
    private final UserService userService;

    public CitizenReportController(WardRepository wardRepository, ReportService reportService, UserService userService) {
        this.wardRepository = wardRepository;
        this.reportService = reportService;
        this.userService = userService;
    }

    @GetMapping("/report/new")
    public String showForm(Model model) {
        model.addAttribute("wards", wardRepository.findAll());
        return "reporting/citizen/report-form";
    }

    @PostMapping("/report")
    public String submit(@RequestParam("address") String address,
                         @RequestParam("wardId") Long wardId,
                         @RequestParam(value = "quantity", required = false) Double quantity,
                         @RequestParam(value = "image", required = false) MultipartFile image,
                         Model model) throws IOException {
        var citizen = userService.getCurrentCitizen().orElseThrow(() -> new IllegalStateException("Seeded citizen missing"));

        var req = new ReportRequest(address, wardId, quantity, image);
        try {
            var resp = reportService.createReport(citizen.getId(), req);
            model.addAttribute("report", resp);
            return "reporting/citizen/success";
        } catch (Exception ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return "error";
        }
    }
}

