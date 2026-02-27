package org.swp391_group4_backend.ecosolution.auth.service.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.swp391_group4_backend.ecosolution.auth.domain.UserCreationRequest;
import org.swp391_group4_backend.ecosolution.auth.domain.dto.response.AuthTokenResponseDto;
import org.swp391_group4_backend.ecosolution.auth.domain.entity.User;
import org.swp391_group4_backend.ecosolution.auth.domain.entity.UserAuth;
import org.swp391_group4_backend.ecosolution.auth.domain.entity.UserRole;
import org.swp391_group4_backend.ecosolution.auth.domain.entity.UserStatus;
import org.swp391_group4_backend.ecosolution.auth.exception.EmailAlreadyExistsException;
import org.swp391_group4_backend.ecosolution.auth.exception.InvalidCredentialsException;
import org.swp391_group4_backend.ecosolution.auth.repository.AuthRepository;
import org.swp391_group4_backend.ecosolution.auth.repository.UserAuthRepository;
import org.swp391_group4_backend.ecosolution.auth.repository.UserRepository;
import org.swp391_group4_backend.ecosolution.auth.security.JwtService;
import org.swp391_group4_backend.ecosolution.auth.service.AuthService;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {

  private final AuthRepository userRepository;
  private final UserRepository userReadRepository;
  private final UserAuthRepository userAuthRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;

  public AuthServiceImpl(AuthRepository userRepository,
                         UserRepository userReadRepository,
                         UserAuthRepository userAuthRepository,
                         PasswordEncoder passwordEncoder,
                         JwtService jwtService) {
    this.userRepository = userRepository;
    this.userReadRepository = userReadRepository;
    this.userAuthRepository = userAuthRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtService = jwtService;
  }

  @Override
  public User createUser(UserCreationRequest request) {
    if (userReadRepository.existsByEmail(request.email())) {
      throw new EmailAlreadyExistsException(request.email());
    }

    User user = new User();
    LocalDateTime now = LocalDateTime.now();

    user.setEmail(request.email());
    user.setName(request.name());
    user.setRole(UserRole.CITIZEN);
    user.setStatus(UserStatus.ACTIVE);
    user.setCreatedAt(now);

    User savedUser = userRepository.save(user);

    UserAuth userAuth = new UserAuth();
    userAuth.setUser(savedUser);
    userAuth.setUserId(savedUser.getId());
    userAuth.setUsername(request.email());
    userAuth.setPasswordHash(passwordEncoder.encode(request.password()));
    userAuth.setCreatedAt(now);

    userAuthRepository.save(userAuth);

    return savedUser;
  }

  @Override
  public AuthTokenResponseDto login(String username, String password) {
    Optional<UserAuth> userAuthCandidate = userAuthRepository.findByUsername(username);
    if (userAuthCandidate.isEmpty()) {
      userAuthCandidate = userReadRepository.findByEmail(username)
          .flatMap(user -> userAuthRepository.findByUserId(user.getId()));
    }

    UserAuth userAuth = userAuthCandidate.orElseThrow(InvalidCredentialsException::new);
    if (!passwordEncoder.matches(password, userAuth.getPasswordHash())) {
      throw new InvalidCredentialsException();
    }

    User user = userAuth.getUser();
    String token = jwtService.generateAccessToken(user, userAuth);

    return new AuthTokenResponseDto(
        token,
        "Bearer",
        jwtService.getExpirationSeconds(),
        user.getId(),
        user.getRole(),
        user.getStatus(),
        user.getName(),
        user.getEmail()
    );
  }
}
