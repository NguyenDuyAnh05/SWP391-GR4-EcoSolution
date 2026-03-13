package org.swp391_group4_backend.ecosolution.core.service;

import org.swp391_group4_backend.ecosolution.core.domain.entity.User;

public interface UserService {
    /** Return a system/citizen user stub for createdBy. */
    User getCurrentUser();
}

