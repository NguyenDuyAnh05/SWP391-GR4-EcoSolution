# EcoSolution — Technical Design Document

> **Version:** 3.0
> **Date:** 2026-03-03
> **Status:** Aligned with BUSINESS_ALIGNMENT.md v2.0 — BR01–BR34 fully integrated.
> **Source of Truth:** [BUSINESS_ALIGNMENT.md](./BUSINESS_ALIGNMENT.md)

---

## Table of Contents

1. [System Purpose](#1-system-purpose)
2. [Business Actors & Roles](#2-business-actors--roles)
3. [Domain Model](#3-domain-model)
4. [Core Entity: WasteRequest](#4-core-entity-wasterequest)
5. [Quantity & Collector Adjustment Model](#5-quantity--collector-adjustment-model)
6. [State Machine](#6-state-machine)
7. [Permission Matrix](#7-permission-matrix)
8. [Business Invariants](#8-business-invariants)
9. [Edge Cases & Expected Behavior](#9-edge-cases--expected-behavior)
10. [Environment & Secrets Strategy](#10-environment--secrets-strategy)
11. [Package Structure](#11-package-structure)
12. [API Contract Overview](#12-api-contract-overview)
13. [Data Integrity & Concurrency](#13-data-integrity--concurrency)
14. [MVP Priority & Gamification](#14-mvp-priority--gamification)
15. [Open Decisions](#15-open-decisions)

---

## 1. System Purpose

EcoSolution is a **workflow management platform** for waste pickup services.
It replaces informal, manual coordination with a **controlled digital process**.

The system is **not** a generic CRUD application.
It is a **role-restricted state machine** where every operation is a controlled state transition on a central business object.

**Primary objectives:**

- Structured request submission by citizens.
- Controlled assignment process by operations staff.
- Defined status transitions enforced by the system.
- Clear role separation for accountability.
- Full operational traceability.

**Secondary objective (MVP-critical):**

- Educate citizens on garbage classification — a **new concept** in the target country.
- Incentivize participation through gamification (points, badges, social media sharing).

---

## 2. Business Actors & Roles

| Role         | Responsibility                                                              | Analogy      |
|--------------|-----------------------------------------------------------------------------|--------------|
| **Citizen**  | Creates pickup requests (with waste type, quantity in kg, address, coordinates, preferred date). Tracks status. Cancels only when PENDING (BR03). Earns badges. | Customer     |
| **Assignor** | Reviews pending requests. Assigns a collector (BR09). Monitors workflow and SLA. Does **not** execute. | Dispatcher   |
| **Collector**| Accepts assignments (BR16). Cannot reject (BR15). Performs pickup. Adjusts waste type/quantity (BR18, recorded separately for audit). Completes with evidence image (BR19). | Field worker |
| **Admin**    | Full RBAC. Suspends accounts (BR27, BR31). Intervenes in IN_PROGRESS requests (BR32). **Not** part of the standard workflow. | System admin |

### Role Isolation Principle

No role may perform actions defined for another role.
Unauthorized operations must be **rejected at the service layer**, not just hidden in the UI.

> **Authentication (MVP):** No Spring Security. Placeholder headers (`X-User-Id`) are used. Role enforcement happens at the service layer by checking the user's role from the database. Spring Security integration is deferred to post-MVP (see BUSINESS_ALIGNMENT.md §6, Decision #5).

---

## 3. Domain Model

### Entity Relationships

```
┌──────────┐        1 ──── *        ┌───────────────┐        0..1 ──── 1        ┌───────────┐
│  Citizen  │ ───────────────────▶  │  WasteRequest  │ ◀──────────────────────── │ Collector  │
│  (User)   │   creates             │  (core entity) │   assigned to             │  (User)    │
└──────────┘                        └───────────────┘                            └───────────┘
                                           ▲
                                           │ manages assignment
                                    ┌──────┴──────┐
                                    │   Assignor   │
                                    │   (User)     │
                                    └─────────────┘
```

- **One Citizen** creates **many WasteRequests**.
- **Each WasteRequest** belongs to **exactly one Citizen** (`citizenId` — mandatory, immutable).
- **Each WasteRequest** has **at most one Collector** (`assignedCollectorId` — nullable, set on ASSIGN).
- **Assignor** manages the assignment but is never the executor and is not persisted on the request.
- All actors are rows in the **same `users` table**, differentiated by `UserRole`.

---

## 4. Core Entity: WasteRequest

| Attribute             | Type          | Constraints                                       |
|-----------------------|---------------|---------------------------------------------------|
| `id`                  | UUID          | PK, auto-generated, immutable                     |
| `citizenId`           | UUID          | FK → `users.id`, mandatory, immutable             |
| `wasteType`           | Enum (String) | Mandatory. Values: `RECYCLABLE`, `NON_RECYCLABLE`, `OTHER` |
| `quantity`            | BigDecimal    | Mandatory, in kilograms, rounded to 2 decimal places, must be > 0 |
| `address`             | String        | Mandatory, non-empty                              |
| `latitude`            | Double        | Mandatory, range: -90 to 90                       |
| `longitude`           | Double        | Mandatory, range: -180 to 180                     |
| `preferredDate`       | LocalDate     | Mandatory, must not be in the past at creation     |
| `status`              | Enum (String) | Mandatory, default = `PENDING`                    |
| `assignedCollectorId` | UUID          | FK → `users.id`, nullable, set when ASSIGNED       |
| `actualWasteType`     | Enum (String) | Nullable. Set at COMPLETED (by Collector or auto-copy from original) |
| `actualQuantity`      | BigDecimal    | Nullable. Set at COMPLETED (by Collector or auto-copy). Kilograms, 2 decimal places |
| `evidenceImage`       | byte[] (BLOB) | Nullable. Required at COMPLETED (BR19). Stored directly in database for MVP. |
| `createdAt`           | LocalDateTime | Auto-set on creation, immutable                   |
| `updatedAt`           | LocalDateTime | Auto-set on every state change                    |
| `version`             | Long          | Optimistic locking (`@Version`)                   |

### WasteType Enum

```
RECYCLABLE, NON_RECYCLABLE, OTHER
```

> 3-value enum aligned with the educational goal of teaching citizens basic garbage classification (BR05).

### RequestStatus Enum

```
PENDING, ASSIGNED, ACCEPTED, IN_PROGRESS, COMPLETED, CANCELLED
```

> **No `REJECTED` status.** Collectors cannot reject tasks (BR15).

---

## 5. Quantity & Collector Adjustment Model

### Citizen's Original Report

At creation, the Citizen provides `wasteType` and `quantity` (kg, 2 decimal places).
These values are **immutable after creation** — the citizen's original report is never overwritten.

### Collector's Adjustment (BR18)

During pickup, the Collector may observe different waste type or quantity.
The adjustment is recorded in the `actual*` fields — **separate from the citizen's original**.

| Field              | Set By                     | When              | Mutable? |
|--------------------|----------------------------|-------------------|----------|
| `wasteType`        | Citizen                    | At creation       | No — immutable |
| `quantity`         | Citizen                    | At creation       | No — immutable |
| `actualWasteType`  | Collector (or auto-copy)   | At completion     | No — set once |
| `actualQuantity`   | Collector (or auto-copy)   | At completion     | No — set once |

### Default Behavior (Auto-Copy)

If the Collector makes **no adjustment**, the `actual*` fields are **automatically copied from the citizen's original values** at completion. Every completed request always has both records — they are simply identical when no adjustment was made.

> The `actual*` fields are `null` until the request reaches `COMPLETED`.

---

## 6. State Machine

This is the **authoritative, non-negotiable** lifecycle of a WasteRequest.

### Transition Graph

```
PENDING ──────→ ASSIGNED ──────→ ACCEPTED ──────→ IN_PROGRESS ──────→ COMPLETED ✓
  │
  ↓
CANCELLED ✓
```

### Transition Table

| From          | To            | Triggered By | Action Description                    |
|---------------|---------------|--------------|---------------------------------------|
| `PENDING`     | `ASSIGNED`    | Assignor     | Assign a collector to the request     |
| `PENDING`     | `CANCELLED`   | Citizen      | Citizen withdraws before assignment   |
| `ASSIGNED`    | `ACCEPTED`    | Collector    | Collector accepts the assignment      |
| `ACCEPTED`    | `IN_PROGRESS` | Collector    | Collector starts the pickup work      |
| `IN_PROGRESS` | `COMPLETED`   | Collector    | Collector finishes the pickup (evidence required — BR19) |

**5 transitions. 6 statuses. No exceptions.**

### Removed Transitions (per BUSINESS_ALIGNMENT.md §6)

| Removed Transition         | Reason                                  |
|----------------------------|-----------------------------------------|
| `ASSIGNED → REJECTED`      | BR15: Collector cannot reject tasks. `REJECTED` status removed entirely. |
| `ASSIGNED → CANCELLED`     | BR03/BR21: Citizen cannot cancel after assignment. Cancel only allowed when PENDING. |

### Terminal States

| State       | Meaning                                   |
|-------------|-------------------------------------------|
| `COMPLETED` | Work done. Request is immutable.          |
| `CANCELLED` | Citizen withdrew. Request is immutable.   |

> **No transitions exist outside this table.**
> Any attempt to perform an undefined transition must be **rejected with an error**.

---

## 7. Permission Matrix

### Citizen Permissions

| Action            | Precondition                                               |
|-------------------|------------------------------------------------------------|
| Create request    | Valid input, `preferredDate` not past (BR06), max 1 per day (BR01), no duplicate (BR07), user status = `ACTIVE` (BR08), 2-min cooldown after cancel (BR34) |
| View own requests | `citizenId` matches authenticated user                     |
| Cancel request    | Status = `PENDING` only (BR03/BR21)                        |

### Assignor Permissions

| Action           | Precondition                                          |
|------------------|-------------------------------------------------------|
| Assign collector | Status = `PENDING` (BR10), collector exists, has role `COLLECTOR`, and is `ACTIVE` (BR12) |
| View all requests| No precondition                                       |

### Collector Permissions

| Action            | Precondition                                                           |
|-------------------|------------------------------------------------------------------------|
| Accept assignment | Status = `ASSIGNED` (BR16), `assignedCollectorId` matches authenticated user |
| Mark IN_PROGRESS  | Status = `ACCEPTED`, `assignedCollectorId` matches authenticated user  |
| Mark COMPLETED    | Status = `IN_PROGRESS`, `assignedCollectorId` matches authenticated user, evidence image provided (BR19) |

> **Ownership check:** A Collector can only act on requests assigned to **them**.
> **Any unauthorized action → HTTP 403.**
> **No reject action exists.** Collectors cannot decline tasks (BR15).

---

## 8. Business Invariants

These rules **must never be violated**, regardless of implementation approach.

| #  | Invariant                                                                  |
|----|----------------------------------------------------------------------------|
| 1  | A WasteRequest always has **exactly one** valid status.                    |
| 2  | A `COMPLETED` or `CANCELLED` request **cannot be modified** (BR22).        |
| 3  | A WasteRequest can have **at most one** assigned collector (BR13).         |
| 4  | State transitions **must follow the defined graph** — no skipping (BR24).  |
| 5  | Unauthorized role actions **must be rejected** — no silent failures.       |
| 6  | `preferredDate` **must not be in the past** at creation time (BR06).       |
| 7  | `citizenId` is **immutable** after creation.                               |
| 8  | `assignedCollectorId` is set **only** during the PENDING → ASSIGNED transition. |
| 9  | A Citizen can create **at most 1 request per day** (BR01).                 |
| 10 | A Collector **cannot reject** a task (BR15). No `REJECTED` status exists.  |
| 11 | A Citizen **cannot cancel** after assignment (BR03/BR21). Cancel only when `PENDING`. |
| 12 | A suspended user **cannot create requests, receive assignments, or perform pickups** (BR08/BR33). |
| 13 | The citizen's original `wasteType` and `quantity` are **immutable** — never overwritten. |
| 14 | `actualWasteType` and `actualQuantity` are set **only once** at COMPLETED, either by Collector adjustment or auto-copy from original (BR18). |
| 15 | `quantity` and `actualQuantity` are in **kilograms**, rounded to **2 decimal places**, must be > 0. |
| 16 | A **duplicate request** (same citizen + same address + same date + non-terminal status) is **not allowed** (BR07). |
| 17 | Completion requires **evidence image upload** (BR19). No image = no completion. |
| 18 | ASSIGNED status **cannot revert to PENDING** (BR14).                       |
| 19 | After cancelling, a Citizen must wait **2 minutes** before creating a new request (BR34). |
| 20 | A request **cannot be modified** after it has been assigned to a Collector (BR04). |

---

## 9. Edge Cases & Expected Behavior

| #  | Edge Case                                           | Expected System Behavior                                                    |
|----|-----------------------------------------------------|-----------------------------------------------------------------------------|
| 1  | Invalid waste type submitted                        | Reject with 400 — only `RECYCLABLE`, `NON_RECYCLABLE`, `OTHER` accepted (BR05). |
| 2  | `preferredDate` is in the past                      | Reject with 400 — "Preferred date must not be in the past" (BR06).          |
| 3  | Assignor assigns a non-existing collector            | Reject with 404 — "Collector not found."                                    |
| 4  | Assignor assigns a user who is not a COLLECTOR role  | Reject with 400 — "Target user is not a collector."                         |
| 5  | Assignor assigns a suspended collector               | Reject with 400 — "Collector is not active" (BR12).                         |
| 6  | Collector tries to complete without accepting first  | Reject with 409 — invalid state transition (BR24).                          |
| 7  | Citizen tries to cancel after ASSIGNED               | Reject with 409 — cancellation only allowed when PENDING (BR03/BR21).       |
| 8  | Citizen already has a non-terminal request today     | Reject with 409 — "Maximum 1 request per day" (BR01).                       |
| 9  | Two Assignors assign the same PENDING request concurrently | One succeeds, the other gets 409 — enforced via optimistic locking.   |
| 10 | Collector acts on a request not assigned to them     | Reject with 403 — "You are not the assigned collector."                     |
| 11 | Any modification to a terminal-state request         | Reject with 409 — "Request is in a terminal state and cannot be modified" (BR22). |
| 12 | Quantity is zero or negative                         | Reject with 400 — "Quantity must be greater than 0."                        |
| 13 | Quantity has more than 2 decimal places              | Reject with 400 — "Quantity must be rounded to 2 decimal places."           |
| 14 | Suspended citizen tries to create request            | Reject with 403 — "Your account is suspended" (BR08/BR33).                  |
| 15 | Collector tries to reject a task                     | No endpoint exists. If attempted via invalid transition → 409 (BR15).       |
| 16 | Collector completes without providing adjustment     | `actualWasteType` and `actualQuantity` auto-copy from citizen's original (BR18). |
| 17 | Collector tries to complete without evidence image   | Reject with 400 — "Evidence image is required" (BR19).                      |
| 18 | Duplicate request (citizen+address+date+non-terminal)| Reject with 409 — "A request for this address and date already exists" (BR07). |
| 19 | Citizen creates request within 2 min of cancelling   | Reject with 429 — "Please wait before creating a new request" (BR34).       |
| 20 | Suspended collector targeted for assignment          | Reject with 400 — "Collector is not active" (BR12/BR33).                    |

---

## 10. Environment & Secrets Strategy

All sensitive configuration is externalized via environment variables.
**No secrets are ever committed to Git.**

### Secret Categories

| Secret                | Env Variable            | Used For                      |
|-----------------------|-------------------------|-------------------------------|
| DB connection URL     | `DB_URL`                | MySQL (Docker)                |
| DB username           | `DB_USER`               | MySQL                         |
| DB password           | `DB_PASS`               | MySQL                         |
| JWT signing key       | `JWT_SECRET`            | Token authentication (post-MVP) |
| Google OAuth client   | `GOOGLE_CLIENT_ID`      | Google login (post-MVP)       |
| Google OAuth secret   | `GOOGLE_CLIENT_SECRET`  | Google login (post-MVP)       |

> **Note:** Evidence images (BR19) are stored directly in the MySQL database as BLOB for MVP. No external image storage service (Cloudinary, S3) is needed. This can be migrated post-MVP if performance requires it.

### Local Development

- Copy `.env.example` → `.env` and fill in values.
- `.env` is in `.gitignore` — never committed.
- `application.properties` / `application-dev.properties` reference vars with safe defaults:
  `${DB_URL:jdbc:mysql://localhost:3306/SWP391_EcoSolution}`.

### CI/CD (GitHub Actions)

- Store secrets in **GitHub Repository Secrets** (Settings → Secrets and variables → Actions).
- Reference in workflows as `${{ secrets.DB_PASS }}` etc.
- Never echo or log secret values.

### Production

- Inject via the hosting platform's environment configuration (e.g., Docker `--env-file`, cloud provider secret manager).
- `JWT_SECRET` must be a strong, unique key (not the dev default).
- `ddl-auto` must be `validate` or `none` — never `update`.

---

## 11. Package Structure

```
org.swp391_group4_backend.ecosolution
├── auth/                              # Authentication & user management
│   ├── controller/
│   ├── domain/
│   │   ├── dto/
│   │   │   ├── request/
│   │   │   └── response/
│   │   └── entity/                    # User, UserAuth, UserRole, UserStatus
│   ├── exception/
│   ├── mapper/
│   │   └── impl/
│   ├── repository/
│   └── service/
│       └── impl/
│
├── wasterequest/                      # Core workflow module
│   ├── controller/
│   ├── domain/
│   │   ├── dto/
│   │   │   ├── request/
│   │   │   └── response/
│   │   └── entity/                    # WasteRequest, RequestStatus, WasteType
│   ├── exception/
│   ├── mapper/
│   │   └── impl/
│   ├── repository/
│   ├── service/
│   │   └── impl/
│   └── statemachine/                  # Transition validation logic
│
├── gamification/                      # Points, badges, social sharing (MVP-critical)
│   ├── controller/
│   ├── domain/
│   │   ├── dto/
│   │   └── entity/                    # Point, Badge, CitizenBadge
│   ├── repository/
│   └── service/
│       └── impl/
│
└── common/                            # Shared cross-cutting concerns
    ├── domain/
    │   └── dto/
    │       └── response/              # ErrorResponseDto, etc.
    └── exception/                     # GlobalExceptionHandler
```

> The `statemachine/` sub-package isolates transition validation logic so it can be unit-tested independently from the service layer.
> The `gamification/` package is **MVP-critical** — not deferred (see BUSINESS_ALIGNMENT.md §8).

---

## 12. API Contract Overview

### Citizen Endpoints

| Method | Path                               | Description           | Auth     |
|--------|------------------------------------|-----------------------|----------|
| POST   | `/api/v1/requests`                 | Create a new request  | CITIZEN  |
| GET    | `/api/v1/requests/my`              | List own requests     | CITIZEN  |
| PATCH  | `/api/v1/requests/{id}/cancel`     | Cancel a request      | CITIZEN  |

### Assignor Endpoints

| Method | Path                               | Description           | Auth     |
|--------|------------------------------------|-----------------------|----------|
| GET    | `/api/v1/requests`                 | List all requests     | ASSIGNOR |
| PATCH  | `/api/v1/requests/{id}/assign`     | Assign a collector    | ASSIGNOR |

### Collector Endpoints

| Method | Path                               | Description           | Auth      |
|--------|------------------------------------|-----------------------|-----------|
| PATCH  | `/api/v1/requests/{id}/accept`     | Accept assignment     | COLLECTOR |
| PATCH  | `/api/v1/requests/{id}/start`      | Mark IN_PROGRESS      | COLLECTOR |
| PATCH  | `/api/v1/requests/{id}/complete`   | Mark COMPLETED (evidence required) | COLLECTOR |

> **No `/reject` endpoint.** Collectors cannot reject tasks (BR15).
> Each state-change endpoint maps to exactly **one transition** in the state machine.
> PATCH is used because these are partial updates to the `status` field, not full replacements.

### Gamification Endpoints (Citizen)

| Method | Path                                          | Description                              | Auth    |
|--------|-----------------------------------------------|------------------------------------------|---------|
| GET    | `/api/v1/gamification/points`                 | View own point total                     | CITIZEN |
| GET    | `/api/v1/gamification/badges`                 | View own earned badges                   | CITIZEN |
| GET    | `/api/v1/gamification/badges/{id}/share`      | Get shareable badge data (Facebook etc.) | CITIZEN |

---

## 13. Data Integrity & Concurrency

### Optimistic Locking

The `WasteRequest` entity must include a `@Version` field.
When two concurrent operations target the same request, the slower one receives an `OptimisticLockException`, which the service layer translates to **HTTP 409 Conflict**.

### Atomicity

Each state transition is a **single database transaction**.
The transition validation (current state check) and the state update happen within the same transaction to prevent race conditions between the check and the write.

### Immutability of Terminal States

Service-layer logic must reject **any** modification attempt on a request whose status is `COMPLETED` or `CANCELLED`, **before** performing any other validation.

### Immutability of Citizen's Original Report

The `wasteType` and `quantity` fields set at creation are **never modified** by any operation. Collector adjustments are stored in separate `actual*` fields.

---

## 14. MVP Priority & Gamification

### MVP Feature Priority (Highest to Lowest)

1. **Citizen creates request** — with waste type classification (educational) and quantity in kg.
2. **Full lifecycle execution** — PENDING → ASSIGNED → ACCEPTED → IN_PROGRESS → COMPLETED (with evidence).
3. **Gamification** — Badges auto-awarded at milestones, social sharing. Points tracked but voucher exchange deferred.
4. **Collector adjustment with audit trail** — Separate record of actual vs. reported.
5. **Assignor workflow** — Assignment with MVP algorithm (BR11: fewest pickups today).
6. **Admin controls** — Suspension (BR31), intervention (BR32).

### Gamification Design Notes

- **Badge milestones:** 1st, 3rd, 5th, and 10th completed pickup. Auto-awarded like GitHub badges.
- Badges are **immutable**, **non-transferable**, and **never expire** (BR30).
- Social sharing generates shareable metadata (URL/Open Graph data for Facebook).
- Gamification logic fires when `IN_PROGRESS → COMPLETED` transition succeeds.
- **Points:** Tracked at COMPLETED (BR28). Voucher exchange is **NOT MVP** — deferred to post-MVP.

---

## 15. Open Decisions

The following items require a decision before or during implementation.

| #  | Question                                                                                               | Status / Recommendation                                                    |
|----|--------------------------------------------------------------------------------------------------------|----------------------------------------------------------------------------|
| 1  | ~~**REJECTED recovery**~~ | ✅ **Closed.** REJECTED state does not exist (BR15). |
| 2  | **Collector timeout:** If a Collector never responds after ASSIGNED, is there an automatic fallback?    | Out of scope for v1. Can be added as a scheduled job later.               |
| 3  | ~~**Duplicate detection definition**~~ | ✅ **Closed.** BR07: same citizen + address + date + non-terminal. BR01: max 1/day. Both enforced. |
| 4  | ~~**Valid waste types**~~ | ✅ **Closed.** `RECYCLABLE`, `NON_RECYCLABLE`, `OTHER` (BR05). |
| 5  | ~~**SLA timer start**~~ | ✅ **Closed.** Starts at assignment (BR25). Duration configurable, deferred. |
| 6  | ~~**SLA violation threshold**~~ | ✅ **Closed.** Not completed within SLA duration (BR26). >3 violations = suspend (BR27). |
| 7  | **Assignment tie-breaking:** If multiple Collectors have same pickup count today?                       | Default: earliest-registered collector. Confirm.                           |
| 8  | ~~**Point accumulation formula**~~ | ✅ **Closed.** Points at COMPLETED (BR28). Voucher exchange NOT MVP. |
| 9  | ~~**Badge milestones**~~ | ✅ **Closed.** 1st, 3rd, 5th, 10th. Auto-awarded like GitHub badges. |
| 10 | **Collector rating system:** Scale? Does it affect assignment priority?                                 | "Optional" — needs definition.                                             |
| 11 | **Admin intervention scope:** What exactly can Admin do in IN_PROGRESS?                                 | BR32 says "can intervene" — allowed actions TBD.                           |
| 12 | ~~**Image evidence requirements**~~ | ✅ **Closed.** Required at completion (BR19). Stored as BLOB in MySQL (Decision #11). |
| 13 | ~~**Missing business rule IDs**~~ | ✅ **Closed.** All 34 rules (BR01–BR34) now provided. |

