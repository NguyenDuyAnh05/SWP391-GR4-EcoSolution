package org.swp391_group4_backend.ecosolution.dto.response;

import lombok.Builder;
import lombok.Data;
import org.swp391_group4_backend.ecosolution.constant.TierType;

@Data
@Builder
public class TaskResponse {
    private Long taskId;
    private String citizenName;
    private String address;
    private String phone;
    private Double lat;
    private Double lng;
    private String status;
    private TierType tierType;
}
