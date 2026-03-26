package org.swp391_group4_backend.ecosolution.dto.response;

import lombok.Builder;
import lombok.Data;
import org.swp391_group4_backend.ecosolution.constant.ReportStatus;
import org.swp391_group4_backend.ecosolution.constant.TierType;

import java.time.LocalDate;
@Data
@Builder
public class PickupTaskResponse {
    private Long taskId;

    // Thông tin người dân
    private String citizenName;
    private String phone;

    // Thông tin địa điểm để lên Bản đồ (Leaflet/OSM)
    private String address;
    private Double latitude;
    private Double longitude;

    // Thông tin công việc
    private LocalDate scheduledDate;
    private ReportStatus status;
    private TierType tierType; // Để biết là lấy rác nhà dân hay doanh nghiệp
    
    // Ảnh bằng chứng (đối với Task đã hoàn thành)
    private String proofImageUrl;
}
