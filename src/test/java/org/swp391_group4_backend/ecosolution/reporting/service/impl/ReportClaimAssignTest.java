package org.swp391_group4_backend.ecosolution.reporting.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import org.swp391_group4_backend.ecosolution.core.domain.UserRole;
import org.swp391_group4_backend.ecosolution.core.domain.entity.User;
import org.swp391_group4_backend.ecosolution.core.repository.UserRepository;
import org.swp391_group4_backend.ecosolution.reporting.domain.ReportStatus;
import org.swp391_group4_backend.ecosolution.reporting.domain.entity.WasteReport;
import org.swp391_group4_backend.ecosolution.reporting.repository.WasteReportRepository;
import org.swp391_group4_backend.ecosolution.reporting.service.ReportService;

import static org.junit.jupiter.api.Assertions.*;

/**
 * BATCH 2 — Claim & Assign Tests
 * Business Rules tested: BR-02 (enterprise-only claim), BR-03 (own collector only)
 * 7 tests total.
 *
 * Requires TODO 08, 09, 10a, 10b, 10c to be implemented.
 */
@SpringBootTest
@ActiveProfiles("test")
class ReportClaimAssignTest {

    @Autowired private ReportService reportService;
    @Autowired private WasteReportRepository reportRepo;
    @Autowired private UserRepository userRepo;

    private User citizen;
    private User enterprise;
    private User collector;         // belongs to enterprise
    private User otherEnterprise;
    private User otherCollector;    // belongs to otherEnterprise

    @BeforeEach
    void setUp() {
        reportRepo.deleteAll();
        userRepo.deleteAll();

        citizen = userRepo.save(User.builder()
                .username("citizen1").email("citizen@test.com").password("pass")
                .role(UserRole.CITIZEN).build());

        enterprise = userRepo.save(User.builder()
                .username("enterprise1").email("enterprise@test.com").password("pass")
                .role(UserRole.ENTERPRISE).build());

        collector = userRepo.save(User.builder()
                .username("collector1").email("collector@test.com").password("pass")
                .role(UserRole.COLLECTOR).employer(enterprise).build());

        otherEnterprise = userRepo.save(User.builder()
                .username("enterprise2").email("enterprise2@test.com").password("pass")
                .role(UserRole.ENTERPRISE).build());

        otherCollector = userRepo.save(User.builder()
                .username("collector2").email("collector2@test.com").password("pass")
                .role(UserRole.COLLECTOR).employer(otherEnterprise).build());
    }

    private WasteReport createPendingReport() {
        return reportRepo.save(WasteReport.builder()
                .createdBy(citizen)
                .locationDistrict("District 1")
                .status(ReportStatus.PENDING)
                .build());
    }

    // ═══════════════════════════════════════════════
    //  BR-02: Claim — Only Enterprise can claim PENDING
    // ═══════════════════════════════════════════════

    @Nested
    @DisplayName("BR-02: Claim Report")
    class ClaimReport {

        @Test
        @DisplayName("Enterprise can claim a PENDING report")
        void shouldAllowEnterprise_ToClaimPendingReport() {
            WasteReport r = createPendingReport();

            reportService.claimReport(r.getId(), enterprise.getId());

            WasteReport updated = reportRepo.findById(r.getId()).orElseThrow();
            assertEquals(ReportStatus.ACCEPTED, updated.getStatus());
            assertEquals(enterprise.getId(), updated.getAcceptedBy().getId());
        }

        @Test
        @DisplayName("Citizen cannot claim → throw")
        void shouldThrow_WhenCitizenTriesToClaim() {
            WasteReport r = createPendingReport();

            assertThrows(IllegalStateException.class,
                    () -> reportService.claimReport(r.getId(), citizen.getId()));
        }

        @Test
        @DisplayName("Collector cannot claim → throw")
        void shouldThrow_WhenCollectorTriesToClaim() {
            WasteReport r = createPendingReport();

            assertThrows(IllegalStateException.class,
                    () -> reportService.claimReport(r.getId(), collector.getId()));
        }

        @Test
        @DisplayName("Cannot claim non-PENDING report → throw")
        void shouldThrow_WhenClaimingNonPendingReport() {
            WasteReport r = reportRepo.save(WasteReport.builder()
                    .createdBy(citizen)
                    .locationDistrict("District 1")
                    .status(ReportStatus.ACCEPTED)
                    .build());

            assertThrows(IllegalStateException.class,
                    () -> reportService.claimReport(r.getId(), enterprise.getId()));
        }
    }

    // ═══════════════════════════════════════════════
    //  BR-03: Assign — Enterprise assigns own collector
    // ═══════════════════════════════════════════════

    @Nested
    @DisplayName("BR-03: Assign Collector")
    class AssignCollector {

        @Test
        @DisplayName("Enterprise assigns own collector to ACCEPTED report")
        void shouldAssignCollector_WhenBelongsToEnterprise() {
            WasteReport r = createPendingReport();
            reportService.claimReport(r.getId(), enterprise.getId());

            reportService.assignCollector(r.getId(), enterprise.getId(), collector.getId());

            WasteReport updated = reportRepo.findById(r.getId()).orElseThrow();
            assertEquals(ReportStatus.ASSIGNED, updated.getStatus());
            assertEquals(collector.getId(), updated.getCollectedBy().getId());
        }

        @Test
        @DisplayName("Cannot assign collector from different enterprise → throw")
        void shouldThrow_WhenAssigningCollectorOfDifferentEnterprise() {
            WasteReport r = createPendingReport();
            reportService.claimReport(r.getId(), enterprise.getId());

            // otherCollector belongs to otherEnterprise, not enterprise
            assertThrows(IllegalStateException.class,
                    () -> reportService.assignCollector(r.getId(), enterprise.getId(), otherCollector.getId()));
        }

        @Test
        @DisplayName("Cannot assign to non-ACCEPTED report → throw")
        void shouldThrow_WhenAssigningToNonAcceptedReport() {
            WasteReport r = createPendingReport(); // status = PENDING

            assertThrows(IllegalStateException.class,
                    () -> reportService.assignCollector(r.getId(), enterprise.getId(), collector.getId()));
        }
    }
}

