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
 * BATCH 1 — Report Lifecycle State Machine (RED)
 * 9 tests covering BR-01 (status flow) and BR-04 (proof required).
 *
 * These tests will NOT compile until you implement TODOs 01–07.
 */
@SpringBootTest
@ActiveProfiles("test")
class ReportServiceTest {

    @Autowired private ReportService reportService;       // → TODO 04, 05
    @Autowired private WasteReportRepository reportRepo;  // → TODO 03
    @Autowired private UserRepository userRepo;           // → TODO 07

    private User citizen;

    @BeforeEach
    void setUp() {
        reportRepo.deleteAll();
        userRepo.deleteAll();

        citizen = new User();
        citizen.setUsername("citizen1");
        citizen.setEmail("citizen@test.com");
        citizen.setPassword("pass");
        citizen.setRole(UserRole.CITIZEN);
        citizen = userRepo.save(citizen);   // → needs TODO 06 (User fields)
    }

    /** Helper: creates and saves a WasteReport at the given status. */
    private WasteReport createReport(ReportStatus status) {
        WasteReport r = new WasteReport();
        r.setCreatedBy(citizen);
        r.setLocationDistrict("District 1");
        r.setStatus(status);
        return reportRepo.save(r);         // → needs TODO 01, 02
    }

    // ═══════════════════════════════════════════════
    //  BR-01: Valid Transitions
    // ═══════════════════════════════════════════════

    @Nested
    @DisplayName("BR-01: Valid Transitions")
    class ValidTransitions {

        @Test
        @DisplayName("PENDING → ACCEPTED")
        void shouldTransition_FromPending_ToAccepted() {
            WasteReport r = createReport(ReportStatus.PENDING);
            reportService.transitionStatus(r.getId(), ReportStatus.ACCEPTED);
            assertEquals(ReportStatus.ACCEPTED, reportRepo.findById(r.getId()).orElseThrow().getStatus());
        }

        @Test
        @DisplayName("ACCEPTED → ASSIGNED")
        void shouldTransition_FromAccepted_ToAssigned() {
            WasteReport r = createReport(ReportStatus.ACCEPTED);
            reportService.transitionStatus(r.getId(), ReportStatus.ASSIGNED);
            assertEquals(ReportStatus.ASSIGNED, reportRepo.findById(r.getId()).orElseThrow().getStatus());
        }

        @Test
        @DisplayName("ASSIGNED → COLLECTED (with proof)")
        void shouldTransition_FromAssigned_ToCollected_WhenProofProvided() {
            WasteReport r = createReport(ReportStatus.ASSIGNED);
            r.setProofImagePath("/proof/image.jpg");
            reportRepo.save(r);

            reportService.transitionStatus(r.getId(), ReportStatus.COLLECTED);
            assertEquals(ReportStatus.COLLECTED, reportRepo.findById(r.getId()).orElseThrow().getStatus());
        }
    }

    // ═══════════════════════════════════════════════
    //  BR-01: Invalid Transitions (No Skipping)
    // ═══════════════════════════════════════════════

    @Nested
    @DisplayName("BR-01: Invalid — No Skipping")
    class InvalidSkipping {

        @Test
        @DisplayName("PENDING → ASSIGNED must throw")
        void shouldThrow_WhenSkipping_PendingToAssigned() {
            WasteReport r = createReport(ReportStatus.PENDING);
            assertThrows(IllegalStateException.class,
                    () -> reportService.transitionStatus(r.getId(), ReportStatus.ASSIGNED));
        }

        @Test
        @DisplayName("PENDING → COLLECTED must throw")
        void shouldThrow_WhenSkipping_PendingToCollected() {
            WasteReport r = createReport(ReportStatus.PENDING);
            assertThrows(IllegalStateException.class,
                    () -> reportService.transitionStatus(r.getId(), ReportStatus.COLLECTED));
        }

        @Test
        @DisplayName("ACCEPTED → COLLECTED (skips ASSIGNED) must throw")
        void shouldThrow_WhenSkipping_AcceptedToCollected() {
            WasteReport r = createReport(ReportStatus.ACCEPTED);
            assertThrows(IllegalStateException.class,
                    () -> reportService.transitionStatus(r.getId(), ReportStatus.COLLECTED));
        }
    }

    // ═══════════════════════════════════════════════
    //  BR-01: Terminal & Reverse
    // ═══════════════════════════════════════════════

    @Nested
    @DisplayName("BR-01: Terminal & Reverse")
    class TerminalAndReverse {

        @Test
        @DisplayName("COLLECTED → any must throw (terminal)")
        void shouldThrow_WhenTransitioning_FromCollected() {
            WasteReport r = createReport(ReportStatus.COLLECTED);
            assertThrows(IllegalStateException.class,
                    () -> reportService.transitionStatus(r.getId(), ReportStatus.PENDING));
        }

        @Test
        @DisplayName("ACCEPTED → PENDING (reverse) must throw")
        void shouldThrow_WhenTransitioning_Backwards() {
            WasteReport r = createReport(ReportStatus.ACCEPTED);
            assertThrows(IllegalStateException.class,
                    () -> reportService.transitionStatus(r.getId(), ReportStatus.PENDING));
        }
    }

    // ═══════════════════════════════════════════════
    //  BR-04: Proof Required for COLLECTED
    // ═══════════════════════════════════════════════

    @Nested
    @DisplayName("BR-04: Proof Required")
    class ProofRequired {

        @Test
        @DisplayName("ASSIGNED → COLLECTED without proof must throw")
        void shouldThrow_WhenCompleting_WithoutProofImage() {
            WasteReport r = createReport(ReportStatus.ASSIGNED);
            // proofImagePath is null
            assertThrows(IllegalStateException.class,
                    () -> reportService.transitionStatus(r.getId(), ReportStatus.COLLECTED));
        }
    }
}

