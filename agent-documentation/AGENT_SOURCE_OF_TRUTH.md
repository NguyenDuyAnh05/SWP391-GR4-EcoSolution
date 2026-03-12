AGENT SOURCE OF TRUTH
Last Updated: 2026-03-13

Purpose
-------
This file is the agent-maintained authoritative reference for implementation decisions, confirmed business rules, constraints and the current project conventions. Use this as the single place the AI refers to when making code or documentation changes.

High-level Decisions (Confirmed)
--------------------------------
- Single-Enterprise MVP: The system is implemented and tested for one enterprise only (no multi-tenant routing). Database schema supports multiple enterprises but logic/UI focus on a single boss account for MVP.
- Authentication: No Spring Security for MVP. User identity is managed via `User` entity; controllers use a simple session-based selection for demos (login simulation).
- Image storage: store images in database as BLOB (MEDIUMBLOB). For seeded records we use 256-byte placeholders. Runtime validation: max 5 MB.
- Java & Spring: Java 21, Spring Boot 3.4.3.
- Database: Development uses Docker MySQL. Tests default to H2 in-memory but current test profile is active and uses H2; user later requested same MySQL for tests — confirm before switching. Current test runs used H2 and passed.
- TDD Approach: Service-layer first (Red → Green → Refactor). Tests drive business rules; controllers and UI follow.

Business Rules (Short Reference)
--------------------------------
- BR01: (Deprecated) Max 1 request/day — removed for MVP.
- BR02: Enterprise only may claim a PENDING request.
- BR03: Assignor (enterprise) may assign a collector only when collector.employer == enterprise.
- BR04: Proof image is required to mark a request COLLECTED.
- BR07: Duplicate requests prevented: same citizen + same address + same preferredDate + non-terminal status.
- BR15: Collector cannot reject assigned request.
- Gamification: badges at completed-counts 1,3,5,10. Points/vouchers out-of-scope for MVP.

Code Conventions & TODO Policy
------------------------------
- TODO comments are authoritative trace points. Do not remove TODO comments automatically. The agent will mark progress in `agent-documentation/CURRENT_STATE.md` and `agent-documentation/DEVELOPMENT_LOG.md`.
- TODO line format: "// TODO NN: Short title — ...". Keep the index consistent.
- When the agent implements a TODO, it will append a [x] entry in `agent-documentation/CURRENT_STATE.md` but will not delete the in-code TODO line (the user will manually remove it when ready).

Mapping: important code locations (current)
-----------------------------------------
- Report lifecycle and business logic: `src/main/java/org/swp391_group4_backend/ecosolution/reporting/service/impl/ReportServiceImpl.java`
- Report service interface: `src/main/java/org/swp391_group4_backend/ecosolution/reporting/service/ReportService.java`
- WasteReport entity: `src/main/java/org/swp391_group4_backend/ecosolution/reporting/domain/entity/WasteReport.java`
- WasteReport repository: `src/main/java/org/swp391_group4_backend/ecosolution/reporting/repository/WasteReportRepository.java`
- User domain: `src/main/java/org/swp391_group4_backend/ecosolution/core/domain/entity/User.java`
- Tests (report lifecycle & claim/assign): `src/test/java/org/swp391_group4_backend/ecosolution/.../ReportServiceTest.java` and `ReportClaimAssignTest.java`

Recent Actions (2026-03-13)
---------------------------
- Implemented `createReportForCitizen(UUID, CreateReportDto)` in `ReportServiceImpl` (TODO 11). Basic validations, duplicate check per BR-07, sets status to PENDING and timestamps.
- Added DTO `CreateReportDto` and domain fields (preferredDate, submittedQuantity, timestamps) to `WasteReport`.
- Added domain exceptions for service-layer validation.
- Extended `ReportStatus` enum to include IN_PROGRESS, CANCELLED, REJECTED.
- Ran full test suite: all existing tests passed (17 tests, 0 failures). See `./mvnw clean test` output in development log.
 - Implemented Thymeleaf UI and controller (ReportController) with session-based login simulation and create/report/detail pages. Templates added under `src/main/resources/templates`.
 - Introduced layered DTO pattern and mapping:
   - Controller DTO: `reporting.controller.dto.CreateReportRequest` (Java record)
   - Service DTO: `reporting.service.dto.CreateReportServiceDto` (record)
   - Mapper: `reporting.mapper.ReportMapper` and `reporting.mapper.impl.ReportMapperImpl`
   - Mapping flow: Controller Record -> Service DTO -> Service method -> Entity -> Repository
 - Added `GlobalExceptionHandler` (`web/GlobalExceptionHandler.java`) to centralize exception handling and map to `error.html` template.
 - Added Thymeleaf templates: `layout.html`, `index.html`, `report/create.html`, `report/detail.html`, `login.html`, `error.html`.
 - Updated `agent-documentation/CURRENT_STATE.md` and test suite ran green after UI addition (22 tests, 0 failures).

Open Design Decisions
---------------------
- Test database strategy: user previously confirmed using same MySQL for tests; current automated tests still use H2. Switching tests to MySQL requires a stable Docker environment and may slow CI. Decision: keep H2 for automated tests unless user requests otherwise.
- Where to store images in DB vs filesystem: confirmed DB BLOB for now (ease of seeding). Future: consider S3 or filesystem with `upload.path` property.

Agent maintenance rules
----------------------
- On every code change the agent makes: update `agent-documentation/CURRENT_STATE.md` with checkbox and short note and append a changelog entry to `agent-documentation/DEVELOPMENT_LOG.md`.
- Always run `./mvnw clean test` after code edits and report results in the log.

End of file

