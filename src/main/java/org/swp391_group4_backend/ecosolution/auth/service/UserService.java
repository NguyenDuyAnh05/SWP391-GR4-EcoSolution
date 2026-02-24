package org.swp391_group4_backend.ecosolution.auth.service;

import org.swp391_group4_backend.ecosolution.auth.domain.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {
  User create(User user);

  Optional<User> getById(UUID id);

  List<User> getAll();

  User update(UUID id, User user);

  void delete(UUID id);
}

