package org.swp391_group4_backend.ecosolution.auth.domain.dto.response;

import org.swp391_group4_backend.ecosolution.auth.domain.entity.AccountRole;
import org.swp391_group4_backend.ecosolution.auth.domain.entity.AccountStatus;

public record AccountCreationResponseDto(
        String name,
        AccountRole role,
        AccountStatus status
) {

}
