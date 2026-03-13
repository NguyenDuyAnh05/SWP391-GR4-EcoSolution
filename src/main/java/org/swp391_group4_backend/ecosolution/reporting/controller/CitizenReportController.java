package org.swp391_group4_backend.ecosolution.reporting.controller;

import org.swp391_group4_backend.ecosolution.core.domain.entity.User;
import org.swp391_group4_backend.ecosolution.core.domain.entity.Ward;
import org.swp391_group4_backend.ecosolution.core.repository.WardRepository;
import org.swp391_group4_backend.ecosolution.core.service.UserService;
import org.swp391_group4_backend.ecosolution.reporting.domain.ReportStatus;
import org.swp391_group4_backend.ecosolution.reporting.domain.entity.WasteReport;
import org.swp391_group4_backend.ecosolution.reporting.domain.request.ReportRequest;
import org.swp391_group4_backend.ecosolution.reporting.service.ReportService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.UUID;

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

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        User user = userService.getCurrentUser();
        List<WasteReport> reports = reportService.getReportsForCurrentUser();
        double points = reports.stream()
                .filter(rep -> rep.getStatus() == ReportStatus.COLLECTED)
                .mapToDouble(rep -> rep.getQuantity() != null ? rep.getQuantity() : 0.0)
                .sum();

        model.addAttribute("points", points);
        return "reporting/citizen/dashboard";
    }

    @GetMapping({"/report/new", "/report"})
    public String form(Model model) {
        List<Ward> wards = wardRepository.findAll();
        model.addAttribute("wards", wards);
        if (!model.containsAttribute("reportRequest")) {
            model.addAttribute("reportRequest", new ReportRequest(null, null, null, null, null, null));
        }
        return "reporting/citizen/report-form";
    }

    @PostMapping(value = "/report", consumes = "multipart/form-data")
    public String submit(@ModelAttribute ReportRequest reportRequest, RedirectAttributes ra) {
        try {
            var r = reportService.createReport(reportRequest);
            ra.addFlashAttribute("message", "Report submitted successfully");
            return "redirect:/citizen/reports";
        } catch (IllegalArgumentException | IllegalStateException e) {
            // validation or duplicate/business rejection
            ra.addFlashAttribute("error", e.getMessage());
        } catch (Exception ex) {
            ra.addFlashAttribute("error", "Failed to submit report: " + ex.getMessage());
        }

        // preserve entered values via flash attributes and redirect back to form
        ra.addFlashAttribute("reportRequest", reportRequest);
        return "redirect:/citizen/report/new";
    }

    @GetMapping("/reports")
    public String history(Model model) {
        List<WasteReport> reports = reportService.getReportsForCurrentUser();
        model.addAttribute("reports", reports);
        return "reporting/citizen/history";
    }

    @PostMapping("/report/{id}/cancel")
    public String cancelReport(@PathVariable("id") UUID id, RedirectAttributes ra) {
        try {
            reportService.cancelReport(id);
            ra.addFlashAttribute("message", "Report cancelled successfully");
        } catch (IllegalStateException ise) {
            ra.addFlashAttribute("error", ise.getMessage());
        } catch (Exception ex) {
            ra.addFlashAttribute("error", "Failed to cancel report: " + ex.getMessage());
        }
        return "redirect:/citizen/reports";
    }
}

