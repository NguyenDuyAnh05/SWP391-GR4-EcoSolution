package org.swp391_group4_backend.ecosolution.auth.exception;

import java.util.UUID;

public class UserNotFoundException extends RuntimeException {
  private final UUID userId;

  public UserNotFoundException(UUID userId) {
    super("User not found");
    this.userId = userId;
  }

  public UUID getUserId() {
    return userId;
  }
}

