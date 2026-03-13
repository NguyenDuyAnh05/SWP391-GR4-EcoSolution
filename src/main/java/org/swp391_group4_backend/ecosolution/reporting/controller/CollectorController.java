package org.swp391_group4_backend.ecosolution.reporting.controller;

import org.swp391_group4_backend.ecosolution.reporting.domain.ReportStatus;
import org.swp391_group4_backend.ecosolution.reporting.domain.entity.WasteReport;
import org.swp391_group4_backend.ecosolution.reporting.domain.request.CollectionCompleteRequest;
import org.swp391_group4_backend.ecosolution.reporting.repository.ReportRepository;
import org.swp391_group4_backend.ecosolution.reporting.service.ReportService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/collector")
public class CollectorController {
    private final ReportService reportService;

    public CollectorController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/tasks")
    public String tasks(Model model) {
        List<WasteReport> assigned = reportService.findAssignedReportsForCurrentUser();
        model.addAttribute("reports", assigned);
        return "reporting/collector/tasks";
    }

    @PostMapping("/complete")
    public String complete(@RequestParam("reportId") UUID reportId,
                           @RequestParam("actualQuantity") Double actualQuantity,
                           @RequestParam("proofImage") MultipartFile proofImage,
                           org.springframework.web.servlet.mvc.support.RedirectAttributes ra) {
        try {
            var req = new CollectionCompleteRequest(reportId, actualQuantity, proofImage);
            reportService.completeCollection(req);
            ra.addFlashAttribute("message", "Report marked as collected");
        } catch (Exception ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/collector/tasks";
    }
}

