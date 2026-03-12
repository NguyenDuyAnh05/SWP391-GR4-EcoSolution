package com.ecosolution.core.service;

import com.ecosolution.core.domain.entity.User;
import com.ecosolution.core.domain.UserRole;
import com.ecosolution.core.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Demo stub - return first seeded citizen when requested
    public Optional<User> getCurrentCitizen() {
        return userRepository.findAll().stream().filter(u -> u.getRole() == UserRole.CITIZEN).findFirst();
    }
}

