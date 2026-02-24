package org.swp391_group4_backend.ecosolution.auth.service.impl;

import org.springframework.stereotype.Service;
import org.swp391_group4_backend.ecosolution.auth.domain.entity.UserAuth;
import org.swp391_group4_backend.ecosolution.auth.repository.UserAuthRepository;
import org.swp391_group4_backend.ecosolution.auth.service.UserAuthService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserAuthServiceImpl implements UserAuthService {
  private final UserAuthRepository userAuthRepository;

  public UserAuthServiceImpl(UserAuthRepository userAuthRepository) {
    this.userAuthRepository = userAuthRepository;
  }

  @Override
  public UserAuth create(UserAuth userAuth) {
    return userAuthRepository.save(userAuth);
  }

  @Override
  public Optional<UserAuth> getById(UUID id) {
    return userAuthRepository.findById(id);
  }

  @Override
  public List<UserAuth> getAll() {
    return userAuthRepository.findAll();
  }

  @Override
  public UserAuth update(UUID id, UserAuth userAuth) {
    userAuth.setUserId(id);
    return userAuthRepository.save(userAuth);
  }

  @Override
  public void delete(UUID id) {
    userAuthRepository.deleteById(id);
  }
}

