package org.swp391_group4_backend.ecosolution.auth.service;

import org.swp391_group4_backend.ecosolution.auth.domain.AccountCreationRequest;
import org.swp391_group4_backend.ecosolution.auth.domain.entity.Account;

public interface AuthService {
  Account createAccount(AccountCreationRequest request);
}
