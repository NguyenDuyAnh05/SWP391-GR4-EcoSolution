package org.swp391_group4_backend.ecosolution.auth.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.swp391_group4_backend.ecosolution.auth.domain.UserCreationRequest;
import org.swp391_group4_backend.ecosolution.auth.domain.dto.request.AssignRoleRequestDto;
import org.swp391_group4_backend.ecosolution.auth.domain.dto.request.LoginRequestDto;
import org.swp391_group4_backend.ecosolution.auth.domain.dto.request.UserCreationRequestDto;
import org.swp391_group4_backend.ecosolution.auth.domain.dto.response.LoginResponseDto;
import org.swp391_group4_backend.ecosolution.auth.domain.dto.response.UserCreationResponseDto;
import org.swp391_group4_backend.ecosolution.auth.domain.entity.User;
import org.swp391_group4_backend.ecosolution.auth.mapper.UserMapper;
import org.swp391_group4_backend.ecosolution.auth.service.AuthService;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

  private final AuthService authService;
  private final UserMapper userMapper;

  public AuthController(AuthService authService, UserMapper userMapper) {
    this.authService = authService;
    this.userMapper = userMapper;
  }

  @PostMapping("/register")
  public ResponseEntity<UserCreationResponseDto> register(
      @RequestBody @Valid UserCreationRequestDto userCreationRequestDto) {

    UserCreationRequest userCreationRequest = userMapper.fromDto(userCreationRequestDto);
    User savedUser = authService.createUser(userCreationRequest);
    return new ResponseEntity<>(userMapper.toDto(savedUser), HttpStatus.CREATED);
  }

  @PostMapping("/login")
  public ResponseEntity<LoginResponseDto> login(@RequestBody @Valid LoginRequestDto loginRequestDto) {
    return ResponseEntity.ok(authService.login(loginRequestDto));
  }

  @PostMapping("/collectors")
  @PreAuthorize("hasAnyRole('ENTERPRISE_ADMIN','SYSTEM_ADMIN')")
  public ResponseEntity<UserCreationResponseDto> createCollector(
      @RequestBody @Valid UserCreationRequestDto userCreationRequestDto) {

    UserCreationRequest userCreationRequest = userMapper.fromDto(userCreationRequestDto);
    User savedUser = authService.createCollector(userCreationRequest);
    return new ResponseEntity<>(userMapper.toDto(savedUser), HttpStatus.CREATED);
  }

  @PatchMapping("/users/{userId}/role")
  @PreAuthorize("hasAnyRole('ENTERPRISE_ADMIN','SYSTEM_ADMIN')")
  public ResponseEntity<UserCreationResponseDto> assignRole(
      @PathVariable UUID userId,
      @RequestBody @Valid AssignRoleRequestDto assignRoleRequestDto) {

    User updatedUser = authService.assignEnterpriseRole(userId, assignRoleRequestDto.role());
    return ResponseEntity.ok(userMapper.toDto(updatedUser));
  }
}
