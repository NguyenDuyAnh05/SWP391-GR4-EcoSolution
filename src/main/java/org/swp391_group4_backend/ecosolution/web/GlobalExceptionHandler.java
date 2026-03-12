package org.swp391_group4_backend.ecosolution.web;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public String handleAll(Exception ex, Model model, RedirectAttributes redirect) {
        model.addAttribute("errorMessage", ex.getMessage());
        return "error"; // Thymeleaf template error.html
    }
}

