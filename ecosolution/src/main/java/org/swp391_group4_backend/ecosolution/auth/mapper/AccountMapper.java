package org.swp391_group4_backend.ecosolution.auth.mapper;

import org.swp391_group4_backend.ecosolution.auth.domain.AccountCreationRequest;
import org.swp391_group4_backend.ecosolution.auth.domain.dto.request.AccountCreationRequestDto;
import org.swp391_group4_backend.ecosolution.auth.domain.dto.response.AccountCreationResponseDto;
import org.swp391_group4_backend.ecosolution.auth.domain.entity.Account;

public interface AccountMapper {
  AccountCreationRequest fromDto(AccountCreationRequestDto request);
  AccountCreationResponseDto toDto(Account account);
}
