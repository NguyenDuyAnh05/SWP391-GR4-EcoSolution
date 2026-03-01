# EcoSolution — Technical Design Document

> **Version:** 1.0
> **Date:** 2026-03-02
> **Status:** Pre-implementation — Approved business rules, ready for development.

---

## Table of Contents

1. [System Purpose](#1-system-purpose)
2. [Business Actors & Roles](#2-business-actors--roles)
3. [Domain Model](#3-domain-model)
4. [Core Entity: WasteRequest](#4-core-entity-wasterequest)
5. [State Machine](#5-state-machine)
6. [Permission Matrix](#6-permission-matrix)
7. [Business Invariants](#7-business-invariants)
8. [Edge Cases & Expected Behavior](#8-edge-cases--expected-behavior)
9. [Environment & Secrets Strategy](#9-environment--secrets-strategy)
10. [Package Structure](#10-package-structure)
11. [API Contract Overview](#11-api-contract-overview)
12. [Data Integrity & Concurrency](#12-data-integrity--concurrency)
13. [Open Decisions](#13-open-decisions)

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

---

## 2. Business Actors & Roles

| Role         | Responsibility                                                              | Analogy      |
|--------------|-----------------------------------------------------------------------------|--------------|
| **Citizen**  | Creates pickup requests. Tracks status. Cancels when allowed.               | Customer     |
| **Assignor** | Reviews pending requests. Assigns a collector. Monitors workflow. Does **not** execute. | Dispatcher   |
| **Collector**| Accepts/rejects assignments. Performs pickup. Reports progress and completion. | Field worker |

> **ADMIN** exists in the `UserRole` enum for system administration purposes but is **not** part of the waste-request workflow.

### Role Isolation Principle

No role may perform actions defined for another role.
Unauthorized operations must be **rejected at the service layer**, not just hidden in the UI.

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
- All three actors are rows in the **same `users` table**, differentiated by `UserRole`.

---

## 4. Core Entity: WasteRequest

| Attribute             | Type          | Constraints                                       |
|-----------------------|---------------|---------------------------------------------------|
| `id`                  | UUID          | PK, auto-generated, immutable                     |
| `citizenId`           | UUID          | FK → `users.id`, mandatory, immutable             |
| `wasteType`           | Enum (String) | Mandatory, must be a defined valid type            |
| `address`             | String        | Mandatory                                         |
| `latitude`            | Double        | Mandatory                                         |
| `longitude`           | Double        | Mandatory                                         |
| `preferredDate`       | LocalDate     | Mandatory, must not be in the past at creation     |
| `status`              | Enum (String) | Mandatory, default = `PENDING`                    |
| `assignedCollectorId` | UUID          | FK → `users.id`, nullable, set when ASSIGNED       |
| `createdAt`           | LocalDateTime | Auto-set on creation, immutable                   |
| `updatedAt`           | LocalDateTime | Auto-set on every state change                    |

### WasteRequest Status Enum

```
PENDING, ASSIGNED, ACCEPTED, IN_PROGRESS, COMPLETED, CANCELLED, REJECTED
```

---

## 5. State Machine

This is the **authoritative, non-negotiable** lifecycle of a WasteRequest.

### Transition Graph

```
PENDING ──────→ ASSIGNED ──────→ ACCEPTED ──────→ IN_PROGRESS ──────→ COMPLETED ✓
  │                │  │
  │                │  └──→ REJECTED ✓
  │                │
  ↓                ↓
CANCELLED ✓    CANCELLED ✓
```

### Transition Table

| From          | To            | Triggered By | Action Description                    |
|---------------|---------------|--------------|---------------------------------------|
| `PENDING`     | `ASSIGNED`    | Assignor     | Assign a collector to the request     |
| `PENDING`     | `CANCELLED`   | Citizen      | Citizen withdraws before assignment   |
| `ASSIGNED`    | `ACCEPTED`    | Collector    | Collector accepts the assignment      |
| `ASSIGNED`    | `REJECTED`    | Collector    | Collector declines the assignment     |
| `ASSIGNED`    | `CANCELLED`   | Citizen      | Citizen withdraws after assignment    |
| `ACCEPTED`    | `IN_PROGRESS` | Collector    | Collector starts the pickup work      |
| `IN_PROGRESS` | `COMPLETED`   | Collector    | Collector finishes the pickup         |

### Terminal States

| State       | Meaning                                   |
|-------------|-------------------------------------------|
| `COMPLETED` | Work done. Request is immutable.          |
| `CANCELLED` | Citizen withdrew. Request is immutable.   |
| `REJECTED`  | Collector declined. Request is immutable. |

> **No transitions exist outside this table.**
> Any attempt to perform an undefined transition must be **rejected with an error**.

---

## 6. Permission Matrix

### Citizen Permissions

| Action            | Precondition                           |
|-------------------|----------------------------------------|
| Create request    | Valid input, `preferredDate` not past  |
| View own requests | `citizenId` matches authenticated user |
| Cancel request    | Status ∈ {`PENDING`, `ASSIGNED`}       |

### Assignor Permissions

| Action           | Precondition                                          |
|------------------|-------------------------------------------------------|
| Assign collector | Status = `PENDING`, collector exists and is `ACTIVE`  |
| View all requests| No precondition                                       |

### Collector Permissions

| Action            | Precondition                                                           |
|-------------------|------------------------------------------------------------------------|
| Accept assignment | Status = `ASSIGNED`, `assignedCollectorId` matches authenticated user  |
| Reject assignment | Status = `ASSIGNED`, `assignedCollectorId` matches authenticated user  |
| Mark IN_PROGRESS  | Status = `ACCEPTED`, `assignedCollectorId` matches authenticated user  |
| Mark COMPLETED    | Status = `IN_PROGRESS`, `assignedCollectorId` matches authenticated user |

> **Ownership check:** A Collector can only act on requests assigned to **them**.
> **Any unauthorized action → HTTP 403.**

---

## 7. Business Invariants

These rules **must never be violated**, regardless of implementation approach.

| #  | Invariant                                                                  |
|----|----------------------------------------------------------------------------|
| 1  | A WasteRequest always has **exactly one** valid status.                    |
| 2  | A `COMPLETED`, `CANCELLED`, or `REJECTED` request **cannot be modified**.  |
| 3  | A WasteRequest can have **at most one** assigned collector.                |
| 4  | State transitions **must follow the defined graph** — no skipping.         |
| 5  | Unauthorized role actions **must be rejected** — no silent failures.       |
| 6  | `preferredDate` **must not be in the past** at creation time.              |
| 7  | `citizenId` is **immutable** after creation.                               |
| 8  | `assignedCollectorId` is set **only** during the PENDING → ASSIGNED transition. |

---

## 8. Edge Cases & Expected Behavior

| #  | Edge Case                                           | Expected System Behavior                                                    |
|----|-----------------------------------------------------|-----------------------------------------------------------------------------|
| 1  | Invalid waste type submitted                        | Reject with 400 — validation error.                                         |
| 2  | `preferredDate` is in the past                      | Reject with 400 — "Preferred date must not be in the past."                 |
| 3  | Assignor assigns a non-existing collector            | Reject with 404 — "Collector not found."                                    |
| 4  | Assignor assigns a user who is not a COLLECTOR role  | Reject with 400 — "Target user is not a collector."                         |
| 5  | Assignor assigns a BANNED collector                  | Reject with 400 — "Collector is not active."                                |
| 6  | Collector tries to complete without accepting first  | Reject with 409 — invalid state transition (IN_PROGRESS requires ACCEPTED). |
| 7  | Citizen tries to cancel after ACCEPTED               | Reject with 409 — cancellation only allowed in PENDING or ASSIGNED.         |
| 8  | Duplicate request (same citizen, address, date)      | Reject with 409 — "A request for this address and date already exists."     |
| 9  | Two Assignors assign the same PENDING request concurrently | One succeeds, the other gets 409 — enforced via optimistic locking.   |
| 10 | Collector acts on a request not assigned to them     | Reject with 403 — "You are not the assigned collector."                     |
| 11 | Any modification to a terminal-state request         | Reject with 409 — "Request is in a terminal state and cannot be modified."  |

---

## 9. Environment & Secrets Strategy

All sensitive configuration is externalized via environment variables.
**No secrets are ever committed to Git.**

### Secret Categories

| Secret                | Env Variable            | Used For                      |
|-----------------------|-------------------------|-------------------------------|
| DB connection URL     | `DB_URL`                | MySQL (Docker)                |
| DB username           | `DB_USER`               | MySQL                         |
| DB password           | `DB_PASS`               | MySQL                         |
| JWT signing key       | `JWT_SECRET`            | Token authentication          |
| Google OAuth client   | `GOOGLE_CLIENT_ID`      | Google login                  |
| Google OAuth secret   | `GOOGLE_CLIENT_SECRET`  | Google login                  |
| Cloudinary cloud name | `CLOUDINARY_CLOUD_NAME` | Image uploads                 |
| Cloudinary API key    | `CLOUDINARY_API_KEY`    | Image uploads                 |
| Cloudinary API secret | `CLOUDINARY_API_SECRET` | Image uploads                 |

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

## 10. Package Structure

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
└── common/                            # Shared cross-cutting concerns
    ├── domain/
    │   └── dto/
    │       └── response/              # ErrorResponseDto, etc.
    └── exception/                     # GlobalExceptionHandler
```

> The `statemachine/` sub-package isolates transition validation logic so it can be unit-tested independently from the service layer.

---

## 11. API Contract Overview

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
| PATCH  | `/api/v1/requests/{id}/reject`     | Reject assignment     | COLLECTOR |
| PATCH  | `/api/v1/requests/{id}/start`      | Mark IN_PROGRESS      | COLLECTOR |
| PATCH  | `/api/v1/requests/{id}/complete`   | Mark COMPLETED        | COLLECTOR |

> Each state-change endpoint maps to exactly **one transition** in the state machine.
> PATCH is used because these are partial updates to the `status` field, not full replacements.

---

## 12. Data Integrity & Concurrency

### Optimistic Locking

The `WasteRequest` entity must include a `@Version` field.
When two concurrent operations target the same request, the slower one receives an `OptimisticLockException`, which the service layer translates to **HTTP 409 Conflict**.

### Atomicity

Each state transition is a **single database transaction**.
The transition validation (current state check) and the state update happen within the same transaction to prevent race conditions between the check and the write.

### Immutability of Terminal States

Service-layer logic must reject **any** modification attempt on a request whose status is `COMPLETED`, `CANCELLED`, or `REJECTED`, **before** performing any other validation.

---

## 13. Open Decisions

The following items were identified during business analysis and require a decision before or during implementation.

| #  | Question                                                                                               | Recommendation                                                                                                         |
|----|--------------------------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------------------------------------|
| 1  | **REJECTED recovery:** When a Collector rejects, does the request stay dead, or can the Assignor reassign? | Current spec: REJECTED is terminal. If reassignment is needed later, add `REJECTED → PENDING` transition explicitly. |
| 2  | **Collector timeout:** If a Collector never responds after ASSIGNED, is there an automatic fallback?    | Out of scope for v1. Can be added as a scheduled job later (`ASSIGNED` for > X hours → auto-revert to `PENDING`).     |
| 3  | **Duplicate detection definition:** What exactly constitutes a duplicate request?                       | Proposed: same `citizenId` + same `address` + same `preferredDate` + status not in terminal state.                     |
| 4  | **Valid waste types:** What are the allowed values?                                                     | Must be defined as an enum. Values TBD by product owner.                                                               |

