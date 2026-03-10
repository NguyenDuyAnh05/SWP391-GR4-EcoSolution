TODO IMPLEMENTATION PLAN: EcoSolution MVP
Created: March 10, 2026
Reference: PROJECT_SPECIFICATION v4.0
Approach: Pragmatic TDD (Red → Green → Refactor) — Batched


═══════════════════════════════════════════════════════════════
 HOW TO USE THIS DOCUMENT
═══════════════════════════════════════════════════════════════

 1. Work top-to-bottom. Each phase depends on the previous.
 2. Check the box [x] when done.
 3. RED  = AI writes failing test skeletons.
    GREEN = You implement production code to pass tests.
    REFACTOR = Clean up, add comments, commit.
 4. If stuck, reference PROJECT_SPECIFICATION for "what" and
    DEVELOPMENT_LOG for "how I solved it last time."

═══════════════════════════════════════════════════════════════
 PHASE 0 — PROJECT SETUP (Day 1)
 Goal: Clean foundation. Everything compiles. No business logic.
═══════════════════════════════════════════════════════════════

 [ ] 0.1  Downgrade Spring Boot version
          File: pom.xml
          Change: spring-boot-starter-parent 4.0.2 → 3.4.3
          Change: spring-boot-starter-webmvc → spring-boot-starter-web
          Why: 3.4.x is stable and well-documented for a 14-day sprint.

 [ ] 0.2  Add H2 test dependency
          File: pom.xml
          Add: com.h2database:h2 with <scope>test</scope>

 [ ] 0.3  Configure test database
          File: src/test/resources/application-test.properties
          Content:
            spring.datasource.url=jdbc:h2:mem:testdb
            spring.datasource.driver-class-name=org.h2.Driver
            spring.datasource.username=sa
            spring.datasource.password=
            spring.jpa.hibernate.ddl-auto=create-drop
            spring.jpa.show-sql=true

 [ ] 0.4  Add upload path property
          File: src/main/resources/application.properties
          Add: upload.path=${UPLOAD_DIR:uploads/}

 [ ] 0.5  Create reporting module package structure
          Create empty folders:
            reporting/domain/entity/
            reporting/domain/dto/
            reporting/repository/
            reporting/service/impl/
            reporting/mapper/impl/
            reporting/controller/

 [ ] 0.6  Create util package
          Create empty folder: util/

 [ ] 0.7  Create web/controller package
          Create empty folder: web/controller/

 [ ] 0.8  Verify: mvn clean compile → BUILD SUCCESS

 [ ] 0.9  Commit: "Phase 0: Project setup — Spring Boot 3.4, H2 test, packages"

═══════════════════════════════════════════════════════════════
 PHASE 1 — BATCH 1: REPORT LIFECYCLE (Days 2–4)
 Goal: State machine works. 9 tests pass.
 Business Rules: BR-01 (status flow), BR-04 (proof required)
═══════════════════════════════════════════════════════════════

 RED (Day 2 — AI writes tests):
 ─────────────────────────────────
 [ ] 1.1  AI provides ReportServiceTest.java
          Location: src/test/java/.../reporting/service/impl/ReportServiceTest.java
          9 tests:
            Valid transitions:
              shouldTransition_FromPending_ToAccepted
              shouldTransition_FromAccepted_ToAssigned
              shouldTransition_FromAssigned_ToCollected_WhenProofProvided
            Invalid transitions (BR-01):
              shouldThrow_WhenSkipping_PendingToAssigned
              shouldThrow_WhenSkipping_PendingToCollected
              shouldThrow_WhenSkipping_AcceptedToCollected
            Terminal/reverse:
              shouldThrow_WhenTransitioning_FromCollected
              shouldThrow_WhenTransitioning_Backwards
            Proof (BR-04):
              shouldThrow_WhenCompleting_WithoutProofImage

 [ ] 1.2  Verify: mvn test → 9 tests FAIL TO COMPILE (RED confirmed)

 GREEN (Days 3–4 — You implement):
 ─────────────────────────────────
 [ ] 1.3  Create ReportStatus.java
          Location: reporting/domain/ReportStatus.java
          Values: PENDING, ACCEPTED, ASSIGNED, COLLECTED

 [ ] 1.4  Create WasteReport.java entity
          Location: reporting/domain/entity/WasteReport.java
          Fields: id, locationDistrict, imagePath, proofImagePath,
                  status (default PENDING), createdBy, acceptedBy, collectedBy
          Annotations: @Entity, @Table, @ManyToOne for User FKs

 [ ] 1.5  Create WasteReportRepository.java
          Location: reporting/repository/WasteReportRepository.java
          Extends: JpaRepository<WasteReport, UUID>

 [ ] 1.6  Create ReportService.java interface
          Location: reporting/service/ReportService.java
          Method: void transitionStatus(UUID reportId, ReportStatus newStatus)

 [ ] 1.7  Create ReportServiceImpl.java
          Location: reporting/service/impl/ReportServiceImpl.java
          Logic:
            - Define valid transitions map: PENDING→ACCEPTED, ACCEPTED→ASSIGNED, ASSIGNED→COLLECTED
            - Validate current status → new status is in the map
            - If target is COLLECTED → check proofImagePath != null (BR-04)
            - Save updated entity

 [ ] 1.8  Verify: mvn test → 9 tests PASS (GREEN confirmed)

 REFACTOR:
 ─────────────────────────────────
 [ ] 1.9  Add 3-bullet summary comment to each new class
 [ ] 1.10 Commit: "Phase 1: Report lifecycle state machine — 9 tests green"

═══════════════════════════════════════════════════════════════
 PHASE 2 — BATCH 2: USER + CLAIM + ASSIGN (Days 5–7)
 Goal: Enterprise claims reports, assigns own collectors. 7 tests pass.
 Business Rules: BR-02 (enterprise-only claim), BR-03 (own collector)
═══════════════════════════════════════════════════════════════

 RED (Day 5 — AI writes tests):
 ─────────────────────────────────
 [ ] 2.1  AI provides ReportClaimAssignTest.java
          Location: src/test/java/.../reporting/service/impl/ReportClaimAssignTest.java
          7 tests:
            Claim (BR-02):
              shouldAllowEnterprise_ToClaimPendingReport
              shouldThrow_WhenCitizenTriesToClaim
              shouldThrow_WhenCollectorTriesToClaim
              shouldThrow_WhenClaimingNonPendingReport
            Assign (BR-03):
              shouldAssignCollector_WhenBelongsToEnterprise
              shouldThrow_WhenAssigningCollectorOfDifferentEnterprise
              shouldThrow_WhenAssigningToNonAcceptedReport

 [ ] 2.2  Verify: RED (won't compile — User entity incomplete)

 GREEN (Days 6–7 — You implement):
 ─────────────────────────────────
 [ ] 2.3  Complete User.java entity
          Location: core/domain/entity/User.java
          Fields: id (UUID), username, email, password, role (UserRole),
                  points (default 0), employer (@ManyToOne self-ref)

 [ ] 2.4  Create UserRepository.java
          Location: core/repository/UserRepository.java
          Extends: JpaRepository<User, UUID>

 [ ] 2.5  Add methods to ReportService interface
          - void claimReport(UUID reportId, UUID enterpriseUserId)
          - void assignCollector(UUID reportId, UUID enterpriseUserId, UUID collectorUserId)

 [ ] 2.6  Implement claim logic in ReportServiceImpl
          - Look up user by ID
          - Check user.role == ENTERPRISE (BR-02)
          - Check report.status == PENDING
          - Set report.acceptedBy = enterprise
          - Transition status to ACCEPTED

 [ ] 2.7  Implement assign logic in ReportServiceImpl
          - Check report.status == ACCEPTED
          - Check collector.employer.id == enterprise.id (BR-03)
          - Set report.collectedBy = collector
          - Transition status to ASSIGNED

 [ ] 2.8  Verify: mvn test → all 16 tests PASS (GREEN confirmed)

 REFACTOR:
 ─────────────────────────────────
 [ ] 2.9  Commit: "Phase 2: User hierarchy + claim/assign — 16 tests green"

═══════════════════════════════════════════════════════════════
 PHASE 3 — BATCH 3: DATA SEEDER (Day 8)
 Goal: Idempotent smart seeder. 5 tests pass.
 Business Rule: BR-05 (no duplicates)
═══════════════════════════════════════════════════════════════

 RED:
 ─────────────────────────────────
 [ ] 3.1  AI provides DataInitializerTest.java
          Location: src/test/java/.../core/init/DataInitializerTest.java
          5 tests:
            shouldSeedUsers_WhenDatabaseIsEmpty
            shouldSeed_OneCitizen_OneEnterprise_OneCollector_OneAdmin
            shouldNotDuplicate_WhenRunTwice
            shouldLinkCollectorEmployer_ToEnterprise
            shouldSkipSeeding_WhenUsersAlreadyExist

 [ ] 3.2  Verify: RED

 GREEN:
 ─────────────────────────────────
 [ ] 3.3  Implement DataInitializer.java
          Location: core/init/DataInitializer.java
          Implements: CommandLineRunner
          Guard: if (userRepository.count() == 0) { seed }
          Seeds: citizen, enterprise, collector (employer→enterprise), admin

 [ ] 3.4  Verify: mvn test → all 21 tests PASS
 [ ] 3.5  Commit: "Phase 3: Idempotent data seeder — 21 tests green"

═══════════════════════════════════════════════════════════════
 PHASE 4 — FILE UPLOAD UTILITY (Day 9)
 Goal: Portable, configurable image storage. 3 tests pass.
═══════════════════════════════════════════════════════════════

 RED:
 ─────────────────────────────────
 [ ] 4.1  AI provides FileStorageServiceTest.java
          Location: src/test/java/.../util/FileStorageServiceTest.java
          3 tests:
            shouldStoreFile_AndReturnRelativePath
            shouldThrow_WhenFileIsEmpty
            shouldCreateUploadDirectory_IfMissing

 GREEN:
 ─────────────────────────────────
 [ ] 4.2  Create FileStorageService.java
          Location: util/FileStorageService.java
          @Value("${upload.path:uploads/}") — reads from property
          Methods: store(MultipartFile), load(String filename), init()
          init() called via @PostConstruct — creates directory if missing

 [ ] 4.3  Verify: mvn test → all 24 tests PASS
 [ ] 4.4  Commit: "Phase 4: Portable file storage utility — 24 tests green"

═══════════════════════════════════════════════════════════════
 PHASE 5 — THYMELEAF VIEWS + CONTROLLERS (Days 10–12)
 Goal: Clickable browser demo of full lifecycle.
═══════════════════════════════════════════════════════════════

 [ ] 5.1  Create base Thymeleaf layout
          File: src/main/resources/templates/layout.html (or fragments)
          Include: Bootstrap 5 CDN, header, footer

 [ ] 5.2  Login simulation page (NO Spring Security)
          Route: GET /login
          Logic: Dropdown to select seeded user → stores userId in HttpSession
          This is a demo shortcut, NOT real authentication.

 [ ] 5.3  Dashboard page
          Route: GET /
          Shows: All waste reports with status badges
          Controller: web/controller/DashboardController.java

 [ ] 5.4  Create Report page (Citizen)
          Route: GET /reports/new → form
          Route: POST /reports → submit with image upload
          Controller: reporting/controller/ReportController.java

 [ ] 5.5  Report Detail page
          Route: GET /reports/{id}
          Shows: status, images, assigned users, action buttons

 [ ] 5.6  Claim action (Enterprise)
          Route: POST /reports/{id}/claim
          Calls: reportService.claimReport(...)

 [ ] 5.7  Assign Collector action (Enterprise)
          Route: POST /reports/{id}/assign
          Form: dropdown of enterprise's collectors
          Calls: reportService.assignCollector(...)

 [ ] 5.8  Complete Collection action (Collector)
          Route: POST /reports/{id}/complete
          Form: proof image upload
          Calls: reportService.transitionStatus(..., COLLECTED)

 [ ] 5.9  Manual browser test: walk through full lifecycle
 [ ] 5.10 Commit: "Phase 5: Thymeleaf views — full lifecycle demo"

═══════════════════════════════════════════════════════════════
 PHASE 6 — POINTS + POLISH (Day 13)
 Goal: Points awarded, error handling, styling.
 Business Rule: BR-06 (points on COLLECTED)
═══════════════════════════════════════════════════════════════

 [ ] 6.1  Add points logic to ReportServiceImpl
          When transitionStatus → COLLECTED:
            report.getCreatedBy().points += 10
            userRepository.save(report.getCreatedBy())

 [ ] 6.2  AI provides test: shouldAward10Points_WhenReportCollected

 [ ] 6.3  Show points on dashboard or user profile

 [ ] 6.4  Error handling:
          - Thymeleaf error page for IllegalStateException
          - Flash messages for success/failure on form submissions

 [ ] 6.5  CSS polish with Bootstrap 5

 [ ] 6.6  Verify: mvn test → all 25 tests PASS
 [ ] 6.7  Commit: "Phase 6: Points + error handling + styling"

═══════════════════════════════════════════════════════════════
 PHASE 7 — FINAL (Day 14)
 Goal: Ship it.
═══════════════════════════════════════════════════════════════

 [ ] 7.1  Full test suite: mvn test → ALL PASS
 [ ] 7.2  Test with real MySQL: start Docker, seed, walk full lifecycle
 [ ] 7.3  Clean up: dead code, unused imports, TODO comments
 [ ] 7.4  Update PROJECT_SPECIFICATION with final state
 [ ] 7.5  Update DEVELOPMENT_LOG with techniques used
 [ ] 7.6  Write README.md:
          - Prerequisites (Java 21, Maven, MySQL/Docker)
          - How to run
          - Default seeded users
          - Screenshots
 [ ] 7.7  Final commit: "v1.0 — EcoSolution MVP"

═══════════════════════════════════════════════════════════════
 CALENDAR VIEW
═══════════════════════════════════════════════════════════════

  Day 1:   Phase 0 — Setup (downgrade, H2, packages)
  Day 2:   Phase 1 — RED (report lifecycle tests)
  Day 3:   Phase 1 — GREEN (entities + state machine)
  Day 4:   Phase 1 — GREEN + REFACTOR
  Day 5:   Phase 2 — RED (claim/assign tests)
  Day 6:   Phase 2 — GREEN (User entity + claim/assign)
  Day 7:   Phase 2 — GREEN + REFACTOR
  Day 8:   Phase 3 — Seeder (RED → GREEN in 1 day)
  Day 9:   Phase 4 — File upload utility
  Day 10:  Phase 5 — Thymeleaf (layout + dashboard + create report)
  Day 11:  Phase 5 — Thymeleaf (claim + assign + complete)
  Day 12:  Phase 5 — Thymeleaf (login sim + manual testing)
  Day 13:  Phase 6 — Points + polish + error handling
  Day 14:  Phase 7 — Final testing + README + ship

═══════════════════════════════════════════════════════════════
 BUSINESS RULES → PHASE TRACEABILITY
═══════════════════════════════════════════════════════════════

  BR-01 (Lifecycle)   → Phase 1 — ReportServiceTest
  BR-02 (Claim)       → Phase 2 — ReportClaimAssignTest
  BR-03 (Assignment)  → Phase 2 — ReportClaimAssignTest
  BR-04 (Proof)       → Phase 1 — ReportServiceTest
  BR-05 (Seeder)      → Phase 3 — DataInitializerTest
  BR-06 (Points)      → Phase 6 — PointsTest

