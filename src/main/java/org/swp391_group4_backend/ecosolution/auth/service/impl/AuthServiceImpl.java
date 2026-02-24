package org.swp391_group4_backend.ecosolution.auth.service.impl;

import org.springframework.stereotype.Service;
import org.swp391_group4_backend.ecosolution.auth.domain.UserCreationRequest;
import org.swp391_group4_backend.ecosolution.auth.domain.entity.User;
import org.swp391_group4_backend.ecosolution.auth.domain.entity.UserRole;
import org.swp391_group4_backend.ecosolution.auth.domain.entity.UserStatus;
import org.swp391_group4_backend.ecosolution.auth.exception.EmailAlreadyExistsException;
import org.swp391_group4_backend.ecosolution.auth.repository.AuthRepository;
import org.swp391_group4_backend.ecosolution.auth.service.AuthService;

import java.time.LocalDateTime;

@Service
public class AuthServiceImpl implements AuthService {

  private final AuthRepository userRepository;

  public AuthServiceImpl(AuthRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public User createUser(UserCreationRequest request) {
    if(userRepository.existsByEmail(request.email())){
      throw new EmailAlreadyExistsException(request.email());
    }

    User user = new User();
    LocalDateTime now = LocalDateTime.now();

    user.setEmail(request.email());
    user.setName(request.name());
    user.setRole(UserRole.CITIZEN);
    user.setStatus(UserStatus.ACTIVE);
    user.setCreatedAt(now);

    return userRepository.save(user);
  }
}
