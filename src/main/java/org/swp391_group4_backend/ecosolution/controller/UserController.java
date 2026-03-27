package org.swp391_group4_backend.ecosolution.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.swp391_group4_backend.ecosolution.dto.request.ActivationRequest;
import org.swp391_group4_backend.ecosolution.dto.response.ActivationResponse;
import org.swp391_group4_backend.ecosolution.dto.response.SubscriptionResponse;
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

    @GetMapping("/{userId}/subscription")
    public ResponseEntity<SubscriptionResponse> getSubscription(@PathVariable Long userId) {
        try {
            return ResponseEntity.ok(userService.getSubscription(userId));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{userId}/subscription/pending")
    public ResponseEntity<Void> cancelPendingSubscription(@PathVariable Long userId) {
        try {
            userService.cancelPendingSubscription(userId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{userId}/transactions")
    public ResponseEntity<java.util.List<org.swp391_group4_backend.ecosolution.dto.response.TransactionResponse>> getUserTransactions(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getUserTransactions(userId));
    }


}
