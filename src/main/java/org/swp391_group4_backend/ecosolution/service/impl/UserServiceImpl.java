package org.swp391_group4_backend.ecosolution.service.impl;

import org.springframework.stereotype.Service;
import org.swp391_group4_backend.ecosolution.constant.UserRole;
import org.swp391_group4_backend.ecosolution.dto.request.LoginRequest;
import org.swp391_group4_backend.ecosolution.dto.request.RegisterRequest;
import org.swp391_group4_backend.ecosolution.dto.response.UserResponse;
import org.swp391_group4_backend.ecosolution.entity.User;
import org.swp391_group4_backend.ecosolution.repository.UserRepository;
import org.swp391_group4_backend.ecosolution.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Simple plain-text check for demo purposes
        if (!user.getPassword().equals(request.password())) {
            throw new RuntimeException("Invalid password");
        }
        return new UserResponse(user.getId(), user.getUsername(), user.getFirstName(), user.getLastName(),
                user.getRole());
    }

    @Override
    public UserResponse register(RegisterRequest request) {
        if (userRepository.findByUsername(request.username()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }
        User user = User.builder()
            .username(request.username())
            .password(request.password())
            .firstName(request.firstName())
            .lastName(request.lastName())
            .role(UserRole.CITIZEN)
            .build();
        User savedUser = userRepository.save(user);
        return new UserResponse(savedUser.getId(), savedUser.getUsername(), savedUser.getFirstName(), savedUser.getLastName(), savedUser.getRole());
    }

    @Override
    public List<UserResponse> getCollectors() {
        return userRepository.findByRole(UserRole.COLLECTOR)
                .orElse(List.of())
                .stream()
                .map(u -> new UserResponse(u.getId(), u.getUsername(), u.getFirstName(), u.getLastName(), u.getRole()))
                .collect(Collectors.toList());
    }
}
