package org.swp391_group4_backend.ecosolution.auth.exception;

public class InvalidCredentialsException extends RuntimeException {
  public InvalidCredentialsException() {
    super("Invalid username or password");
  }
}

