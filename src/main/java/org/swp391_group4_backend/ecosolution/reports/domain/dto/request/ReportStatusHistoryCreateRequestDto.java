package org.swp391_group4_backend.ecosolution.reports.domain.dto.request;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import org.swp391_group4_backend.ecosolution.reports.domain.entity.ReportStatus;

public record ReportStatusHistoryCreateRequestDto(
    @NotNull(message = \
Report
ID
is
required\)
    UUID reportId,

    @NotNull(message = \Status
from
is
required\)
    ReportStatus statusFrom,

    @NotNull(message = \Status
to
is
required\)
    ReportStatus statusTo,

    @NotNull(message = \Changed
by
user
ID
is
required\)
    UUID changedById,

    String reason
) {
}
