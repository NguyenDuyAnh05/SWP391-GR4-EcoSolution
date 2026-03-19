package org.swp391_group4_backend.ecosolution.service;

import org.swp391_group4_backend.ecosolution.dto.request.LoginRequest;
import org.swp391_group4_backend.ecosolution.dto.request.RegisterRequest;
import org.swp391_group4_backend.ecosolution.dto.response.UserResponse;
import java.util.List;

public interface UserService {
    UserResponse login(LoginRequest request);
    UserResponse register(RegisterRequest request);
    List<UserResponse> getCollectors();
}
