package org.swp391_group4_backend.ecosolution.auth.service;

import org.swp391_group4_backend.ecosolution.auth.domain.UserCreationRequest;
import org.swp391_group4_backend.ecosolution.auth.domain.dto.request.LoginRequestDto;
import org.swp391_group4_backend.ecosolution.auth.domain.dto.response.LoginResponseDto;
import org.swp391_group4_backend.ecosolution.auth.domain.entity.User;
import org.swp391_group4_backend.ecosolution.auth.domain.entity.UserRole;

import java.util.UUID;

public interface AuthService {
  User createUser(UserCreationRequest request);

  User createCollector(UserCreationRequest request);

  LoginResponseDto login(LoginRequestDto request);

  User assignEnterpriseRole(UUID userId, UserRole role);
}
