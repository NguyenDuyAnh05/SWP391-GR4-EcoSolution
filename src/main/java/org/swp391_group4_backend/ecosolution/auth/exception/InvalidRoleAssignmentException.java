package org.swp391_group4_backend.ecosolution.auth.exception;

import org.swp391_group4_backend.ecosolution.auth.domain.entity.UserRole;

public class InvalidRoleAssignmentException extends RuntimeException {
  private final UserRole role;

  public InvalidRoleAssignmentException(UserRole role) {
    super("Role cannot be assigned by this API");
    this.role = role;
  }

  public UserRole getRole() {
    return role;
  }
}

