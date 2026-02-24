package org.swp391_group4_backend.ecosolution.auth.mapper;

import org.swp391_group4_backend.ecosolution.auth.domain.UserCreationRequest;
import org.swp391_group4_backend.ecosolution.auth.domain.dto.request.UserCreationRequestDto;
import org.swp391_group4_backend.ecosolution.auth.domain.dto.response.UserCreationResponseDto;
import org.swp391_group4_backend.ecosolution.auth.domain.entity.User;

public interface UserMapper {
  UserCreationRequest fromDto(UserCreationRequestDto request);
  UserCreationResponseDto toDto(User user);
}
