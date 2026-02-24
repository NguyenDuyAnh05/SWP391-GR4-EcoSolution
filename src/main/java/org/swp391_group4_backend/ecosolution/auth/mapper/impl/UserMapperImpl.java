package org.swp391_group4_backend.ecosolution.auth.mapper.impl;

import org.springframework.stereotype.Component;
import org.swp391_group4_backend.ecosolution.auth.domain.UserCreationRequest;
import org.swp391_group4_backend.ecosolution.auth.domain.dto.request.UserCreationRequestDto;
import org.swp391_group4_backend.ecosolution.auth.domain.dto.response.UserCreationResponseDto;
import org.swp391_group4_backend.ecosolution.auth.domain.entity.User;
import org.swp391_group4_backend.ecosolution.auth.mapper.UserMapper;

@Component
public class UserMapperImpl implements UserMapper {
  @Override
  public UserCreationRequest fromDto(UserCreationRequestDto request) {
    return new UserCreationRequest(
            request.email(),
            request.password(),
            request.name()
    );
  }

  @Override
  public UserCreationResponseDto toDto(User user) {
    return new UserCreationResponseDto(
            user.getName(),
            user.getRole(),
            user.getStatus()
    );
  }
}
