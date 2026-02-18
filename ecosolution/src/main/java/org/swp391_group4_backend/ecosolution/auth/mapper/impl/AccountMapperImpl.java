package org.swp391_group4_backend.ecosolution.auth.mapper.impl;

import org.springframework.stereotype.Component;
import org.swp391_group4_backend.ecosolution.auth.domain.AccountCreationRequest;
import org.swp391_group4_backend.ecosolution.auth.domain.dto.request.AccountCreationRequestDto;
import org.swp391_group4_backend.ecosolution.auth.domain.dto.response.AccountCreationResponseDto;
import org.swp391_group4_backend.ecosolution.auth.domain.entity.Account;
import org.swp391_group4_backend.ecosolution.auth.mapper.AccountMapper;

@Component
public class AccountMapperImpl implements AccountMapper {
  @Override
  public AccountCreationRequest fromDto(AccountCreationRequestDto request) {
    return new AccountCreationRequest(
            request.username(),
            request.password(),
            request.name()
    );
  }

  @Override
  public AccountCreationResponseDto toDto(Account account) {
    return new AccountCreationResponseDto(
            account.getName(),
            account.getRole(),
            account.getStatus()
    );
  }
}
