package org.swp391_group4_backend.ecosolution.core.service.impl;

import org.swp391_group4_backend.ecosolution.core.domain.entity.User;
import org.swp391_group4_backend.ecosolution.core.repository.UserRepository;
import org.swp391_group4_backend.ecosolution.core.service.UserService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final User stub;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
        // default stub
        this.stub = User.builder()
                .id(UUID.fromString("00000000-0000-0000-0000-000000000001"))
                .username("citizen-stub")
                .role("CITIZEN")
                .points(0)
                .build();
    }

    @Override
    public User getCurrentUser() {
        // Allow overriding current user via DEV_USER_ID env var for testing
        String devUser = System.getenv("DEV_USER_ID");
        if (devUser != null && !devUser.isBlank()) {
            try {
                var id = UUID.fromString(devUser.trim());
                return userRepository.findById(id).orElse(stub);
            } catch (Exception e) {
                return stub;
            }
        }
        return stub;
    }
}

