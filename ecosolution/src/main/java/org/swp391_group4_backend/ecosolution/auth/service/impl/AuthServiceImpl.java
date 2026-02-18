package org.swp391_group4_backend.ecosolution.auth.service.impl;

import org.springframework.stereotype.Service;
import org.swp391_group4_backend.ecosolution.auth.domain.AccountCreationRequest;
import org.swp391_group4_backend.ecosolution.auth.domain.entity.Account;
import org.swp391_group4_backend.ecosolution.auth.domain.entity.AccountRole;
import org.swp391_group4_backend.ecosolution.auth.domain.entity.AccountStatus;
import org.swp391_group4_backend.ecosolution.auth.repository.AuthRepository;
import org.swp391_group4_backend.ecosolution.auth.service.AuthService;

import java.time.LocalDateTime;

@Service
public class AuthServiceImpl implements AuthService {

  private final AuthRepository accountRepository;

  public AuthServiceImpl(AuthRepository accountRepository) {
    this.accountRepository = accountRepository;
  }

  @Override
  public Account createAccount(AccountCreationRequest request) {
    if(accountRepository.existsByUsername(request.username())){
      throw new RuntimeException("Username already exists");
    }

    Account account = new Account();
    LocalDateTime now = LocalDateTime.now();


    account.setUsername(request.username());
    account.setPassword(request.password());
    account.setName(request.name());
    account.setRole(AccountRole.CITIZEN);
    account.setStatus(AccountStatus.ACTIVE);
    account.setCreatedAt(now);

    return accountRepository.save(account);
  }
}
