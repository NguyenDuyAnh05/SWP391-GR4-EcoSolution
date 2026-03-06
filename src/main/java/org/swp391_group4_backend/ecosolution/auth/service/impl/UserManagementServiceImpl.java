package org.swp391_group4_backend.ecosolution.auth.service.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.swp391_group4_backend.ecosolution.auth.domain.dto.request.AddCollectorRequestDto;
import org.swp391_group4_backend.ecosolution.auth.domain.dto.request.AssignRoleRequestDto;
import org.swp391_group4_backend.ecosolution.auth.domain.dto.response.UserResponseDto;
import org.swp391_group4_backend.ecosolution.auth.domain.entity.User;
import org.swp391_group4_backend.ecosolution.auth.domain.entity.UserAuth;
import org.swp391_group4_backend.ecosolution.auth.domain.entity.UserRole;
import org.swp391_group4_backend.ecosolution.auth.domain.entity.UserStatus;
import org.swp391_group4_backend.ecosolution.auth.exception.EmailAlreadyExistsException;
import org.swp391_group4_backend.ecosolution.auth.exception.InvalidRoleAssignmentException;
import org.swp391_group4_backend.ecosolution.auth.exception.UserNotFoundException;
import org.swp391_group4_backend.ecosolution.auth.repository.AuthRepository;
import org.swp391_group4_backend.ecosolution.auth.repository.UserAuthRepository;
import org.swp391_group4_backend.ecosolution.auth.repository.UserRepository;
import org.swp391_group4_backend.ecosolution.auth.service.UserManagementService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserManagementServiceImpl implements UserManagementService {

  private final AuthRepository authRepository;
  private final UserRepository userRepository;
  private final UserAuthRepository userAuthRepository;
  private final PasswordEncoder passwordEncoder;

  public UserManagementServiceImpl(AuthRepository authRepository,
                                   UserRepository userRepository,
                                   UserAuthRepository userAuthRepository,
                                   PasswordEncoder passwordEncoder) {
    this.authRepository = authRepository;
    this.userRepository = userRepository;
    this.userAuthRepository = userAuthRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  @Transactional
  public UserResponseDto addCollector(AddCollectorRequestDto request) {
    // Check if email already exists
    if (userRepository.existsByEmail(request.email())) {
      throw new EmailAlreadyExistsException(request.email());
    }

    LocalDateTime now = LocalDateTime.now();

    // Create User
    User user = User.builder()
        .name(request.name())
        .email(request.email())
        .role(UserRole.COLLECTOR)
        .status(UserStatus.ACTIVE)
        .createdAt(now)
        .build();

    User savedUser = authRepository.save(user);

    // Create UserAuth
    UserAuth userAuth = new UserAuth();
    userAuth.setUser(savedUser);
    userAuth.setUserId(savedUser.getId());
    userAuth.setUsername(request.email());
    userAuth.setPasswordHash(passwordEncoder.encode(request.password()));
    userAuth.setCreatedAt(now);

    userAuthRepository.save(userAuth);

    return mapToDto(savedUser);
  }

  @Override
  @Transactional
  public UserResponseDto assignRole(AssignRoleRequestDto request) {
    // Only ASSIGNOR and ENTERPRISE_ADMIN roles can be assigned
    if (request.role() != UserRole.ASSIGNOR && request.role() != UserRole.ENTERPRISE_ADMIN) {
      throw new InvalidRoleAssignmentException(
          "Only ASSIGNOR and ENTERPRISE_ADMIN roles can be assigned. Cannot assign: " + request.role()
      );
    }

    User user = userRepository.findById(request.userId())
        .orElseThrow(() -> new UserNotFoundException(request.userId().toString()));

    // Check if user is not already a SYSTEM_ADMIN
    if (user.getRole() == UserRole.SYSTEM_ADMIN) {
      throw new InvalidRoleAssignmentException("Cannot modify SYSTEM_ADMIN role");
    }

    // Check if user is a CITIZEN (should not be)
    if (user.getRole() == UserRole.CITIZEN) {
      throw new InvalidRoleAssignmentException(
          "Cannot assign enterprise roles to CITIZEN. User must be a COLLECTOR first."
      );
    }

    user.setRole(request.role());
    User updatedUser = authRepository.save(user);

    return mapToDto(updatedUser);
  }

  @Override
  public List<UserResponseDto> getUsersByRole(UserRole role) {
    return userRepository.findByRole(role)
        .stream()
        .map(this::mapToDto)
        .collect(Collectors.toList());
  }

  @Override
  public List<UserResponseDto> getAllCollectors() {
    return getUsersByRole(UserRole.COLLECTOR);
  }

  @Override
  public List<UserResponseDto> getAllAssignors() {
    return getUsersByRole(UserRole.ASSIGNOR);
  }

  private UserResponseDto mapToDto(User user) {
    return new UserResponseDto(
        user.getId(),
        user.getName(),
        user.getEmail(),
        user.getRole(),
        user.getStatus(),
        user.getCreatedAt()
    );
  }
}

