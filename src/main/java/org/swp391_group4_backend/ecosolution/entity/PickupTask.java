package org.swp391_group4_backend.ecosolution.entity;

import jakarta.persistence.*;
import lombok.*;
import org.swp391_group4_backend.ecosolution.constant.ReportStatus;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PickupTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "subscription_id")
    private CitizenSubscription subscription; // Để lấy địa chỉ và gói cước

    @ManyToOne
    @JoinColumn(name = "collector_id")
    private User collector; // Nhân viên được phân công

    private LocalDate scheduledDate; // Ngày thu gom dự kiến

    @Enumerated(EnumType.STRING)
    private ReportStatus status; // PENDING, COMPLETED, MISSED

    private String proofImageUrl; // Ảnh bằng chứng sau khi lấy rác xong

    private Double latitude; // Tọa độ thực tế lúc thu gom (nếu cần đối soát)
    private Double longitude;

}
