package org.swp391_group4_backend.ecosolution.auth.exception;

public class UserNotFoundException extends RuntimeException {
  public UserNotFoundException(String userId) {
    super("User not found with ID: " + userId);
  }
}

