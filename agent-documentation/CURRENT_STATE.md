CURRENT STATE — Agent Reference
Last Updated: March 10, 2026
Purpose: Quick snapshot for the AI agent to know exactly where the project is.
Convention: All TODOs remain until manually deleted. **CURRENT** marks the active task.

═══════════════════════════════════════════════════════════════
 PHASE 0 — PROJECT SETUP
═══════════════════════════════════════════════════════════════

 [x] 0.1  pom.xml — Spring Boot 3.4.3, Java 21
 [x] 0.2  pom.xml — H2 test dependency added
 [x] 0.3  application-test.properties — H2 config
 [x] 0.4  Documentation folders created (agent-documentation/, evos-documentation/)

═══════════════════════════════════════════════════════════════
 PHASE 1 — BATCH 1: REPORT LIFECYCLE (9 tests GREEN)
═══════════════════════════════════════════════════════════════

 Test file: ReportServiceTest.java — 9 tests GREEN ✅

 [x] TODO 01 — ReportStatus.java enum (PENDING, ACCEPTED, ASSIGNED, COLLECTED)
               → reporting/domain/ReportStatus.java
 [x] TODO 02 — WasteReport.java entity (02a–02d: PK, fields, status, relationships)
               → reporting/domain/entity/WasteReport.java
 [x] TODO 03 — WasteReportRepository.java (JpaRepository<WasteReport, UUID>)
               → reporting/repository/WasteReportRepository.java
 [x] TODO 04 — ReportService.java interface (transitionStatus method)
               → reporting/service/ReportService.java
 [x] TODO 05 — ReportServiceImpl.java (05a–05c: transitions map, inject repo, logic)
               → reporting/service/impl/ReportServiceImpl.java
 [x] TODO 06 — User.java entity (06a–06e: PK, identity, role, points, employer)
               → core/domain/entity/User.java
 [x] TODO 07 — UserRepository.java (JpaRepository<User, UUID>)
               → core/repository/UserRepository.java

═══════════════════════════════════════════════════════════════
 PHASE 2 — BATCH 2: CLAIM + ASSIGN (7 tests RED → your turn)
═══════════════════════════════════════════════════════════════

 Test file: ReportClaimAssignTest.java — 7 tests

 [x] TODO 08 — ReportService: add claimReport(UUID, UUID) signature
               → reporting/service/ReportService.java
 [x] TODO 09 — ReportService: add assignCollector(UUID, UUID, UUID) signature
               → reporting/service/ReportService.java
 [x] TODO 10a — ReportServiceImpl: inject UserRepository in constructor
               → reporting/service/impl/ReportServiceImpl.java

 **CURRENT** ↓

 [ ] TODO 10b — ReportServiceImpl: implement claimReport()
               → reporting/service/impl/ReportServiceImpl.java
               Rule: BR-02 (enterprise-only claim, PENDING only)
               Steps: find report → find user → check ENTERPRISE → check PENDING
                      → setAcceptedBy → setStatus(ACCEPTED) → save

 [ ] TODO 10c — ReportServiceImpl: implement assignCollector()
               → reporting/service/impl/ReportServiceImpl.java
               Rule: BR-03 (own collector only, ACCEPTED only)
               Steps: find report → check ACCEPTED → find collector
                      → check employer matches → setCollectedBy → setStatus(ASSIGNED) → save

 When done: ./mvnw clean test → expect 17 tests (10 old + 7 new) ALL GREEN

═══════════════════════════════════════════════════════════════
 NOT STARTED
═══════════════════════════════════════════════════════════════

 Phase 3: Data Seeder (Batch 3) — 5 tests
 Phase 4: File Upload — 3 tests
 Phase 5: Thymeleaf Views
 Phase 6: Points + Polish — 1 test
 Phase 7: Final
