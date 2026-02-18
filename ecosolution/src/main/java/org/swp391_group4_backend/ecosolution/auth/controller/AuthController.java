package org.swp391_group4_backend.ecosolution.auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.swp391_group4_backend.ecosolution.auth.domain.AccountCreationRequest;
import org.swp391_group4_backend.ecosolution.auth.domain.dto.request.AccountCreationRequestDto;
import org.swp391_group4_backend.ecosolution.auth.domain.dto.response.AccountCreationResponseDto;
import org.swp391_group4_backend.ecosolution.auth.domain.entity.Account;
import org.swp391_group4_backend.ecosolution.auth.mapper.AccountMapper;
import org.swp391_group4_backend.ecosolution.auth.service.AuthService;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

  private final AuthService authService;
  private final AccountMapper accountMapper;

  public AuthController(AuthService authService, AccountMapper accountMapper) {
    this.authService = authService;
    this.accountMapper = accountMapper;
  }

  @PostMapping
  public ResponseEntity<AccountCreationResponseDto> createAccount(@RequestBody AccountCreationRequestDto accountCreationRequestDto) {

    AccountCreationRequest accountCreationRequest = accountMapper.fromDto(accountCreationRequestDto);
    Account savedAccount = authService.createAccount(accountCreationRequest);
    AccountCreationResponseDto responseDto = accountMapper.toDto(savedAccount);
    return new ResponseEntity<>(responseDto, HttpStatus.CREATED) ;
  }


}
