package org.swp391_group4_backend.ecosolution.auth.service;

import org.swp391_group4_backend.ecosolution.auth.domain.entity.UserAuth;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserAuthService {
  UserAuth create(UserAuth userAuth);

  Optional<UserAuth> getById(UUID id);

  List<UserAuth> getAll();

  UserAuth update(UUID id, UserAuth userAuth);

  void delete(UUID id);
}
