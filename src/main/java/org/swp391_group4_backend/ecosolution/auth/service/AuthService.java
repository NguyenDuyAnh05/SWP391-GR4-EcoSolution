package org.swp391_group4_backend.ecosolution.auth.service;

import org.swp391_group4_backend.ecosolution.auth.domain.UserCreationRequest;
import org.swp391_group4_backend.ecosolution.auth.domain.entity.User;

public interface AuthService {
  User createUser(UserCreationRequest request);
}
