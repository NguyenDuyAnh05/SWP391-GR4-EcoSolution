package org.swp391_group4_backend.ecosolution.auth.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.swp391_group4_backend.ecosolution.auth.domain.UserCreationRequest;
import org.swp391_group4_backend.ecosolution.auth.domain.dto.request.UserCreationRequestDto;
import org.swp391_group4_backend.ecosolution.auth.domain.dto.response.UserCreationResponseDto;
import org.swp391_group4_backend.ecosolution.auth.domain.entity.User;
import org.swp391_group4_backend.ecosolution.auth.mapper.UserMapper;
import org.swp391_group4_backend.ecosolution.auth.service.AuthService;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

  private final AuthService authService;
  private final UserMapper userMapper;

  public AuthController(AuthService authService, UserMapper userMapper) {
    this.authService = authService;
    this.userMapper = userMapper;
  }

  @PostMapping
  public ResponseEntity<UserCreationResponseDto> createUser(
          @RequestBody @Valid UserCreationRequestDto userCreationRequestDto) {

    UserCreationRequest userCreationRequest = userMapper.fromDto(userCreationRequestDto);
    User savedUser = authService.createUser(userCreationRequest);
    UserCreationResponseDto responseDto = userMapper.toDto(savedUser);
    return new ResponseEntity<>(responseDto, HttpStatus.CREATED) ;
  }


}
