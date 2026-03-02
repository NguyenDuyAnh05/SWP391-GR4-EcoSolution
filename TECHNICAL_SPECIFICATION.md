# EcoSolution — Technical Specification

> **Version:** 3.0
> **Date:** 2026-03-03
> **Aligned with:** BUSINESS_ALIGNMENT.md v2.0, TECHNICAL_DESIGN.md v3.0

---

### 1. Technology Stack
- **Java 21 & Spring Boot 4**: The project is initialized with Java 21 and Spring Boot.
- **Spring Data JPA & MySQL**: Configured for persistence via Docker-hosted MySQL.
- **Spring Security**: **Deferred to post-MVP.** Role enforcement is handled at the service layer using placeholder headers (`X-User-Id`). See BUSINESS_ALIGNMENT.md §6, Decision #5.
- **MapStruct**: Used for DTO-Entity mapping (e.g., `WasteRequestMapper`, `UserMapper`).
- **Lombok**: Used for boilerplate reduction (`@Data`, `@Builder`, `@RequiredArgsConstructor`).
- **Swagger/OpenAPI**: Documentation standard for all `/api/v1/...` endpoints.
- **Evidence Image Storage**: Stored as BLOB directly in MySQL for MVP (Decision #11). No external service (Cloudinary, S3).

### 2. Architecture Rules (N-Layer)
The project follows the mandatory N-Layer structure as seen in the `auth`, `wasterequest`, and `gamification` modules:
- **Controller**: REST API entry points. No business logic.
- **Service**: Interface + Implementation (`impl/`) containing core business logic.
- **Domain**: Separated into `entity/` (JPA models) and `dto/` (immutable Java records for requests/responses).
- **Repository**: Spring Data JPA interfaces.
- **Mapper**: MapStruct interfaces with generated implementations.
- **Exception**: Module-specific exceptions handled by a `GlobalExceptionHandler`.

### 3. Workflow & State Machine
The `wasterequest` module is designed as a role-restricted state machine:
- **Status Lifecycle**: `PENDING` → `ASSIGNED` → `ACCEPTED` → `IN_PROGRESS` → `COMPLETED`.
- **Alternative path**: `PENDING` → `CANCELLED` (Citizen cancels before assignment).
- **Terminal States**: `COMPLETED`, `CANCELLED`. **No `REJECTED` state** — Collectors cannot reject tasks (BR15).
- **5 transitions, 6 statuses, no exceptions.** See TECHNICAL_DESIGN.md §6 for the authoritative transition table.
- **Removed transitions**: `ASSIGNED → REJECTED` (BR15), `ASSIGNED → CANCELLED` (BR03/BR21).
- **Completion requires**: Evidence image upload + final waste details (BR19).
- **Validation**: Transitions are enforced in the `statemachine/` package to ensure logic remains DRY and testable.

### 4. Core Entity Fields
The `WasteRequest` entity includes:
- **Citizen's original report** (`wasteType`, `quantity`): Set at creation, **immutable** — never overwritten.
- **Collector's adjustment** (`actualWasteType`, `actualQuantity`): Set at completion. Recorded **separately** for audit. If Collector makes no adjustment, values **auto-copy** from the citizen's original (BR18).
- **Evidence image** (`evidenceImage`): byte[] BLOB, nullable. Required at COMPLETED (BR19). Stored in database for MVP.
- **Quantity**: In **kilograms**, rounded to **2 decimal places**, must be > 0.
- **WasteType enum**: `RECYCLABLE`, `NON_RECYCLABLE`, `OTHER` (BR05 — 3-value enum for garbage classification education).
- **Optimistic locking**: `@Version` field for concurrency protection.

### 5. Security & API Standards
- **Authentication (MVP)**: Placeholder headers (`X-User-Id`). No Spring Security. Role is resolved from the database at the service layer.
- **Endpoint Prefixing**: All routes follow the `/api/v1/` convention.
- **Environment Variables**: Secrets like `DB_URL`, `JWT_SECRET`, and `GOOGLE_CLIENT_ID` are managed via environment variables (GitHub Secrets for CI/CD, `.env` for local).
- **Role Isolation**: Permissions for **Citizen**, **Assignor**, **Collector**, and **Admin** are enforced at the Service layer. No role may perform actions belonging to another role.
- **No `/reject` endpoint**: Collectors cannot reject tasks (BR15).

### 6. Business Rules Enforcement (BR01–BR34)
All 34 rules from BUSINESS_ALIGNMENT.md §5 are enforced. Key rules by implementation area:

**Request Creation:**
- **BR01**: Max 1 request per citizen per calendar day.
- **BR02**: Must include waste type, address, preferred date, quantity.
- **BR05**: Waste type must be RECYCLABLE, NON_RECYCLABLE, or OTHER.
- **BR06**: Preferred date must not be in the past.
- **BR07**: No duplicate request (same citizen + address + date + non-terminal status).
- **BR08**: Citizen must have ACTIVE account.
- **BR34**: 2-minute cooldown after cancellation before re-creation.

**Assignment:**
- **BR09**: Only Assignor may assign.
- **BR10**: Only PENDING requests can be assigned.
- **BR11**: MVP selection: Collector with fewest completed pickups today.
- **BR12**: Collector must be ACTIVE to be assigned.
- **BR13**: One Collector per request.
- **BR14**: ASSIGNED cannot revert to PENDING.

**Collector Execution:**
- **BR15**: No rejection.
- **BR16**: Accept only when ASSIGNED.
- **BR17**: Starting work → IN_PROGRESS.
- **BR18**: Adjustments recorded separately; original immutable. Auto-copy if no adjustment.
- **BR19**: Completion requires evidence image upload + final waste details.
- **BR20**: Completion → COMPLETED.

**State Integrity:**
- **BR03/BR21**: Cancel only when PENDING. No cancel after assignment.
- **BR22**: COMPLETED is immutable.
- **BR23**: IN_PROGRESS cannot be cancelled by Citizen.
- **BR24**: Strict lifecycle transitions — no skipping.

**SLA:**
- **BR25**: SLA starts at assignment.
- **BR26**: Violation = not completed within SLA duration (configurable, deferred).
- **BR27**: >3 violations → suspend Collector.

**Gamification:**
- **BR28**: Points granted at COMPLETED (voucher exchange NOT MVP).
- **BR29**: Badges at accumulated completion milestones (1st, 3rd, 5th, 10th).
- **BR30**: Badges permanent, non-transferable, no expiry.

**Administration:**
- **BR31**: Admin suspends any user.
- **BR32**: Admin intervenes in IN_PROGRESS.
- **BR33**: Suspended users cannot create/receive/perform.

### 7. MVP Priority
Gamification is **MVP-critical**, not deferred. Garbage classification is a new concept in the target country — badges and social sharing drive education and adoption.

**Priority order**:
1. Citizen creates request (with waste type + quantity in kg)
2. Full lifecycle (PENDING → ASSIGNED → ACCEPTED → IN_PROGRESS → COMPLETED with evidence)
3. Gamification — Badges auto-awarded at 1st/3rd/5th/10th completions, social sharing. Points tracked but voucher exchange deferred.
4. Collector adjustment with audit trail
5. Assignor workflow (BR11 algorithm)
6. Admin controls (BR31/BR32)

### 8. Testing & Error Handling
- **TDD Approach**: Unit tests focus on validation logic, state transitions, permission enforcement, the quantity/adjustment model, evidence requirement, and cooldown logic.
- **Consistency**: A unified `ErrorResponseDto` ensures all API errors return a consistent format.
- **Domain Exceptions**: `InvalidStateTransitionException`, `UnauthorizedRoleException`, `UserNotFoundException`, `UserSuspendedException`, `DailyRequestLimitExceededException`, `DuplicateWasteRequestException`, `CancelCooldownException`, `EvidenceRequiredException` — all wired into `GlobalExceptionHandler`.

The project is structured and ready for implementation following the MVP priority order defined in BUSINESS_ALIGNMENT.md §8.
