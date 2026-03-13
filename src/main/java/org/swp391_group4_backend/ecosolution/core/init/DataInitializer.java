package org.swp391_group4_backend.ecosolution.core.init;

import org.swp391_group4_backend.ecosolution.core.domain.entity.Ward;
import org.swp391_group4_backend.ecosolution.core.repository.WardRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class DataInitializer implements ApplicationRunner {
    private final WardRepository wardRepository;
    private final org.swp391_group4_backend.ecosolution.core.repository.UserRepository userRepository;
    private final org.swp391_group4_backend.ecosolution.reporting.repository.ReportRepository reportRepository;

    public DataInitializer(WardRepository wardRepository,
                           org.swp391_group4_backend.ecosolution.core.repository.UserRepository userRepository,
                           org.swp391_group4_backend.ecosolution.reporting.repository.ReportRepository reportRepository) {
        this.wardRepository = wardRepository;
        this.userRepository = userRepository;
        this.reportRepository = reportRepository;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // Seed wards only when empty, but continue to user/report seeding regardless
        if (wardRepository.count() == 0) {
            var names = Arrays.asList(
                "Phường Thủ Dầu Một", "Phường Phú Lợi", "Phường Bình Dương", "Phường Phú An",
                "Phường Chánh Hiệp", "Xã Dầu Tiếng", "Xã Minh Thạnh", "Xã Long Hòa", "Xã Thanh An",
                "Phường Bến Cát", "Xã Trừ Văn Thố", "Xã Bàu Bàng", "Phường Chánh Phú Hòa", "Phường Long Nguyên",
                "Phường Tây Nam", "Phường Thới Hòa", "Phường Hòa Lợi", "Xã Phú Giáo", "Xã Phước Thành",
                "Xã An Long", "Xã Phước Hòa", "Phường Tân Uyên", "Phường Tân Khánh", "Xã Bắc Tân Uyên",
                "Xã Thường Tân", "Phường Vĩnh Tân", "Phường Bình Cơ", "Phường Tân Hiệp", "Phường Dĩ An",
                "Phường Tân Đông Hiệp", "Phường Đông Hòa", "Phường Lái Thiêu", "Phường Thuận Giao", "Phường An Phú",
                "Phường Thuận An", "Phường Bình Hòa", "Phường Vũng Tàu", "Phường Tam Thắng", "Phường Rạch Dừa",
                "Phường Phước Thắng", "Xã Long Sơn", "Phường Bà Rịa", "Phường Long Hương", "Phường Tam Long",
                "Xã Ngãi Giao", "Xã Xuân Sơn", "Xã Bình Giã", "Xã Châu Đức", "Xã Kim Long", "Xã Nghĩa Thành",
                "Xã Hồ Tràm", "Xã Xuyên Mộc", "Xã Bàu Lâm", "Xã Hòa Hội", "Xã Hòa Hiệp", "Xã Bình Châu",
                "Xã Long Điền", "Xã Long Hải", "Xã Đất Đỏ", "Xã Phước Hải", "Phường Phú Mỹ", "Phường Tân Hải",
                "Phường Tân Phước", "Phường Tân Thành", "Xã Châu Pha", "Đặc khu Côn Đảo", "Phường Tân Định",
                "Phường Sài Gòn", "Phường Bến Thành", "Phường Cầu Ông Lãnh", "Phường An Phú Đông", "Phường Thới An",
                "Phường Tân Thới Hiệp", "Phường Trung Mỹ Tây", "Phường Đông Hưng Thuận", "Phường Linh Xuân",
                "Phường Tam Bình", "Phường Hiệp Bình", "Phường Thủ Đức", "Phường Long Bình", "Phường Tăng Nhơn Phú",
                "Phường Phước Long", "Phường Long Phước", "Phường Long Trường", "Phường An Nhơn", "Phường An Hội Đông",
                "Phường An Hội Tây", "Phường Gò Vấp", "Phường Hạnh Thông", "Phường Thông Tây Hội", "Phường Bình Lợi Trung",
                "Phường Bình Quới", "Phường Bình Thạnh", "Phường Gia Định", "Phường Thạnh Mỹ Tây", "Phường Tân Sơn Nhất",
                "Phường Tân Sơn Hòa", "Phường Bảy Hiền", "Phường Tân Hòa", "Phường Tân Bình", "Phường Tân Sơn",
                "Phường Tây Thạnh", "Phường Tân Sơn Nhì", "Phường Phú Thọ Hòa", "Phường Phú Thạnh", "Phường Tân Phú",
                "Phường Đức Nhuận", "Phường Cầu Kiệu", "Phường Phú Nhuận", "Phường An Khánh", "Phường Bình Trưng",
                "Phường Cát Lái", "Phường Xuân Hòa", "Phường Nhiêu Lộc", "Phường Bàn Cờ", "Phường Hòa Hưng",
                "Phường Diên Hồng", "Phường Vườn Lài", "Phường Hòa Bình", "Phường Phú Thọ", "Phường Bình Thới",
                "Phường Minh Phụng", "Phường Xóm Chiếu", "Phường Khánh Hội", "Phường Vĩnh Hội", "Phường Chợ Quán",
                "Phường An Đông", "Phường Chợ Lớn", "Phường Phú Lâm", "Phường Bình Phú", "Phường Bình Tây",
                "Phường Bình Tiên", "Phường Chánh Hưng", "Phường Bình Đông", "Phường Phú Định", "Phường Bình Hưng Hòa",
                "Phường Bình Tân", "Phường Bình Trị Đông", "Phường Tân Tạo", "Phường An Lạc", "Phường Tân Hưng",
                "Phường Tân Thuận", "Phường Phú Thuận", "Phường Tân Mỹ", "Xã Tân An Hội", "Xã An Nhơn Tây",
                "Xã Nhuận Đức", "Xã Thái Mỹ", "Xã Phú Hòa Đông", "Xã Bình Mỹ", "Xã Củ Chi", "Xã Hóc Môn",
                "Xã Đông Thạnh", "Xã Xuân Thới Sơn", "Xã Bà Điểm", "Xã Tân Nhựt", "Xã Vĩnh Lộc", "Xã Tân Vĩnh Lộc",
                "Xã Bình Lợi", "Xã Bình Hưng", "Xã Hưng Long", "Xã Bình Chánh", "Xã Nhà Bè", "Xã Hiệp Phước",
                "Xã Cần Giờ", "Xã Bình Khánh", "Xã An Thới Đông", "Xã Thạnh An"
        );

            names.stream()
                    .map(n -> Ward.builder().name(n).build())
                    .forEach(wardRepository::save);
        }

        // Seed sample users and sample reports (only if users table empty)
        if (userRepository.count() == 0) {
            var citizen = org.swp391_group4_backend.ecosolution.core.domain.entity.User.builder()
                    .id(java.util.UUID.fromString("00000000-0000-0000-0000-000000000001"))
                    .username("citizen-stub")
                    .email("citizen@example.local")
                    .password("pass123")
                    .role("CITIZEN")
                    .points(0)
                    .build();

            var boss = org.swp391_group4_backend.ecosolution.core.domain.entity.User.builder()
                    .id(java.util.UUID.fromString("00000000-0000-0000-0000-000000000010"))
                    .username("boss-sample")
                    .email("boss@example.local")
                    .password("bosspass")
                    .role("BOSS")
                    .points(0)
                    .build();

            var collector1 = org.swp391_group4_backend.ecosolution.core.domain.entity.User.builder()
                    .id(java.util.UUID.fromString("00000000-0000-0000-0000-000000000011"))
                    .username("collector-1")
                    .email("collector1@example.local")
                    .password("collectorpass1")
                    .role("COLLECTOR")
                    .employerId(boss.getId())
                    .points(0)
                    .build();

            var collector2 = org.swp391_group4_backend.ecosolution.core.domain.entity.User.builder()
                    .id(java.util.UUID.fromString("00000000-0000-0000-0000-000000000012"))
                    .username("collector-2")
                    .email("collector2@example.local")
                    .password("collectorpass2")
                    .role("COLLECTOR")
                    .employerId(boss.getId())
                    .points(0)
                    .build();

            var enterprise = org.swp391_group4_backend.ecosolution.core.domain.entity.User.builder()
                    .id(java.util.UUID.fromString("00000000-0000-0000-0000-000000000020"))
                    .username("enterprise-sample")
                    .email("enterprise@example.local")
                    .password("enterpr1se")
                    .role("ENTERPRISE")
                    .points(0)
                    .build();

            userRepository.save(citizen);
            userRepository.save(boss);
            userRepository.save(collector1);
            userRepository.save(collector2);
            userRepository.save(enterprise);

            // Seed 30 sample reports: 10 PENDING, 10 ASSIGNED, 10 COLLECTED
            var wards = wardRepository.findAll();
            java.util.Random rnd = new java.util.Random(42);
            java.util.List<org.swp391_group4_backend.ecosolution.reporting.domain.entity.WasteReport> samples = new java.util.ArrayList<>();
            for (int i = 0; i < 10; i++) {
                var r = new org.swp391_group4_backend.ecosolution.reporting.domain.entity.WasteReport();
                r.setAddress("Sample Pending Address " + i);
                r.setQuantity(1.0 + i);
                r.setWard(wards.get(rnd.nextInt(wards.size())));
                r.setCreatedBy(citizen);
                r.setStatus(org.swp391_group4_backend.ecosolution.reporting.domain.ReportStatus.PENDING);
                r.setImagePath("");
                samples.add(r);
            }

            for (int i = 0; i < 10; i++) {
                var r = new org.swp391_group4_backend.ecosolution.reporting.domain.entity.WasteReport();
                r.setAddress("Sample Assigned Address " + i);
                r.setQuantity(2.0 + i);
                r.setWard(wards.get(rnd.nextInt(wards.size())));
                r.setCreatedBy(citizen);
                r.setStatus(org.swp391_group4_backend.ecosolution.reporting.domain.ReportStatus.ASSIGNED);
                r.setAssignedBy(boss.getId());
                r.setAssignedTo(i % 2 == 0 ? collector1.getId() : collector2.getId());
                r.setAssignedAt(java.time.OffsetDateTime.now().minusMinutes(10 + i));
                r.setImagePath("");
                samples.add(r);
            }

            for (int i = 0; i < 10; i++) {
                var r = new org.swp391_group4_backend.ecosolution.reporting.domain.entity.WasteReport();
                r.setAddress("Sample Collected Address " + i);
                r.setQuantity(3.0 + i);
                r.setWard(wards.get(rnd.nextInt(wards.size())));
                r.setCreatedBy(citizen);
                r.setStatus(org.swp391_group4_backend.ecosolution.reporting.domain.ReportStatus.COLLECTED);
                r.setAssignedBy(boss.getId());
                r.setAssignedTo(i % 2 == 0 ? collector1.getId() : collector2.getId());
                r.setAssignedAt(java.time.OffsetDateTime.now().minusMinutes(30 + i));
                r.setActualQuantity( (double)(4 + i) );
                // small placeholder proof image bytes
                r.setProofImage(("proof-" + i).getBytes(java.nio.charset.StandardCharsets.UTF_8));
                r.setImagePath("");
                samples.add(r);
            }

            reportRepository.saveAll(samples);

            // After saving seeded collected reports, award points to citizen to mirror normal flow
            int totalAdd = samples.stream()
                    .filter(r -> r.getStatus() == org.swp391_group4_backend.ecosolution.reporting.domain.ReportStatus.COLLECTED)
                    .mapToInt(r -> (int) Math.round((r.getActualQuantity() != null ? r.getActualQuantity() : 0.0) * 10.0))
                    .sum();
            if (totalAdd > 0) {
                citizen.setPoints((citizen.getPoints() != null ? citizen.getPoints() : 0) + totalAdd);
                userRepository.save(citizen);
            }
        }
    }
}

