package com.ecosolution.core.bootstrap;

import com.ecosolution.core.domain.entity.User;
import com.ecosolution.core.domain.entity.Ward;
import com.ecosolution.core.domain.UserRole;
import com.ecosolution.core.repository.UserRepository;
import com.ecosolution.core.repository.WardRepository;
import com.ecosolution.reporting.domain.ReportStatus;
import com.ecosolution.reporting.domain.entity.WasteReport;
import com.ecosolution.reporting.repository.WasteReportRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final WardRepository wardRepository;
    private final UserRepository userRepository;
    private final WasteReportRepository reportRepository;

    public DataInitializer(WardRepository wardRepository, UserRepository userRepository, WasteReportRepository reportRepository) {
        this.wardRepository = wardRepository;
        this.userRepository = userRepository;
        this.reportRepository = reportRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (wardRepository.count() == 0) {
            String[] names = new String[]{"Phường Thủ Dầu Một","Phường Phú Lợi","Phường Bình Dương","Phường Phú An","Phường Chánh Hiệp","Xã Dầu Tiếng","Xã Minh Thạnh","Xã Long Hòa","Xã Thanh An","Phường Bến Cát","Xã Trừ Văn Thố","Xã Bàu Bàng","Phường Chánh Phú Hòa","Phường Long Nguyên","Phường Tây Nam","Phường Thới Hòa","Phường Hòa Lợi","Xã Phú Giáo","Xã Phước Thành","Xã An Long","Xã Phước Hòa","Phường Tân Uyên","Phường Tân Khánh","Xã Bắc Tân Uyên","Xã Thường Tân","Phường Vĩnh Tân","Phường Bình Cơ","Phường Tân Hiệp","Phường Dĩ An","Phường Tân Đông Hiệp","Phường Đông Hòa","Phường Lái Thiêu","Phường Thuận Giao","Phường An Phú","Phường Thuận An","Phường Bình Hòa","Phường Vũng Tàu","Phường Tam Thắng","Phường Rạch Dừa","Phường Phước Thắng","Xã Long Sơn","Phường Bà Rịa","Phường Long Hương","Phường Tam Long","Xã Ngãi Giao","Xã Xuân Sơn","Xã Bình Giã","Xã Châu Đức","Xã Kim Long","Xã Nghĩa Thành","Xã Hồ Tràm","Xã Xuyên Mộc","Xã Bàu Lâm","Xã Hòa Hội","Xã Hòa Hiệp","Xã Bình Châu","Xã Long Điền","Xã Long Hải","Xã Đất Đỏ","Xã Phước Hải","Phường Phú Mỹ","Phường Tân Hải","Phường Tân Phước","Phường Tân Thành","Xã Châu Pha","Đặc khu Côn Đảo","Phường Tân Định","Phường Sài Gòn","Phường Bến Thành","Phường Cầu Ông Lãnh","Phường An Phú Đông","Phường Thới An","Phường Tân Thới Hiệp","Phường Trung Mỹ Tây","Phường Đông Hưng Thuận","Phường Linh Xuân","Phường Tam Bình","Phường Hiệp Bình","Phường Thủ Đức","Phường Long Bình","Phường Tăng Nhơn Phú","Phường Phước Long","Phường Long Phước","Phường Long Trường","Phường An Nhơn","Phường An Hội Đông","Phường An Hội Tây","Phường Gò Vấp","Phường Hạnh Thông","Phường Thông Tây Hội","Phường Bình Lợi Trung","Phường Bình Quới","Phường Bình Thạnh","Phường Gia Định","Phường Thạnh Mỹ Tây","Phường Tân Sơn Nhất","Phường Tân Sơn Hòa","Phường Bảy Hiền","Phường Tân Hòa","Phường Tân Bình","Phường Tân Sơn","Phường Tây Thạnh","Phường Tân Sơn Nhì","Phường Phú Thọ Hòa","Phường Phú Thạnh","Phường Tân Phú","Phường Đức Nhuận","Phường Cầu Kiệu","Phường Phú Nhuận","Phường An Khánh","Phường Bình Trưng","Phường Cát Lái","Phường Xuân Hòa","Phường Nhiêu Lộc","Phường Bàn Cờ","Phường Hòa Hưng","Phường Diên Hồng","Phường Vườn Lài","Phường Hòa Bình","Phường Phú Thọ","Phường Bình Thới","Phường Minh Phụng","Phường Xóm Chiếu","Phường Khánh Hội","Phường Vĩnh Hội","Phường Chợ Quán","Phường An Đông","Phường Chợ Lớn","Phường Phú Lâm","Phường Bình Phú","Phường Bình Tây","Phường Bình Tiên","Phường Chánh Hưng","Phường Bình Đông","Phường Phú Định","Phường Bình Hưng Hòa","Phường Bình Tân","Phường Bình Trị Đông","Phường Tân Tạo","Phường An Lạc","Phường Tân Hưng","Phường Tân Thuận","Phường Phú Thuận","Phường Tân Mỹ","Xã Tân An Hội","Xã An Nhơn Tây","Xã Nhuận Đức","Xã Thái Mỹ","Xã Phú Hòa Đông","Xã Bình Mỹ","Xã Củ Chi","Xã Hóc Môn","Xã Đông Thạnh","Xã Xuân Thới Sơn","Xã Bà Điểm","Xã Tân Nhựt","Xã Vĩnh Lộc","Xã Tân Vĩnh Lộc","Xã Bình Lợi","Xã Bình Hưng","Xã Hưng Long","Xã Bình Chánh","Xã Nhà Bè","Xã Hiệp Phước","Xã Cần Giờ","Xã Bình Khánh","Xã An Thới Đông","Xã Thạnh An"};
            for (String n : names) {
                Ward w = Ward.builder().name(n).build();
                wardRepository.save(w);
            }
        }

        if (userRepository.count() == 0) {
            User boss = User.builder().username("boss").displayName("Enterprise Boss").role(UserRole.ENTERPRISE).build();
            User citizen = User.builder().username("citizen").displayName("Demo Citizen").role(UserRole.CITIZEN).build();
            User collector = User.builder().username("collector").displayName("Collector").role(UserRole.COLLECTOR).build();
            userRepository.save(boss);
            userRepository.save(citizen);
            userRepository.save(collector);
        }

        if (reportRepository.count() == 0) {
            var wards = wardRepository.findAll();
            byte[] placeholder = new byte[256];
            var users = userRepository.findAll();
            for (int i = 0; i < 30; i++) {
                WasteReport r = WasteReport.builder()
                        .address("Seed address " + i)
                        .ward(wards.get(i % wards.size()))
                        .quantity(BigDecimal.valueOf(1 + (i % 20)))
                        .imageData(placeholder)
                        .createdBy(users.get(1))
                        .status(i < 10 ? ReportStatus.PENDING : (i < 20 ? ReportStatus.ASSIGNED : ReportStatus.COLLECTED))
                        .build();
                reportRepository.save(r);
            }
        }
    }
}

