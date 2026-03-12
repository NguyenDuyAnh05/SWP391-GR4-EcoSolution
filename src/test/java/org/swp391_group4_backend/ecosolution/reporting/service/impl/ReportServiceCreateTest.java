package org.swp391_group4_backend.ecosolution.reporting.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import org.swp391_group4_backend.ecosolution.core.domain.UserRole;
import org.swp391_group4_backend.ecosolution.core.domain.entity.User;
import org.swp391_group4_backend.ecosolution.core.repository.UserRepository;
import org.swp391_group4_backend.ecosolution.reporting.domain.ReportStatus;
import org.swp391_group4_backend.ecosolution.reporting.domain.WasteType;
import org.swp391_group4_backend.ecosolution.reporting.repository.WasteReportRepository;
import org.swp391_group4_backend.ecosolution.reporting.service.ReportService;
import org.swp391_group4_backend.ecosolution.reporting.service.dto.CreateReportDto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class ReportServiceCreateTest {

    @Autowired
    private ReportService reportService;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private WasteReportRepository reportRepo;

    private User citizen;

    @BeforeEach
    void setUp() {
        reportRepo.deleteAll();
        userRepo.deleteAll();

        citizen = userRepo.save(User.builder()
                .username("citizen_create")
                .email("citizen_create@test.com")
                .password("pass")
                .role(UserRole.CITIZEN)
                .build());
    }

    @Test
    void createReport_Succeeds_WhenValidInput() {
        CreateReportDto dto = new CreateReportDto();
        dto.wasteType = WasteType.RECYCLABLE;
        dto.address = "123 Create St";
        dto.preferredDate = LocalDate.now().plusDays(1);
        dto.submittedQuantity = new BigDecimal("2.50");

        UUID id = reportService.createReportForCitizen(citizen.getId(), dto);
        assertNotNull(id);
        var saved = reportRepo.findById(id).orElseThrow();
        assertEquals(ReportStatus.PENDING, saved.getStatus());
        assertEquals("123 Create St", saved.getAddress());
    }

    @Test
    void createReport_Fails_WhenPreferredDateInPast() {
        CreateReportDto dto = new CreateReportDto();
        dto.wasteType = WasteType.RECYCLABLE;
        dto.address = "123 Past St";
        dto.preferredDate = LocalDate.now().minusDays(1);
        dto.submittedQuantity = new BigDecimal("1.00");

        assertThrows(RuntimeException.class, () -> reportService.createReportForCitizen(citizen.getId(), dto));
    }

    @Test
    void createReport_Fails_WhenAddressEmpty() {
        CreateReportDto dto = new CreateReportDto();
        dto.wasteType = WasteType.RECYCLABLE;
        dto.address = "   ";
        dto.preferredDate = LocalDate.now().plusDays(2);
        dto.submittedQuantity = new BigDecimal("1.00");

        assertThrows(RuntimeException.class, () -> reportService.createReportForCitizen(citizen.getId(), dto));
    }

    @Test
    void createReport_Fails_WhenDuplicateExists() {
        CreateReportDto dto = new CreateReportDto();
        dto.wasteType = WasteType.RECYCLABLE;
        dto.address = "456 Dup St";
        dto.preferredDate = LocalDate.now().plusDays(3);
        dto.submittedQuantity = new BigDecimal("3.00");

        UUID id1 = reportService.createReportForCitizen(citizen.getId(), dto);
        assertNotNull(id1);

        // second attempt should fail due to duplicate detection
        assertThrows(RuntimeException.class, () -> reportService.createReportForCitizen(citizen.getId(), dto));
    }

    @Test
    void createReport_Fails_WhenCitizenNotFound() {
        CreateReportDto dto = new CreateReportDto();
        dto.wasteType = WasteType.RECYCLABLE;
        dto.address = "789 NoUser St";
        dto.preferredDate = LocalDate.now().plusDays(1);
        dto.submittedQuantity = new BigDecimal("1.00");

        assertThrows(RuntimeException.class, () -> reportService.createReportForCitizen(UUID.randomUUID(), dto));
    }
}

