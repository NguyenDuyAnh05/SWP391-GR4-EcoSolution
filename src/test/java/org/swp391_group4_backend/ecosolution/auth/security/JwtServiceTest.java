package org.swp391_group4_backend.ecosolution.auth.security;

import org.junit.jupiter.api.Test;
import org.swp391_group4_backend.ecosolution.auth.domain.entity.User;
import org.swp391_group4_backend.ecosolution.auth.domain.entity.UserAuth;
import org.swp391_group4_backend.ecosolution.auth.domain.entity.UserRole;
import org.swp391_group4_backend.ecosolution.auth.domain.entity.UserStatus;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtServiceTest {
  @Test
  void generatesAndParsesToken() {
    JwtService jwtService = new JwtService("test-secret-test-secret-test-secret-1234", 60);

    User user = new User();
    UUID userId = UUID.randomUUID();
    user.setId(userId);
    user.setEmail("test@example.com");
    user.setName("Test User");
    user.setRole(UserRole.CITIZEN);
    user.setStatus(UserStatus.ACTIVE);

    UserAuth userAuth = new UserAuth();
    userAuth.setUsername("test@example.com");

    String token = jwtService.generateAccessToken(user, userAuth);

    assertTrue(jwtService.isTokenValid(token));
    assertEquals(userId, jwtService.parseUserId(token));
  }
}

