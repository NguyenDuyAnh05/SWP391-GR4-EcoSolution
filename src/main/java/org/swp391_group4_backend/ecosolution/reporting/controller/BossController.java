package org.swp391_group4_backend.ecosolution.reporting.controller;

import org.swp391_group4_backend.ecosolution.reporting.domain.ReportStatus;
import org.swp391_group4_backend.ecosolution.reporting.domain.entity.WasteReport;
import org.swp391_group4_backend.ecosolution.reporting.service.ReportService;
import org.swp391_group4_backend.ecosolution.reporting.repository.ReportRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/boss")
public class BossController {
    private final ReportService reportService;

    public BossController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/assignments")
    public String assignments(Model model) {
        List<WasteReport> accepted = reportService.findAcceptedReports();
        model.addAttribute("reports", accepted);
        return "reporting/boss/assignments";
    }

    @PostMapping("/assign")
    public String assign(@RequestParam("reportId") UUID reportId, @RequestParam("collectorId") UUID collectorId, org.springframework.web.servlet.mvc.support.RedirectAttributes ra) {
        try {
            reportService.assignCollector(reportId, collectorId);
            ra.addFlashAttribute("message", "Collector assigned successfully");
        } catch (Exception ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/boss/assignments";
    }
}

