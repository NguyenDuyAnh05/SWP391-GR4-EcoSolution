package org.swp391_group4_backend.ecosolution.dto.response;

import org.swp391_group4_backend.ecosolution.constant.UserRole;

public record UserResponse(
    Long id, 
    String username, 
    String firstName, 
    String lastName, 
    UserRole role
) {}
