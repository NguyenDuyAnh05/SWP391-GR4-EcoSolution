package org.swp391_group4_backend.ecosolution.core.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/login")
public class LoginController {
    @GetMapping("/citizen")
    public String loginAsCitizen() {
        // The application uses a stubbed UserService that returns a seeded user.
        // This endpoint exists so the UI can navigate to the dashboard as the citizen.
        return "redirect:/citizen/dashboard";
    }
}

