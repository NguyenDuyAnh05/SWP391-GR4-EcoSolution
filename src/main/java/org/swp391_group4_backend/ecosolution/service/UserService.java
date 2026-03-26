package org.swp391_group4_backend.ecosolution.service;

import jakarta.servlet.http.HttpServletRequest;
import org.swp391_group4_backend.ecosolution.dto.request.ActivationRequest;
import org.swp391_group4_backend.ecosolution.dto.request.LoginRequest;
import org.swp391_group4_backend.ecosolution.dto.request.RegisterRequest;
import org.swp391_group4_backend.ecosolution.dto.response.ActivationResponse;
import org.swp391_group4_backend.ecosolution.dto.response.UserResponse;
import org.swp391_group4_backend.ecosolution.entity.User;

import java.util.List;

public interface UserService {
    UserResponse login(LoginRequest request);
    UserResponse register(RegisterRequest request);
    ActivationResponse activateService(ActivationRequest request, HttpServletRequest httpServletRequest);

}
