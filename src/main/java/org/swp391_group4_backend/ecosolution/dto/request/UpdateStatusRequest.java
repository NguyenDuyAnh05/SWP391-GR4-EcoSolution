package org.swp391_group4_backend.ecosolution.dto.request;

import jakarta.validation.constraints.NotNull;
import org.swp391_group4_backend.ecosolution.constant.ReportStatus;

//Dto use to update status report when collector finished
public record UpdateStatusRequest(
        @NotNull(message = "Status is required")
        ReportStatus status,
        String confirmationImageUrl
) {
}
