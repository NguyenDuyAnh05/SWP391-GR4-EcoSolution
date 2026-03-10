PROJECT SPECIFICATION: EcoSolution MVP
Version: 4.0
Last Updated: March 10, 2026
Developer: Solo Developer + AI Assistant
Sprint: 14 Days (March 2026)

═══════════════════════════════════════════════════════════════
 1. WHAT THIS PROJECT IS
═══════════════════════════════════════════════════════════════

A waste management & recycling platform for Vietnam.
Citizens report waste. Enterprises claim and assign collectors.
Collectors complete the job with photo proof.

This is an MVP — minimum viable product. Ship in 14 days.

═══════════════════════════════════════════════════════════════
 2. TECH STACK (LOCKED)
═══════════════════════════════════════════════════════════════

 Component        | Choice                  | Reason
 ─────────────────|─────────────────────────|──────────────────────
 Language         | Java 21 (LTS)           | Stable, long-term support
 Framework        | Spring Boot 3.4.x       | Mature, well-documented
 View Engine      | Thymeleaf + Bootstrap 5 | Server-side rendering, no JS framework
 Persistence      | Spring Data JPA         | Hibernate under the hood
 Database (prod)  | MySQL 8.x (Docker)      | Persistent, production-grade
 Database (test)  | H2 in-memory            | Fast, no Docker needed for tests
 Validation       | Jakarta Bean Validation  | On DTOs only
 Mapping          | Manual (in Service)     | No MapStruct — spec constraint
 Security         | DEFERRED                | Not until main features complete
 Build            | Maven                   | Standard

═══════════════════════════════════════════════════════════════
 3. PACKAGE STRUCTURE
═══════════════════════════════════════════════════════════════

 Root: org.swp391_group4_backend.ecosolution

 ├── EcosolutionApplication.java
 │
 ├── core/                         ← Shared foundation (no controllers)
 │   ├── domain/
 │   │   ├── UserRole.java         (enum: CITIZEN, ENTERPRISE, COLLECTOR, ADMIN)
 │   │   ├── entity/
 │   │   │   └── User.java
 │   │   └── dto/
 │   ├── repository/
 │   │   └── UserRepository.java
 │   ├── service/
 │   │   ├── UserService.java
 │   │   └── impl/
 │   │       └── UserServiceImpl.java
 │   ├── mapper/impl/
 │   └── init/
 │       └── DataInitializer.java
 │
 ├── reporting/                    ← Waste report lifecycle (includes claim + assign)
 │   ├── domain/
 │   │   ├── ReportStatus.java     (enum: PENDING, ACCEPTED, ASSIGNED, COLLECTED)
 │   │   ├── entity/
 │   │   │   └── WasteReport.java
 │   │   └── dto/
 │   ├── repository/
 │   │   └── WasteReportRepository.java
 │   ├── service/
 │   │   ├── ReportService.java
 │   │   └── impl/
 │   │       └── ReportServiceImpl.java
 │   ├── mapper/impl/
 │   └── controller/
 │
 ├── web/                          ← Thymeleaf MVC controllers
 │   └── controller/
 │
 └── util/                         ← Utilities
     └── FileStorageService.java

 Dependency direction: core ← reporting ← web
                       util is standalone

═══════════════════════════════════════════════════════════════
 4. DOMAIN MODEL
═══════════════════════════════════════════════════════════════

 4.1 UserRole (enum)
   CITIZEN     — Creates waste reports, earns points.
   ENTERPRISE  — Claims PENDING reports, assigns own collectors.
   COLLECTOR   — Executes pickups, uploads proof.
   ADMIN       — Seeded role only (no custom UI in MVP).

 4.2 User (entity — core module)
   UUID id
   String username (unique)
   String email (unique)
   String password
   UserRole role
   int points (default 0)
   User employer (self-reference FK → User.id)
     → Only COLLECTOR has non-null employer
     → COLLECTOR.employer.role must be ENTERPRISE

 4.3 ReportStatus (enum)
   PENDING → ACCEPTED → ASSIGNED → COLLECTED

 4.4 WasteReport (entity — reporting module)
   UUID id
   String locationDistrict
   String imagePath (citizen upload)
   String proofImagePath (collector proof)
   ReportStatus status (default PENDING)
   User createdBy   → citizen_id FK (required)
   User acceptedBy  → enterprise_id FK (nullable)
   User collectedBy → collector_id FK (nullable)

═══════════════════════════════════════════════════════════════
 5. BUSINESS RULES
═══════════════════════════════════════════════════════════════

 BR-01 (Lifecycle):
   Status transitions must follow PENDING → ACCEPTED → ASSIGNED → COLLECTED.
   No skipping. No reversing.

 BR-02 (Claim):
   Only users with role == ENTERPRISE can claim a PENDING report.

 BR-03 (Assignment):
   Enterprise can only assign a collector where
   collector.employer.id == enterprise.id

 BR-04 (Proof):
   Cannot transition to COLLECTED unless proofImagePath is set.

 BR-05 (Seeder):
   DataInitializer is idempotent. Only seeds if userRepository.count() == 0.

 BR-06 (Points):
   When report reaches COLLECTED → createdBy.points += 10.

═══════════════════════════════════════════════════════════════
 6. IMAGE STORAGE
═══════════════════════════════════════════════════════════════

 Strategy: Actual MultipartFile upload.
 Configuration: application.properties → upload.path=${UPLOAD_DIR:uploads/}
 Default: project-relative "uploads/" directory.
 NEVER hard-code absolute paths (C:\\, /home/user, etc.).
 FileStorageService handles store(), load(), init().

═══════════════════════════════════════════════════════════════
 7. DATABASE & ENVIRONMENT
═══════════════════════════════════════════════════════════════

 Production:
   URL:  jdbc:mysql://localhost:3306/ecosolution
   Auth: Environment variables (DB_USERNAME, DB_PASSWORD)
   DDL:  spring.jpa.hibernate.ddl-auto=update

 Test:
   URL:  jdbc:h2:mem:testdb
   DDL:  spring.jpa.hibernate.ddl-auto=create-drop

 Seeded data (DataInitializer):
   1 Citizen, 1 Enterprise, 1 Collector (employer → Enterprise), 1 Admin

═══════════════════════════════════════════════════════════════
 8. TESTING STRATEGY
═══════════════════════════════════════════════════════════════

 Approach: Pragmatic TDD — batched, not all-at-once.
 Principle: Test business rules, NOT getters/setters.
 Target: Service layer is where business rules live.

 Batch 1: ReportServiceTest        — 9 tests  (BR-01, BR-04)
 Batch 2: ReportClaimAssignTest    — 7 tests  (BR-02, BR-03)
 Batch 3: DataInitializerTest      — 5 tests  (BR-05)
 Batch 4: FileStorageServiceTest   — 3 tests
 Batch 5: PointsTest               — 1 test   (BR-06)

═══════════════════════════════════════════════════════════════
 9. CONSTRAINTS FOR AI ASSISTANT
═══════════════════════════════════════════════════════════════

 C-01  Do NOT add Spring Security.
 C-02  Do NOT use MapStruct — manual mapping only.
 C-03  Do NOT use H2 in production — MySQL only.
 C-04  Do NOT use REST controllers — Thymeleaf SSR only.
 C-05  Do NOT hard-code file paths — use upload.path property.
 C-06  Deliver tests in batches — one phase at a time.
 C-07  Every production class gets a 3-bullet summary comment.
 C-08  Java 21, Spring Boot 3.4.x — do NOT use newer versions.
 C-09  Dependency direction: core ← reporting ← web (one way).
 C-10  This document is the source of truth. Follow it exactly.

═══════════════════════════════════════════════════════════════
 10. CURRENT STATE (March 10, 2026)
═══════════════════════════════════════════════════════════════

 EXISTS:
   [x] pom.xml — clean deps (needs version downgrade to 3.4.x)
   [x] application.properties — MySQL connected
   [x] UserRole.java — CITIZEN, ENTERPRISE, COLLECTOR, ADMIN
   [x] User.java — empty shell (@Entity, @Table only)
   [x] DataInitializer.java — empty file
   [x] Package skeleton — core/{controller,domain,init,mapper,repository,service}
   [x] .env.example — DB credentials template
   [x] 3 empty test files (ReportServiceTest, AssignmentServiceTest, DataInitializerTest)

 DOES NOT EXIST YET:
   [ ] ReportStatus enum
   [ ] WasteReport entity
   [ ] Any repository
   [ ] Any service interface or implementation
   [ ] Any Thymeleaf template
   [ ] Any controller
   [ ] reporting/ module package
   [ ] util/ package
   [ ] FileStorageService
   [ ] H2 test dependency in pom.xml
   [ ] application-test.properties (exists but empty)
