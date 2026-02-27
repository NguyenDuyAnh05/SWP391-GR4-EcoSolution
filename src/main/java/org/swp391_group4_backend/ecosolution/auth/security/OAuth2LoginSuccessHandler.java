package org.swp391_group4_backend.ecosolution.auth.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.swp391_group4_backend.ecosolution.auth.domain.dto.response.AuthTokenResponseDto;
import org.swp391_group4_backend.ecosolution.auth.domain.entity.User;
import org.swp391_group4_backend.ecosolution.auth.domain.entity.UserAuth;
import org.swp391_group4_backend.ecosolution.auth.domain.entity.UserRole;
import org.swp391_group4_backend.ecosolution.auth.domain.entity.UserStatus;
import org.swp391_group4_backend.ecosolution.auth.repository.UserAuthRepository;
import org.swp391_group4_backend.ecosolution.auth.repository.UserRepository;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {
  private final UserRepository userRepository;
  private final UserAuthRepository userAuthRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final ObjectMapper objectMapper;

  public OAuth2LoginSuccessHandler(UserRepository userRepository,
                                   UserAuthRepository userAuthRepository,
                                   PasswordEncoder passwordEncoder,
                                   JwtService jwtService,
                                   ObjectMapper objectMapper) {
    this.userRepository = userRepository;
    this.userAuthRepository = userAuthRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtService = jwtService;
    this.objectMapper = objectMapper;
  }

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request,
                                      HttpServletResponse response,
                                      Authentication authentication) throws IOException, ServletException {
    OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
    String email = oauth2User.getAttribute("email");
    String name = oauth2User.getAttribute("name");
    String googleId = oauth2User.getAttribute("sub");

    if (email == null || email.isBlank()) {
      response.sendError(HttpServletResponse.SC_BAD_REQUEST, "OAuth2 provider did not return an email");
      return;
    }

    User user = userRepository.findByEmail(email).orElseGet(() -> {
      User newUser = new User();
      LocalDateTime now = LocalDateTime.now();
      newUser.setEmail(email);
      newUser.setName(name == null || name.isBlank() ? email : name);
      newUser.setRole(UserRole.CITIZEN);
      newUser.setStatus(UserStatus.ACTIVE);
      newUser.setCreatedAt(now);
      return userRepository.save(newUser);
    });

    Optional<UserAuth> userAuthOptional = userAuthRepository.findByUserId(user.getId());
    UserAuth userAuth = userAuthOptional.orElseGet(() -> {
      UserAuth newAuth = new UserAuth();
      newAuth.setUser(user);
      newAuth.setUserId(user.getId());
      newAuth.setUsername(email);
      newAuth.setPasswordHash(passwordEncoder.encode(UUID.randomUUID().toString()));
      newAuth.setGoogleId(googleId);
      newAuth.setCreatedAt(LocalDateTime.now());
      return userAuthRepository.save(newAuth);
    });

    if (googleId != null && (userAuth.getGoogleId() == null || !googleId.equals(userAuth.getGoogleId()))) {
      userAuth.setGoogleId(googleId);
      userAuthRepository.save(userAuth);
    }

    String token = jwtService.generateAccessToken(user, userAuth);
    AuthTokenResponseDto responseDto = new AuthTokenResponseDto(
        token,
        "Bearer",
        jwtService.getExpirationSeconds(),
        user.getId(),
        user.getRole(),
        user.getStatus(),
        user.getName(),
        user.getEmail()
    );

    response.setStatus(HttpServletResponse.SC_OK);
    response.setContentType("application/json");
    objectMapper.writeValue(response.getOutputStream(), responseDto);
  }
}

