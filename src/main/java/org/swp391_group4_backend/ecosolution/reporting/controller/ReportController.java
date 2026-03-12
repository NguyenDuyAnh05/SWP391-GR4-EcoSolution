package org.swp391_group4_backend.ecosolution.reporting.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.swp391_group4_backend.ecosolution.core.repository.UserRepository;
import org.swp391_group4_backend.ecosolution.reporting.controller.dto.CreateReportRequest;
import org.swp391_group4_backend.ecosolution.reporting.mapper.ReportMapper;
import com.ecosolution.reporting.service.ReportService;
import com.ecosolution.reporting.domain.request.ReportRequest;
import org.swp391_group4_backend.ecosolution.reporting.repository.WasteReportRepository;

import java.io.IOException;

@Controller
public class ReportController {

    private final ReportService reportService;
    private final WasteReportRepository reportRepo;
    private final UserRepository userRepo;
    private final ReportMapper mapper;

    public ReportController(ReportService reportService, WasteReportRepository reportRepo, UserRepository userRepo, ReportMapper mapper) {
        this.reportService = reportService;
        this.reportRepo = reportRepo;
        this.userRepo = userRepo;
        this.mapper = mapper;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("reports", reportRepo.findAll());
        return "index";
    }

    @GetMapping("/reports/new")
    public String createForm() {
        return "report/create";
    }

    @PostMapping("/reports")
    public String submitCreate(@ModelAttribute CreateReportRequest req,
                               @RequestParam(name = "image", required = false) MultipartFile image,
                               HttpSession session) throws IOException {
        byte[] imageBytes = null;
        if (image != null && !image.isEmpty()) {
            imageBytes = image.getBytes();
        }
        // Build com.ecosolution ReportRequest using the incoming MultipartFile
        com.ecosolution.reporting.domain.request.ReportRequest newReq = new com.ecosolution.reporting.domain.request.ReportRequest(
                req.address(), req.wardId(), req.submittedQuantity() != null ? req.submittedQuantity().doubleValue() : null, image
        );
        // Use session-based user (demo) — default to first user if missing
        Object uid = session.getAttribute("currentUserId");
        java.util.UUID userId = null;
        if (uid != null) userId = java.util.UUID.fromString(uid.toString());
        else {
            var firstUser = userRepo.findAll().stream().findFirst().orElseThrow();
            userId = firstUser.getId();
        }

        reportService.createReport(userId, newReq);

        return "redirect:/";
    }

    @GetMapping("/reports/{id}")
    public String detail(@PathVariable("id") java.util.UUID id, Model model) {
        var r = reportRepo.findById(id).orElseThrow();
        model.addAttribute("report", r);
        return "report/detail";
    }

    @GetMapping("/login")
    public String loginForm(Model model) {
        model.addAttribute("users", userRepo.findAll());
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam("userId") java.util.UUID userId, HttpSession session) {
        session.setAttribute("currentUserId", userId.toString());
        return "redirect:/";
    }
}


