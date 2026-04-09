package org.swp391_group4_backend.ecosolution.auth.exception;

public class UsernameAlreadyExistsException extends RuntimeException {
  private final String username;

  public UsernameAlreadyExistsException(String username) {
    super("Username already exists");
    this.username = username;
  }

  public String getUsername() {
    return username;
  }
}

