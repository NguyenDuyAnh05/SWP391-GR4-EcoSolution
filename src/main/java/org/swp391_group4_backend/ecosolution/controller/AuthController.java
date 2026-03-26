package org.swp391_group4_backend.ecosolution.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.swp391_group4_backend.ecosolution.dto.request.ActivationRequest;
import org.swp391_group4_backend.ecosolution.dto.request.LoginRequest;
import org.swp391_group4_backend.ecosolution.dto.request.RegisterRequest;
import org.swp391_group4_backend.ecosolution.dto.response.ActivationResponse;
import org.swp391_group4_backend.ecosolution.dto.response.UserResponse;
import org.swp391_group4_backend.ecosolution.service.UserService;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = "*") // CỰC KỲ QUAN TRỌNG: Cho phép Frontend React gọi API
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(userService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(userService.login(request));
    }
}
