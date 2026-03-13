package org.swp391_group4_backend.ecosolution.reporting.controller;

import org.swp391_group4_backend.ecosolution.reporting.domain.ReportStatus;
import org.swp391_group4_backend.ecosolution.reporting.domain.entity.WasteReport;
import org.swp391_group4_backend.ecosolution.reporting.service.ReportService;
import org.swp391_group4_backend.ecosolution.reporting.repository.ReportRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/enterprise")
public class EnterpriseController {
    private final ReportService reportService;

    public EnterpriseController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/board")
    public String board(Model model) {
        List<WasteReport> pending = reportService.findPendingReports();
        model.addAttribute("reports", pending);
        return "reporting/enterprise/board";
    }

    @PostMapping("/claim/{id}")
    public String claim(@PathVariable("id") UUID id, org.springframework.web.servlet.mvc.support.RedirectAttributes ra) {
        try {
            reportService.claimReport(id);
            ra.addFlashAttribute("message", "Report claimed successfully");
        } catch (Exception ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/enterprise/board";
    }
}

