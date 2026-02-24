package org.swp391_group4_backend.ecosolution.auth.service.impl;

import org.springframework.stereotype.Service;
import org.swp391_group4_backend.ecosolution.auth.domain.entity.User;
import org.swp391_group4_backend.ecosolution.auth.repository.UserRepository;
import org.swp391_group4_backend.ecosolution.auth.service.UserService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
  private final UserRepository userRepository;

  public UserServiceImpl(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public User create(User user) {
    return userRepository.save(user);
  }

  @Override
  public Optional<User> getById(UUID id) {
    return userRepository.findById(id);
  }

  @Override
  public List<User> getAll() {
    return userRepository.findAll();
  }

  @Override
  public User update(UUID id, User user) {
    user.setId(id);
    return userRepository.save(user);
  }

  @Override
  public void delete(UUID id) {
    userRepository.deleteById(id);
  }
}

