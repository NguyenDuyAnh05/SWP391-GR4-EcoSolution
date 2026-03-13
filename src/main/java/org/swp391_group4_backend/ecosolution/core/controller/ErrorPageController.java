package org.swp391_group4_backend.ecosolution.core.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ErrorPageController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object statusObj = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Integer status = null;
        if (statusObj != null) {
            try {
                status = Integer.valueOf(statusObj.toString());
            } catch (NumberFormatException ignored) {
            }
        }

        String message = (String) request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
        if (message == null || message.isBlank()) {
            Object ex = request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
            if (ex instanceof Exception) {
                message = ((Exception) ex).getMessage();
            }
        }
        model.addAttribute("message", message != null ? message : "Unexpected error");

        if (status != null) {
            if (status == 404) return "404";
            if (status == 500) return "500";
        }
        return "error";
    }
}

