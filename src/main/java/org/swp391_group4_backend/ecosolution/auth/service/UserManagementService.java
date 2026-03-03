package org.swp391_group4_backend.ecosolution.auth.service;

import org.swp391_group4_backend.ecosolution.auth.domain.dto.request.AddCollectorRequestDto;
import org.swp391_group4_backend.ecosolution.auth.domain.dto.request.AssignRoleRequestDto;
import org.swp391_group4_backend.ecosolution.auth.domain.dto.response.UserResponseDto;
import org.swp391_group4_backend.ecosolution.auth.domain.entity.User;
import org.swp391_group4_backend.ecosolution.auth.domain.entity.UserRole;

import java.util.List;
import java.util.UUID;

public interface UserManagementService {

  /**
   * Add a new collector to the system
   * @param request collector details
   * @return created user
   */
  UserResponseDto addCollector(AddCollectorRequestDto request);

  /**
   * Assign a role to an existing user (ASSIGNOR or ENTERPRISE_ADMIN)
   * Only ENTERPRISE_ADMIN can assign roles
   * @param request role assignment request
   * @return updated user
   */
  UserResponseDto assignRole(AssignRoleRequestDto request);

  /**
   * Get all users with a specific role
   * @param role the role to filter by
   * @return list of users
   */
  List<UserResponseDto> getUsersByRole(UserRole role);

  /**
   * Get all collectors
   * @return list of collectors
   */
  List<UserResponseDto> getAllCollectors();

  /**
   * Get all assignors
   * @return list of assignors
   */
  List<UserResponseDto> getAllAssignors();
}

