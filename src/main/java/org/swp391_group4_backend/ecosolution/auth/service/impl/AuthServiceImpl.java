package org.swp391_group4_backend.ecosolution.auth.service.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.swp391_group4_backend.ecosolution.auth.domain.UserCreationRequest;
import org.swp391_group4_backend.ecosolution.auth.domain.dto.request.LoginRequestDto;
import org.swp391_group4_backend.ecosolution.auth.domain.dto.response.LoginResponseDto;
import org.swp391_group4_backend.ecosolution.auth.domain.entity.User;
import org.swp391_group4_backend.ecosolution.auth.domain.entity.UserAuth;
import org.swp391_group4_backend.ecosolution.auth.domain.entity.UserRole;
import org.swp391_group4_backend.ecosolution.auth.domain.entity.UserStatus;
import org.swp391_group4_backend.ecosolution.auth.exception.EmailAlreadyExistsException;
import org.swp391_group4_backend.ecosolution.auth.exception.InvalidCredentialsException;
import org.swp391_group4_backend.ecosolution.auth.exception.InvalidRoleAssignmentException;
import org.swp391_group4_backend.ecosolution.auth.exception.UserNotFoundException;
import org.swp391_group4_backend.ecosolution.auth.exception.UsernameAlreadyExistsException;
import org.swp391_group4_backend.ecosolution.auth.repository.AuthRepository;
import org.swp391_group4_backend.ecosolution.auth.repository.UserAuthRepository;
import org.swp391_group4_backend.ecosolution.auth.security.JwtService;
import org.swp391_group4_backend.ecosolution.auth.service.AuthService;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService {

  private static final Set<UserRole> ASSIGNABLE_ENTERPRISE_ROLES = Set.of(
      UserRole.ASSIGNOR,
      UserRole.ENTERPRISE_ADMIN
  );

  private final AuthRepository userRepository;
  private final UserAuthRepository userAuthRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;

  public AuthServiceImpl(
      AuthRepository userRepository,
      UserAuthRepository userAuthRepository,
      PasswordEncoder passwordEncoder,
      JwtService jwtService
  ) {
    this.userRepository = userRepository;
    this.userAuthRepository = userAuthRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtService = jwtService;
  }

  @Override
  @Transactional
  public User createUser(UserCreationRequest request) {
    return createUserWithRole(request, UserRole.CITIZEN);
  }

  @Override
  @Transactional
  public User createCollector(UserCreationRequest request) {
    return createUserWithRole(request, UserRole.COLLECTOR);
  }

  @Override
  public LoginResponseDto login(LoginRequestDto request) {
    UserAuth userAuth = userAuthRepository.findByUsername(request.username())
        .orElseThrow(InvalidCredentialsException::new);

    if (!passwordEncoder.matches(request.password(), userAuth.getPasswordHash())) {
      throw new InvalidCredentialsException();
    }

    User user = userAuth.getUser();
    if (user == null || user.getStatus() != UserStatus.ACTIVE) {
      throw new InvalidCredentialsException();
    }

    String token = jwtService.generateAccessToken(user);
    return new LoginResponseDto(
        "Bearer",
        token,
        jwtService.getExpirationSeconds(),
        user.getId(),
        user.getRole()
    );
  }

  @Override
  @Transactional
  public User assignEnterpriseRole(UUID userId, UserRole role) {
    if (!ASSIGNABLE_ENTERPRISE_ROLES.contains(role)) {
      throw new InvalidRoleAssignmentException(role);
    }

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new UserNotFoundException(userId));

    user.setRole(role);
    return userRepository.save(user);
  }

  private User createUserWithRole(UserCreationRequest request, UserRole role) {
    if (userRepository.existsByEmail(request.email())) {
      throw new EmailAlreadyExistsException(request.email());
    }
    if (userAuthRepository.existsByUsername(request.username())) {
      throw new UsernameAlreadyExistsException(request.username());
    }

    LocalDateTime now = LocalDateTime.now();

    User user = new User();
    user.setEmail(request.email());
    user.setName(request.name());
    user.setRole(role);
    user.setStatus(UserStatus.ACTIVE);
    user.setCreatedAt(now);

    User savedUser = userRepository.save(user);

    UserAuth userAuth = new UserAuth();
    userAuth.setUser(savedUser);
    userAuth.setUsername(request.username());
    userAuth.setPasswordHash(passwordEncoder.encode(request.password()));
    userAuth.setCreatedAt(now);
    userAuthRepository.save(userAuth);

    return savedUser;
  }
}
