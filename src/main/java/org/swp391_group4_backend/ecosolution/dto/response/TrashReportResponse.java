package org.swp391_group4_backend.ecosolution.dto.response;

import lombok.Builder;
import lombok.Data;
import org.swp391_group4_backend.ecosolution.constant.TrashReportStatus;

import java.time.LocalDateTime;

@Data
@Builder
public class TrashReportResponse {
    private Long id;
    private String citizenName;
    private String wardName;
    private TrashReportStatus status;
    private Integer totalPointsEarned;
    private LocalDateTime createdAt;
}
