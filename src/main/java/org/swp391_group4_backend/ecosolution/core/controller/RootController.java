package org.swp391_group4_backend.ecosolution.core.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RootController {
    /**
     * Handle requests to the application root and redirect to the simulated login
     * so the developer-mode user (DEV_USER_ID or stub) sees the dashboard.
     */
    @GetMapping({"/", ""})
    public String root() {
        // Render a small landing page with links for developer-mode testing.
        // The landing page includes links to the simulated login which then
        // redirects to the citizen dashboard.
        return "landing";
    }

    // convenience mapping
    @GetMapping("/index")
    public String index() {
        return "redirect:/";
    }
}


