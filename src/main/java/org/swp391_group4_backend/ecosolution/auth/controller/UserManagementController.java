package org.swp391_group4_backend.ecosolution.auth.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.swp391_group4_backend.ecosolution.auth.domain.dto.request.AddCollectorRequestDto;
import org.swp391_group4_backend.ecosolution.auth.domain.dto.request.AssignRoleRequestDto;
import org.swp391_group4_backend.ecosolution.auth.domain.dto.response.UserResponseDto;
import org.swp391_group4_backend.ecosolution.auth.domain.entity.UserRole;
import org.swp391_group4_backend.ecosolution.auth.service.UserManagementService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class UserManagementController {

  private final UserManagementService userManagementService;

  public UserManagementController(UserManagementService userManagementService) {
    this.userManagementService = userManagementService;
  }

  /**
   * Add a new collector - Only ENTERPRISE_ADMIN can do this
   */
  @PostMapping("/collectors")
  @PreAuthorize("hasRole('ENTERPRISE_ADMIN')")
  public ResponseEntity<UserResponseDto> addCollector(
      @RequestBody @Valid AddCollectorRequestDto request) {
    UserResponseDto response = userManagementService.addCollector(request);
    return new ResponseEntity<>(response, HttpStatus.CREATED);
  }

  /**
   * Assign role (ASSIGNOR or ENTERPRISE_ADMIN) to a user
   * Only ENTERPRISE_ADMIN can do this
   */
  @PutMapping("/assign-role")
  @PreAuthorize("hasRole('ENTERPRISE_ADMIN')")
  public ResponseEntity<UserResponseDto> assignRole(
      @RequestBody @Valid AssignRoleRequestDto request) {
    UserResponseDto response = userManagementService.assignRole(request);
    return ResponseEntity.ok(response);
  }

  /**
   * Get all collectors - ENTERPRISE_ADMIN and ASSIGNOR can view
   */
  @GetMapping("/collectors")
  @PreAuthorize("hasAnyRole('ENTERPRISE_ADMIN', 'ASSIGNOR')")
  public ResponseEntity<List<UserResponseDto>> getAllCollectors() {
    List<UserResponseDto> collectors = userManagementService.getAllCollectors();
    return ResponseEntity.ok(collectors);
  }

  /**
   * Get all assignors - Only ENTERPRISE_ADMIN can view
   */
  @GetMapping("/assignors")
  @PreAuthorize("hasRole('ENTERPRISE_ADMIN')")
  public ResponseEntity<List<UserResponseDto>> getAllAssignors() {
    List<UserResponseDto> assignors = userManagementService.getAllAssignors();
    return ResponseEntity.ok(assignors);
  }

  /**
   * Get users by role - Only ENTERPRISE_ADMIN and SYSTEM_ADMIN can use this
   */
  @GetMapping("/by-role/{role}")
  @PreAuthorize("hasAnyRole('ENTERPRISE_ADMIN', 'SYSTEM_ADMIN')")
  public ResponseEntity<List<UserResponseDto>> getUsersByRole(@PathVariable UserRole role) {
    List<UserResponseDto> users = userManagementService.getUsersByRole(role);
    return ResponseEntity.ok(users);
  }
}

