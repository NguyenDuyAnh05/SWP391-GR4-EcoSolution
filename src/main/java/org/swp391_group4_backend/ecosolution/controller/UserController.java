package org.swp391_group4_backend.ecosolution.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.swp391_group4_backend.ecosolution.dto.request.ActivationRequest;
import org.swp391_group4_backend.ecosolution.dto.response.ActivationResponse;
import org.swp391_group4_backend.ecosolution.dto.response.UserResponse;
import org.swp391_group4_backend.ecosolution.service.UserService;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/activate")
    public ResponseEntity<ActivationResponse> activateService(
            @Valid @RequestBody ActivationRequest request, HttpServletRequest servletRequest
    ) {
        return ResponseEntity.ok(userService.activateService(request, servletRequest));
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMyProfile(){
        return null;
    }


}
