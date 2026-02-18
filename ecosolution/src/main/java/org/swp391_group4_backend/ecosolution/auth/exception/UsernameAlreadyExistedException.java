package org.swp391_group4_backend.ecosolution.auth.exception;

public class UsernameAlreadyExistedException extends RuntimeException {
  private final String username;


  public UsernameAlreadyExistedException(String username) {
    super(
            String.format("User with username %s already exists", username)
    );
    this.username = username;

  }

  public String getUsername() {
    return username;
  }
}
