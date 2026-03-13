Task receipt

I will generate a single authoritative technical specification file `Implementation_Specification.md` in the project root that documents the current implementation, the intended business rules, domain model, data model, constraints, gaps, and a prioritized set of clarifying questions. The document follows the 14-section structure you required and clearly marks any UNKNOWN / NEEDS CONFIRMATION items.

High-level plan

- Inspect the current codebase and prior analysis results.
- Produce a professional technical specification including: system overview, architecture, domain model, DB schema, data flow, business rules, validation, state machines, security, constraints, edge cases, limitations, consistency verification, and clarification questions.
- Mark missing items explicitly as UNKNOWN / NEEDS CONFIRMATION.

Checklist

- [x] Section 1 — System Overview
- [x] Section 2 — Architecture Overview
- [x] Section 3 — Domain Model
- [x] Section 4 — Database Schema
- [x] Section 5 — Data Flow Between Tables
- [x] Section 6 — Business Rules
- [x] Section 7 — Validation Rules
- [x] Section 8 — State Machines
- [x] Section 9 — Security Constraints
- [x] Section 10 — System Constraints
- [x] Section 11 — Edge Cases
- [x] Section 12 — Known Limitations
- [x] Section 13 — Consistency Verification
- [x] Section 14 — Clarification Questions


Implementation_Specification — EcoSolution MVP
Date: 2026-03-13

1. System Overview

System Name
- EcoSolution (code currently in `org.swp391_group4_backend.ecosolution.*` packages)

System Goal
- Provide a server-rendered web application for citizen reporting of waste and enterprise processing of those reports, with simple gamification. The UI is Thymeleaf + Bootstrap; the backend is Spring Boot with JPA and a local MySQL database.

Target Users
- Citizen: reports waste and tracks their submissions and points.
- Enterprise Boss: claims and assigns reports to collectors (business user).
- Collector: performs collection and uploads proof of collection.
- Administrator / Developer: deploys and maintains the system.

High Level Capabilities
- Citizen reporting UI (submit report with address, ward, quantity, photo).
- Report persistence with status tracking (PENDING, ACCEPTED, ASSIGNED, COLLECTED, CANCELLED).
- Duplicate prevention logic on submission (by address + ward within a window).
- Citizen history and dashboard showing collected reports and point totals.
- Basic simulated authentication via a stubbed `UserService`.
- Image size validation (5MB limit) and server-side mapping of multipart upload to byte[] stored in DB.

Notes about coverage vs. spec
- Several features in the new project specification (enterprise claim/assign, collector proof with actualQuantity, seeder for 30 sample reports, and some schema fields) are not fully implemented in code and are flagged later.


2. Architecture Overview

Components
- Frontend
  - Server-side Thymeleaf templates under `src/main/resources/templates/reporting/citizen/`.
  - Bootstrap 5 included via CDN in templates.

- Backend
  - Spring Boot 3.4.3 application (Java 21). Controllers, Services, Repositories, and Mappers use Spring stereotypes.
  - Key controllers: `CitizenReportController` (citizen flows), `LoginController` (simulated login redirect).
  - Key services: `ReportService` / `ReportServiceImpl`, `UserService` / `UserServiceImpl` (stub).
  - Mapper: `ReportMapperImpl` (maps `ReportRequest` to `WasteReport` entity; enforces image size rule).
  - Repositories: `WardRepository`, `ReportRepository` (JPA interfaces).

- Database
  - Local MySQL (configured in `application.properties` as `jdbc:mysql://localhost:3306/ecosolution`).
  - JPA/Hibernate with `spring.jpa.hibernate.ddl-auto=update`.

- External services
  - None integrated.

- Authentication / Authorization
  - Currently simulated: `UserServiceImpl` returns a seeded in-memory stub user (UUID `00000000-0000-0000-0000-000000000001`, username `citizen-stub`, role `CITIZEN`).
  - No Spring Security or role enforcement present; authorization enforced ad-hoc in services (e.g., ownership check on cancellation).

Component Interaction & Flow
- User requests a page -> Controller builds model using Services/Repositories -> Thymeleaf renders HTML -> Browser displays UI.
- On form submit (multipart): Controller binds `ReportRequest` -> calls `ReportService.createReport()` -> `ReportMapperImpl.toEntity()` validates and maps -> `ReportRepository.save()` persists report to `waste_reports` table.
- Duplicate detection queries `ReportRepository.findRecentByCreatedByAndAddressAndWard` with a cutoff timestamp to enforce duplicate rule.

Architectural Flow (text)
- Browser -> Controller -> Service -> Mapper -> Repository -> MySQL
- For read-only flows: Browser -> Controller -> Repository -> Thymeleaf -> Browser


3. Domain Model

Core domain entities and responsibilities below. Note: I list entities found in the code and note missing entities required by the new spec.

Entity: User
- Description: identity of a system actor. Currently mainly used as a lightweight holder for id/username/role.
- Responsibilities: identify report creators and check ownership.
- Relationships: referenced by `WasteReport.createdBy` (ManyToOne).
- Implementation notes: The code uses a stub `UserServiceImpl` that returns a single in-memory user. `User` is an entity class mapped to `users` table but there is no `UserRepository` in the current code.

Entity: Ward
- Description: flat geographic unit (no districts). A single-level geography.
- Responsibilities: keep a list of wards; reports refer directly to a ward.
- Relationships: referenced by `WasteReport.ward` (ManyToOne).
- Implementation notes: `Ward` entity exists and `WardRepository` is present. `DataInitializer` seeds an extensive list of ward names.

Entity: WasteReport
- Description: primary workflow entity representing a citizen-submitted waste report.
- Responsibilities: hold report metadata (address, ward, quantity, image, status, createdAt, createdBy).
- Relationships: ManyToOne -> Ward; ManyToOne -> User (createdBy).
- Implementation notes: Fields present are sufficient for citizen submission and cancellation; additional fields required by the new spec (proof image, actualQuantity, assignedTo) are not present.

DTOs / Records
- `ReportRequest` (record): fields `address: String`, `wardId: Long`, `quantity: Double`, `image: MultipartFile`.
- `ReportResponse` (exists but not fully analyzed) likely used for presentation mapping.

Enums
- `ReportStatus`: PENDING, ACCEPTED, ASSIGNED, COLLECTED, CANCELLED.
- `WasteType`: RECYCLABLE, NON_RECYCLABLE, OTHER.

Missing but required entities (per new spec)
- Enterprise/Organization: Not present. REQUIRED to represent enterprise users and their collectors.
- Assignment / Claim representation on `WasteReport`: fields like `assignedTo`, `assignedBy`, `employerId` not present and must be added for BR-02/BR-03.
- Proof record or separate audit entity for collection proof: not present; `WasteReport` currently contains a single `image` intended for citizen photo.


4. Database Schema

Below are tables implied by the existing JPA entities. If any specification is missing I mark it.

Table: users
- Purpose: Store user identity (basic).
- Columns:
  - id (PK) — UUID
  - username — VARCHAR
  - role — VARCHAR
- PK: id
- FKs: none
- Constraints: none explicit (username uniqueness NOT enforced in entity)
- Indexes: none declared
- Notes: No `points` column currently; necessary for gamification unless points are computed on the fly.

Table: wards
- Purpose: Flat geography list.
- Columns:
  - id (PK) — BIGINT (Long, IDENTITY)
  - name — VARCHAR (unique, not null)
- PK: id
- Constraints: `name` is `unique` and `nullable = false` per entity
- Indexes: unique index on `name`

Table: waste_reports
- Purpose: Stores citizen waste reports and workflow status.
- Columns:
  - id (PK) — UUID
  - waste_type — VARCHAR (enum)
  - address — VARCHAR
  - quantity — DOUBLE (citizen estimate)
  - image — BLOB / MEDIUMBLOB (used to store uploaded image bytes)
  - status — VARCHAR (enum: ReportStatus)
  - ward_id — BIGINT (FK -> wards.id)
  - created_by — UUID (FK -> users.id)
  - created_at — TIMESTAMP / OffsetDateTime
- PK: id
- FKs: ward_id -> wards(id); created_by -> users(id)
- Constraints: `image` stored as LOB (MEDIUMBLOB). No explicit NOT NULL annotations for `address`/`quantity`.
- Indexes: none declared. Several JPQL queries read by createdBy, ward and address; indexes on `created_by`, `ward_id`, and a normalized address would help performance.

Missing or proposed schema changes (per new spec)
- Add `actual_quantity` (DOUBLE) to capture the real measured collection quantity.
- Add `proof_image` (MEDIUMBLOB) to store collector's proof for COLLECTED.
- Add assignment fields: `assigned_to` (UUID), `assigned_by` (UUID), `assigned_at` (Timestamp).
- Add `points` column on `users` (INTEGER/DOUBLE) if points are to be stored rather than computed.


5. Data Flow Between Tables

Report Submission Flow (Citizen)
1. `Ward` entries are seeded / present in `wards` table.
2. Citizen submits form -> `ReportRequest` bound; `ReportMapperImpl` maps to `WasteReport` and fills `ward` via `WardRepository.findById(wardId)`.
3. `ReportService.createReport()` sets `createdBy` from `UserService.getCurrentUser()` and runs duplicate detection using `ReportRepository.findRecentByCreatedByAndAddressAndWard(...)`.
4. If pass, `ReportRepository.save()` inserts a row into `waste_reports` with status PENDING and created_by referencing the user.

Report Processing Flow (Enterprise / Collector) — (planned / required)
1. Enterprise user (role ENTERPRISE) claims a PENDING report -> status changes to ACCEPTED.
2. Boss assigns a collector (who must work for the boss) -> status changes to ASSIGNED and `assigned_to` set.
3. Collector performs collection -> uploads `proof_image` and reports `actual_quantity` -> status changes to COLLECTED; on that transition points are awarded to citizen.

Point awarding (Gamification)
- Points = actualQuantity * 10 (per new spec). When a report transitions to COLLECTED and `actual_quantity` is recorded, points should be added to the citizen's account.

Dependencies & order
- `wards` seeded first, then `waste_reports` referencing `ward_id` created.
- `users` must exist or `UserService` must supply `createdBy`. The current code uses a stub user rather than persisted users.


6. Business Rules (complete list)

Below are all business rules discovered or supplied by the new specification. Each is stated as a rule (BR-XX). If the current code implements the rule I mark as IMPLEMENTED or PARTIAL; if missing I mark NEEDS IMPLEMENTATION or UNKNOWN.

BR-01: Report lifecycle states MUST follow PENDING → ACCEPTED → ASSIGNED → COLLECTED.
- Implementation: `ReportStatus` contains these states. Transition enforcement: NEEDS IMPLEMENTATION.

BR-02: Only ENTERPRISE users can claim PENDING reports (i.e., transition PENDING → ACCEPTED).
- Implementation: NOT IMPLEMENTED (no claim endpoint or role enforcement).

BR-03: Bosses can only assign collectors who work for them (collector.employerId == boss.id).
- Implementation: NOT IMPLEMENTED; data model lacks employer relationships and assignment fields.

BR-04: COLLECTED status requires a byte[] proof image and an actualQuantity value; without them the transition is invalid.
- Implementation: NOT IMPLEMENTED; `WasteReport` lacks `proof_image` and `actualQuantity` fields.

BR-05: (not present in spec text) — (no additional rules provided)

BR-07: Prevent submission of the same address/ward within 5 minutes.
- Implementation: PARTIAL. Duplicate detection exists but current code uses 10 seconds window; must be changed to 5 minutes.

BR-CANCEL: Citizens may cancel their own report only while status == PENDING; cancelling sets status to CANCELLED and the report will no longer be visible to enterprises.
- Implementation: IMPLEMENTED in `ReportServiceImpl.cancelReport`.

BR-IMG: Uploaded images for citizen submission MUST be <= 5MB.
- Implementation: IMPLEMENTED in `ReportMapperImpl` (throws IllegalArgumentException on violation).

BR-POINTS: Gamification: Points awarded to a citizen = actualQuantity * 10 when a report reaches COLLECTED.
- Implementation: NOT IMPLEMENTED (dashboard currently sums quantity for collected reports but doesn't multiply and no points storage exists).

BR-OWNERSHIP: Only the creator of a report can cancel it.
- Implementation: IMPLEMENTED (owner check in cancelReport).

BR-DUP-SCOPE: (UNKNOWN) Whether duplicate prevention is global (across all users) or per-user; current implementation is per-user. Need confirmation.


7. Validation Rules

Input validation
- `ReportRequest` binding ensures typed fields (String, Long, Double, MultipartFile).
- `ReportMapperImpl` validates image size <= 5MB and rejects otherwise.
- Thymeleaf forms set `required` on some inputs (client-side). Server-side does not reject missing or invalid wardId; mapper silently ignores missing ward (this can cause null ward in `WasteReport`). This is a potential bug/unclear behavior (NEEDS CONFIRMATION / fix recommended).

Business validation
- Duplicate detection normalizes address with `String.trim().toLowerCase()` before comparing to DB.
- Duplicate cutoff currently controlled by a constant (currently 10 seconds in code; must be changed to 5 minutes per new spec).
- `cancelReport` enforces PENDING-only cancellation and ownership.

Security / upload constraints
- Only file size checked server-side. No server-side MIME type verification, no virus scanning, and images are stored directly in DB.


8. State Machines (ReportStatus)

Declared states
- PENDING
- ACCEPTED
- ASSIGNED
- COLLECTED
- CANCELLED

Allowed transitions (as required)
- PENDING → ACCEPTED (trigger: ENTERPRISE claim). Constraint: only ENTERPRISE role.
- ACCEPTED → ASSIGNED (trigger: Boss assigns collector). Constraint: assigned collector must have `employerId == boss.id`.
- ASSIGNED → COLLECTED (trigger: Collector completes job and supplies proof). Constraint: require `proof_image` and `actual_quantity`.
- PENDING → CANCELLED (trigger: Citizen cancels). Constraint: only allowed while PENDING and only by the creator.

Implementation status
- Only PENDING creation and PENDING → CANCELLED are implemented. All other transitions are not implemented and MUST be added.


9. Security Constraints

Authentication
- Current: simulated via `UserServiceImpl` which returns a seeded user (no authentication mechanism).
- Recommended: adopt Spring Security (form login or token) to enforce role-based access control.

Authorization rules
- Currently ad-hoc checks exist (e.g., `cancelReport` owner check). No centralized role checks exist.
- Required: role-based enforcement for CLAIM and ASSIGN operations (ENTERPRISE and BOSS roles) — NOT IMPLEMENTED.

Permissions
- Citizen: create report, view own reports, cancel own PENDING reports (implemented).
- Enterprise: claim PENDING reports (not implemented).
- Boss: assign collectors (not implemented).
- Collector: mark assigned jobs as COLLECTED and upload proof (not implemented).

Sensitive data protections
- Images are stored in DB as MEDIUMBLOB. There is no encryption at rest (unknown DB encryption config). Access control is only via app logic (no object-level permissions).


10. System Constraints

Technical constraints present in the codebase
- Java 21
- Spring Boot 3.4.3
- MySQL (local) — connection in `application.properties`.
- Thymeleaf + Bootstrap 5 UI (server-side rendering only).
- `spring.jpa.hibernate.ddl-auto=update` (schema auto evolution on startup).
- Packaged as a Spring Boot jar (mvn package creates a repackaged jar).

Deployment assumptions
- MySQL 8.x running locally on `localhost:3306` with database `ecosolution`.
- No Docker required.

Package naming
- Current packages: `org.swp391_group4_backend.ecosolution.*`.
- New spec states: `com.ecosolution.*` (renaming/migration unknown but recommended if required).


11. Edge Cases

- Duplicate submissions: race conditions may allow duplicate records if two near-simultaneous requests bypass the `findRecent...` check before either is saved. A DB-level constraint or unique hash + transaction should be added.
- Missing ward mapping: if `wardId` is invalid the mapper currently leaves `ward` null; DB may accept NULL if foreign key nullable, causing orphaned reports. Should reject unknown wardId.
- Image too large: `ReportMapperImpl` throws IllegalArgumentException; controller catches and shows error — ensure consistent UI flow.
- Null or unauthenticated users: current stub returns a user; if that changes to real auth then services must handle the user == null case.
- Concurrency on state transitions: no optimistic locking or transactional protections; conflicts may occur during CLAIM/ASSIGN/COMPLETE sequences.
- DB growth due to BLOBs: storing many images in DB may exhaust storage or cause performance issues.


12. Known Limitations

- Claim/Assign/Collect flows required by BR-02/BR-03/BR-04 are not implemented.
- Data model lacks `actualQuantity`, `proof_image`, and assignment fields on `WasteReport` — schema changes required.
- Duplicate detection window mismatch (10s implemented vs 5m required).
- Seeder currently seeds wards only; seeding 30 sample reports is NOT implemented.
- Authentication is simulated; no Spring Security or role enforcement.
- No collector/employer relationships or `UserRepository` for persisted users.
- Gamification (points storage) is not implemented as specified (Points = actualQuantity * 10).
- Package namespace does not match new spec `com.ecosolution.*`.


13. Consistency Verification

Contradictions found
- Duplicate window: code = 10 seconds; spec = 5 minutes. CONTRADICTION.
- Proof/actualQuantity absent in schema but required for COLLECTED. CONTRADICTION.
- Gamification implemented incorrectly (dashboard sums quantity rather than computing points). CONTRADICTION.
- Package naming mismatch (org.swp391_group4_backend vs com.ecosolution). CONTRADICTION.

Missing tables/entities
- Assignment or claim table/fields for representing who a report is assigned to.
- Enterprise / employer model and collector relationships.
- Persistent users table usage (no `UserRepository`) — while `User` class exists it is used only with a stub service.

Unclear business rules / areas needing confirmation
- Duplicate scope: global vs per-user.
- Whether citizen-submitted `quantity` should be retained distinct from `actualQuantity` (required by BR-04).
- Where and when points are persisted (user table or computed on demand).
- Exact role names and hierarchy for Enterprise Boss and Collector.

Assumptions made while reconstructing
- `quantity` holds citizen estimate; `actualQuantity` is the post-collection measured quantity (not present yet).
- `User.role` string values will be used for authorization decisions (e.g., "CITIZEN", "ENTERPRISE", "BOSS", "COLLECTOR"). This is currently an assumption.


14. Clarification Questions

Data Model
1. actualQuantity and proof image
   - Should `WasteReport` be extended to include `actualQuantity: Double` and `proofImage: byte[]`? Or should collection proof be stored in a separate `CollectionProof` entity?
2. Points storage
   - Should points be stored on the `User` entity as an accumulated numeric column, or should points be computed on demand by summing `actualQuantity * 10` for `COLLECTED` reports?
3. Assignment model
   - How should assignments be represented? Add `assignedTo` (UUID) and `assignedBy` (UUID) fields to `WasteReport`, or create a dedicated `Assignment` entity?

Business Logic
1. Duplicate detection scope
   - BR-07: Should duplicate prevention apply globally (no one can submit the same address/ward within 5 minutes) or per-citizen (the same citizen cannot submit duplicates)? Current implementation is per-user.
2. Points awarding trigger
   - When should points be awarded? Immediately when report status becomes COLLECTED and `actualQuantity` is confirmed? Should points be revokable if collection is reverted?
3. Roles and permissions
   - Confirm canonical role strings and hierarchy (e.g., CITIZEN, ENTERPRISE, BOSS, COLLECTOR) and whether roles may be applied to the same `User` entity via role field(s).

User Behavior
1. Is citizen image mandatory or optional on initial submission?
2. On ward mismatch (invalid wardId), should server reject the submission with a user-friendly error or fall back to a default behavior?
3. Should the UI present a confirmation or cooldown when the user attempts to resubmit within the duplicate window? Or rely on server-side rejection?

Security
1. Authentication approach
   - Do you want to keep the `UserService` stub during early development, or implement Spring Security with persisted users and proper login flows now?
2. Authorization enforcement
   - Do you want role-based enforcement via Spring Security annotation + method-level checks, or prefer to keep ad-hoc checks in services/controllers?

Operational / Seeder
1. Seeder specifics
   - For the requested seeded 30 sample reports (10 Pending, 10 Assigned, 10 Collected):
     - For ASSIGNED reports: who are they assigned to? If no collectors exist in the DB, how should assignment be represented?
     - For COLLECTED reports: should seeded entries include `proofImage` and `actualQuantity`? If yes, base64 small images or placeholder byte[] are acceptable.


Appendix: Recommended immediate changes to reach spec compliance

1. Data model updates
   - Add `actualQuantity: Double` and `proofImage: byte[]` to `WasteReport` (or add a separate `CollectionProof` entity).
   - Add assignment fields: `assignedTo: UUID`, `assignedBy: UUID`, `assignedAt: OffsetDateTime`.
   - Consider adding `points` on `User` if you prefer persisted points.

2. Business logic & flows
   - Implement endpoints and service methods for CLAIM (PENDING->ACCEPTED), ASSIGN (ACCEPTED->ASSIGNED), and COMPLETE (ASSIGNED->COLLECTED) with role checks and validation.
   - Enforce BR-07 with 5-minute cutoff and consider DB-level deduplication safeguards.
   - Implement points awarding when a report becomes COLLECTED.

3. Security
   - Add Spring Security with role-based access control and integrate `UserRepository` for persisted users.
   - Replace the `UserServiceImpl` stub with a security-backed current-user provider.

4. Seeder
   - Update `DataInitializer` to seed 180+ wards (already present) and additionally seed 30 sample reports using the updated schema.

5. Tests
   - Add unit & integration tests for the lifecycle transitions, duplicate detection, seeder, and points awarding logic.


End of `Implementation_Specification.md`.

If you confirm the decisions on the Clarification Questions (especially the duplicate scope and the `actualQuantity`/`proofImage` schema), I will proceed to implement the schema changes, service/controller endpoints for claim/assign/collect, and a robust data seeder that inserts 180+ wards and 30 sample reports and then run the build and smoke tests.
