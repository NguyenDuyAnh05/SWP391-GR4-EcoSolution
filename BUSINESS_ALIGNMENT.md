# EcoSolution — Business Alignment Document

> **Purpose:** Establish shared understanding of the business domain between the human product owner and the AI development agent — the single source of truth for implementation.
> **Version:** 2.0 — Complete Business Rules (BR01–BR34)
> **Date:** 2026-03-03
> **Status:** ✅ RESOLVED — Ready for implementation.

---

## Table of Contents

1. [System Purpose](#1-system-purpose)
2. [Actors & Permission Boundaries](#2-actors--permission-boundaries)
3. [WasteRequest Lifecycle](#3-wasterequest-lifecycle)
4. [Quantity & Collector Adjustment Model](#4-quantity--collector-adjustment-model)
5. [Business Rules Catalog (BR01–BR34)](#5-business-rules-catalog-br01br34)
6. [Resolved Decisions](#6-resolved-decisions)
7. [Scope Definition](#7-scope-definition)
8. [MVP Priority](#8-mvp-priority)
9. [Open Decisions](#9-open-decisions)

---

## 1. System Purpose

EcoSolution is a **waste pickup coordination platform**. Its job is to replace informal, untracked waste collection arrangements with a **controlled, auditable digital workflow**.

The central business object is the **WasteRequest** — a citizen's request for trash collection. Every meaningful operation in the system is either:

- **Creating** a WasteRequest, or
- **Transitioning** a WasteRequest from one status to another.

The system exists to guarantee that:

- Only the right person can perform the right action at the right time.
- Every request moves through a predictable, well-defined lifecycle.
- No request can end up in an inconsistent or ambiguous state.
- All actions are traceable.

**Secondary purpose (MVP-critical):** Educate citizens on garbage classification. Garbage classification is a **new concept** in the target country. The system uses gamification (badges, social sharing) to incentivize participation and spread awareness.

This is **not** a generic task management tool. It is a **role-restricted state machine** built around a single core workflow.

---

## 2. Actors & Permission Boundaries

There are **four roles** in the system. Each role has a strict set of allowed actions. No role may perform actions belonging to another role.

### 2.1 Citizen (the Customer)

**What they can do:**

- Submit a new waste pickup request (with waste type, quantity in kg, address, coordinates, preferred date) (BR02).
- View their own requests and track status changes.
- Cancel their own request — **only when status is PENDING** (BR03).
- Earn badges for completed collections (BR29).
- Share earned badges to social media (e.g., Facebook).
- Optionally rate the Collector after completion.

**What they cannot do:**

- View other citizens' requests.
- Assign, accept, start, or complete any request.
- Set or manipulate the request status directly.
- Assign a collector to their own request.
- Create more than 1 request per day (BR01).
- Modify a request after it has been assigned (BR04).
- Overwrite or modify their original report after submission.
- Create a new request within 2 minutes of cancelling one (BR34).

---

### 2.2 Assignor (the Dispatcher)

**What they can do:**

- View all waste requests across all citizens.
- Assign a specific Collector to a PENDING request (BR09, BR10).
- View the request map.
- Monitor SLA compliance.

**What they cannot do:**

- Create, cancel, accept, start, or complete any request.
- Perform the physical pickup.
- The Assignor's identity is **not recorded** on the WasteRequest. They are an operational facilitator, not a participant.

---

### 2.3 Collector (the Field Worker)

**What they can do:**

- Receive assigned tasks.
- Accept an assigned task (ASSIGNED → ACCEPTED) (BR16).
- Start work on an accepted task (ACCEPTED → IN_PROGRESS) (BR17).
- Check-in and check-out with image evidence (no geotag required).
- Adjust waste type and quantity during pickup (BR18). Adjustments are **recorded separately** from the citizen's original report for audit purposes.
- Mark a request as completed — requires **evidence image upload** and confirmation of final waste details (BR19, BR20).

**What they cannot do:**

- Reject a task (BR15). If a Collector has an issue, they must escalate outside the system.
- Create or cancel requests.
- Assign themselves to a request.
- Act on a request that is not assigned to them.
- Skip steps in the lifecycle (BR24).
- Overwrite the citizen's original report. Adjustments are stored as a separate record.

---

### 2.4 Admin (System Administrator)

**What they can do:**

- Full role-based access control management.
- Suspend any user account — Citizens or Collectors (BR31).
- Intervene in in-progress requests (BR32).

**What they are not:**

- Admin is **not** a participant in the waste-request workflow. They do not create, assign, accept, or complete requests under normal operations.
- Admin intervention is an **exceptional override**, not a standard workflow step.

**Suspended users cannot** (BR33):

- Create requests
- Receive assignments
- Perform pickup actions

---

### Role Isolation Principle

> If a Citizen tries to assign a collector — **denied.**
> If a Collector tries to cancel a request — **denied.**
> If a Collector tries to reject a task — **denied.**
> If an Assignor tries to complete a request — **denied.**
>
> Unauthorized operations are **denied at the system level**, not merely hidden in the UI.

---

## 3. WasteRequest Lifecycle

The WasteRequest moves through a defined set of statuses. Each transition is triggered by a specific role and has preconditions.

### 3.1 Statuses

There are exactly **6 statuses**: `PENDING`, `ASSIGNED`, `ACCEPTED`, `IN_PROGRESS`, `COMPLETED`, `CANCELLED`.

> There is no `REJECTED` status. Collectors cannot reject tasks (BR15).

### 3.2 Lifecycle Flow (Plain English)

1. A **Citizen** creates a request → it starts as **PENDING**.
2. An **Assignor** reviews pending requests and assigns a Collector → status becomes **ASSIGNED** (BR14: cannot revert to PENDING).
3. The **Collector** accepts the assignment → status becomes **ACCEPTED** (BR16).
4. The **Collector** begins the pickup → status becomes **IN_PROGRESS** (BR17).
5. The **Collector** finishes the pickup with evidence → status becomes **COMPLETED** (BR19, BR20 — terminal, done forever).

**Alternative path:**

- While the request is still **PENDING**, the **Citizen** may cancel → **CANCELLED** (terminal).
- Once a Collector is assigned, the Citizen **cannot** cancel (BR03, BR21).
- An IN_PROGRESS request **cannot** be cancelled by Citizen (BR23).
- After cancelling, the Citizen must wait **2 minutes** before creating a new request (BR34).

### 3.3 Transition Table (Authoritative)

| From          | To            | Who Does It | What Happens                          |
|---------------|---------------|-------------|---------------------------------------|
| PENDING       | ASSIGNED      | Assignor    | A collector is assigned to the request |
| PENDING       | CANCELLED     | Citizen     | Citizen withdraws before anyone is assigned |
| ASSIGNED      | ACCEPTED      | Collector   | Collector agrees to do the pickup     |
| ACCEPTED      | IN_PROGRESS   | Collector   | Collector starts the physical work    |
| IN_PROGRESS   | COMPLETED     | Collector   | Pickup is done (evidence required — BR19) |

**5 transitions. 6 statuses. No exceptions.**

### 3.4 Terminal States

Once a request reaches one of these states, it is **frozen forever**. No further changes are allowed (BR22).

| Terminal State | Meaning                              |
|----------------|--------------------------------------|
| COMPLETED      | The pickup was successfully finished |
| CANCELLED      | The Citizen withdrew the request     |

### 3.5 The Golden Rule

> **If a transition is not in the table above, it is forbidden.** (BR24)
>
> No skipping. No backdoors. No "just this once." The system rejects any attempt to perform an undefined transition.

---

## 4. Quantity & Collector Adjustment Model

### 4.1 Citizen's Original Report

When a Citizen creates a WasteRequest, they provide (BR02):

- **wasteType** — one of: `RECYCLABLE`, `NON_RECYCLABLE`, `OTHER` (BR05)
- **quantity** — in **kilograms**, rounded to **two decimal places** (e.g., 12.50 kg)

These values are part of the WasteRequest entity and are **immutable after creation**. The citizen's original report is never overwritten.

### 4.2 Collector's Adjustment (BR18)

When a Collector physically picks up the waste, they may observe a different waste type or quantity than what the Citizen reported. The Collector can submit an adjustment.

**Key rules:**

- The adjustment is **recorded separately** from the citizen's original report.
- The citizen's `wasteType` and `quantity` are **never overwritten**.
- Both the original and the adjustment are preserved for **audit purposes**.
- **Default behavior:** If the Collector makes no adjustment, the adjustment values are **automatically copied from the citizen's original report**. This means every completed request always has both an original and an actual record — they are simply identical when no adjustment was made.

### 4.3 Data Semantics

| Field              | Set By    | When              | Mutable? |
|--------------------|-----------|-------------------|----------|
| `wasteType`        | Citizen   | At creation       | No — immutable |
| `quantity`         | Citizen   | At creation       | No — immutable |
| `actualWasteType`  | Collector (or auto-copy) | At completion | No — set once |
| `actualQuantity`   | Collector (or auto-copy) | At completion | No — set once |

> The `actual*` fields are `null` until the request reaches COMPLETED. At completion, they are either set by the Collector's explicit adjustment or auto-copied from the citizen's original values.

---

## 5. Business Rules Catalog (BR01–BR34)

These are the **non-negotiable** rules governing the system's behavior. **All 34 rules** are listed — no gaps.

### A. Citizen & Request Creation Rules

| ID   | Rule                                                                 |
|------|----------------------------------------------------------------------|
| BR01 | A Citizen may create a **maximum of one (1)** waste collection request per **calendar day**. This is a physical logistics constraint — collection happens once per day because the garbage central is far from citizens. |
| BR02 | A waste request **must include**: waste type, address, preferred collection date, estimated quantity. |
| BR03 | A Citizen may cancel a request **only when status = PENDING**. |
| BR04 | A Citizen **cannot modify** a request once it has been assigned to a Collector. |
| BR05 | A waste request must specify one of the following waste types only: **Recyclable, Non-Recyclable, Other**. |
| BR06 | Preferred collection date **must not be in the past** at the time of submission. |
| BR07 | A **duplicate request** (same Citizen + same address + same date + non-terminal status) is **not allowed**. |
| BR08 | A Citizen must have an **ACTIVE** account to create a request. |

### B. Assignment Rules

| ID   | Rule                                                                 |
|------|----------------------------------------------------------------------|
| BR09 | Only an **Assignor** may assign a Collector to a PENDING request. |
| BR10 | A request can only be assigned if its status is **PENDING**. |
| BR11 | Assignment selection rule (MVP): The Collector with the **lowest number of completed pickups** for the current day must be selected. |
| BR12 | A Collector must have **ACTIVE** status to be eligible for assignment. |
| BR13 | A request can be assigned to **only one Collector**. |
| BR14 | Once assigned, the request status becomes ASSIGNED and **cannot revert to PENDING** automatically. |

### C. Collector Execution Rules

| ID   | Rule                                                                 |
|------|----------------------------------------------------------------------|
| BR15 | A Collector **cannot reject** an assigned request. |
| BR16 | A Collector may only accept work when status = **ASSIGNED**. |
| BR17 | Starting work changes status to **IN_PROGRESS**. |
| BR18 | A Collector may **adjust waste type and waste quantity** during pickup. Adjustments are recorded separately; the citizen's original is never overwritten. If no adjustment is made, actual values auto-copy from original. |
| BR19 | Completion of pickup requires: **evidence image upload** and **confirmation of final waste details**. Evidence images are stored directly in the database for MVP. |
| BR20 | Marking a request as completed changes status to **COMPLETED**. |

### D. Cancellation & State Integrity Rules

| ID   | Rule                                                                 |
|------|----------------------------------------------------------------------|
| BR21 | A request **cannot be cancelled** after it has been assigned. |
| BR22 | A **COMPLETED** request **cannot be modified**. |
| BR23 | An **IN_PROGRESS** request cannot be cancelled by Citizen. |
| BR24 | State transitions must follow the defined lifecycle **strictly**; skipping states is **not allowed**. |

### E. SLA & Performance Rules

| ID   | Rule                                                                 |
|------|----------------------------------------------------------------------|
| BR25 | SLA measurement begins at the **moment of assignment**. |
| BR26 | An SLA violation is recorded if a request is **not completed within the defined SLA duration**. |
| BR27 | If a Collector accumulates **more than three (3) SLA violations** within a defined evaluation period, the account must be **suspended**. |

> **Note:** The specific SLA duration (in hours) is not yet defined. It is deferred — not blocking MVP implementation.

### F. Gamification & Rewards Rules

| ID   | Rule                                                                 |
|------|----------------------------------------------------------------------|
| BR28 | Reward points are granted only when a request reaches **COMPLETED** status. |
| BR29 | Badges are awarded based on accumulated number of completed pickups. |
| BR30 | Badges: **do not expire**, **cannot be transferred**, are **permanently** associated with the Citizen account. |

> **Badge milestones:** 1st, 3rd, 5th, and 10th completed pickup. Badges are auto-awarded (like GitHub badges).
> **Points-for-vouchers:** Points are tracked at COMPLETED, but **voucher exchange is NOT MVP**. Deferred to post-MVP.

### G. Administration & Governance Rules

| ID   | Rule                                                                 |
|------|----------------------------------------------------------------------|
| BR31 | Admin may **suspend any user account** (Citizen or Collector). |
| BR32 | Admin may **intervene** in an IN_PROGRESS request. |
| BR33 | Suspended users **cannot**: create requests, receive assignments, perform pickup actions. |

### H. Additional Constraints

| ID   | Rule                                                                 |
|------|----------------------------------------------------------------------|
| BR34 | After a Citizen **cancels** a request, they must wait **2 minutes** before creating a new request (cooldown to prevent abuse). |

> BR34 was added to prevent rapid cancel-and-recreate abuse. The 2-minute cooldown is enforced at creation time by checking the timestamp of the citizen's most recent CANCELLED request.

---

## 6. Resolved Decisions

These were previously contradictions or open questions. All have been resolved by the product owner.

---

### Decision #1: Collector Rejection → **NOT ALLOWED**

**Resolved:** Forbid rejection. BR15 is authoritative.

- `ASSIGNED → REJECTED` transition is **removed** from the state machine.
- `REJECTED` status is **removed** entirely from the system.
- If a Collector has an issue with an assignment, they must escalate outside the system.
- Terminal states are: `COMPLETED` and `CANCELLED` only.

**Decided:** 2026-03-02

---

### Decision #2: Citizen Cancellation After Assignment → **NOT ALLOWED**

**Resolved:** Forbid cancellation after assignment. BR03/BR21 are authoritative.

- `ASSIGNED → CANCELLED` transition is **removed** from the state machine.
- Citizen can only cancel when status = `PENDING`.
- Once a Collector is assigned, the Citizen loses the ability to cancel.

**Decided:** 2026-03-02

---

### Decision #3: Waste Type Enumeration → **3-VALUE ENUM**

**Resolved:** Use 3-value enum matching BR05.

- Waste types: `RECYCLABLE`, `NON_RECYCLABLE`, `OTHER`
- This aligns with the educational goal: teach citizens the basic classification system.

**Decided:** 2026-03-02

---

### Decision #4: Quantity Field → **INCLUDED IN CITIZEN REQUEST**

**Resolved:** Quantity is part of the Citizen's submission (BR02).

- Unit: **kilograms (kg)**, rounded to **two decimal places**.
- Collector may adjust during pickup — recorded **separately** (BR18, see §4).
- Citizen's original is **never overwritten**.
- Auto-copy if no adjustment.

**Decided:** 2026-03-03

---

### Decision #5: Authentication → **PLACEHOLDER FOR MVP**

**Resolved:** No Spring Security in MVP phase.

- Placeholder headers (`X-User-Id`). Role enforcement at the service layer.
- Spring Security deferred to post-MVP.

**Decided:** 2026-03-03

---

### Decision #6: BR01 + BR07 Coexistence → **BOTH ENFORCED**

**Resolved:** BR01 and BR07 are separate rules that coexist.

- **BR01:** Max 1 request per citizen per calendar day (physical logistics constraint).
- **BR07:** No duplicate request (same citizen + same address + same date + non-terminal status).
- Both are enforced independently.

**Decided:** 2026-03-03

---

### Decision #7: Badge Milestones → **1st, 3rd, 5th, 10th**

**Resolved:** Badges are auto-awarded at these milestones:

- **1st** completed pickup
- **3rd** completed pickup
- **5th** completed pickup
- **10th** completed pickup

Badges work like GitHub badges — automatically awarded, permanently visible, no manual claim needed.

**Decided:** 2026-03-03

---

### Decision #8: Points-for-Vouchers → **NOT MVP**

**Resolved:** Points are tracked (BR28: granted at COMPLETED), but **voucher exchange is deferred** to post-MVP. The points system exists for future use but has no user-facing redemption in MVP.

**Decided:** 2026-03-03

---

### Decision #9: State Machine Confirmation → **ACCEPTED STATE EXISTS**

**Resolved:** The lifecycle is ASSIGNED → ACCEPTED → IN_PROGRESS → COMPLETED.

- BR16 means Collector accepts when status = ASSIGNED.
- The ACCEPTED state is a distinct step between assignment and physical work.
- 5 transitions, 6 statuses — confirmed.

**Decided:** 2026-03-03

---

### Decision #10: SLA Duration → **DEFERRED**

**Resolved:** SLA tracking infrastructure is built (BR25: starts at assignment, BR26: violation = not completed in time, BR27: >3 violations → suspend), but the **specific SLA duration in hours is not yet defined**. Implementation can proceed — it will be configurable.

**Decided:** 2026-03-03

---

### Decision #11: Evidence Image Storage → **DATABASE (MVP)**

**Resolved:** For MVP, evidence images (BR19) are stored **directly in the database** (as byte array / BLOB).

- No external storage service (Cloudinary, S3, etc.) for MVP.
- This simplifies infrastructure — only MySQL (Docker) is needed.
- Can be migrated to external storage post-MVP if performance requires it.

**Decided:** 2026-03-03

---

### Decision #12: Cancel + Re-create Cooldown → **2 MINUTES**

**Resolved:** After a Citizen cancels a request, they must wait **2 minutes** before creating a new one (BR34).

- Prevents rapid cancel-and-recreate abuse.
- Enforced at creation time by checking timestamp of citizen's most recent CANCELLED request.
- If `now - lastCancelledAt < 2 minutes`, creation is rejected.

**Decided:** 2026-03-03

---

## 7. Scope Definition

### 7.1 In-Scope Modules

**Module 1: Citizen Web Application**

- Create waste collection request (waste type, quantity in kg, address, coordinates, preferred date; max 1 per day)
- Track request status in real-time
- Earn badges (auto-awarded at milestones: 1st, 3rd, 5th, 10th)
- Share badge to social media (e.g., Facebook)
- Optional: Rate the Collector after completion

**Module 2: Collector Application / UI**

- Receive assigned task
- Cannot reject task (BR15)
- Accept task, start work (IN_PROGRESS), mark completed with evidence image (BR19)
- Adjust waste type and quantity during pickup (recorded separately for audit)

**Module 3: Assignor Web Dashboard**

- Assign Collectors to pending requests
- View all requests on a map
- Monitor SLA compliance
- MVP assignment criteria: Collector with fewest completed pickups today (BR11)

**Module 4: Admin Portal**

- Full role-based access control management
- Suspend user accounts (BR31)
- Intervene in in-progress requests (BR32)

### 7.2 Explicitly Out-of-Scope

| Excluded Feature           | Reason                                    |
|----------------------------|-------------------------------------------|
| Payment gateway            | No financial transactions in system       |
| Payroll calculation        | Outside system boundary                   |
| Cash waste trading         | Not part of this platform                 |
| SMS / OTP verification     | Not required for v1                       |
| Government integration     | No external government APIs               |
| Spring Security            | Deferred to post-MVP (Decision #5)        |
| Points-for-voucher exchange| Deferred to post-MVP (Decision #8)        |
| External image storage     | Database storage for MVP (Decision #11)   |

---

## 8. MVP Priority

The MVP's core value proposition is **not** just waste collection — it is **citizen education and engagement through gamification**.

### Why Gamification is MVP-Critical

- Garbage classification is a **new concept** in the target country.
- Citizens need motivation to participate and learn the classification system.
- Badges and social media sharing create **organic awareness** and viral adoption.
- The system must make citizens **happy to use it first** — the operational workflow supports this goal.

### MVP Feature Priority (Highest to Lowest)

1. **Citizen creates request** — with waste type classification (educational) and quantity in kg.
2. **Full lifecycle execution** — PENDING → ASSIGNED → ACCEPTED → IN_PROGRESS → COMPLETED (with evidence).
3. **Gamification** — Badges auto-awarded at 1st/3rd/5th/10th completions, social sharing. Points tracked but voucher exchange deferred.
4. **Collector adjustment with audit trail** — Separate record of actual vs. reported.
5. **Assignor workflow** — Assignment with MVP algorithm (BR11).
6. **Admin controls** — Suspension (BR31), intervention (BR32).

---

## 9. Open Decisions

Most items are now resolved. Remaining open items are non-blocking.

| #  | Question                                                                                    | Current Status         |
|----|---------------------------------------------------------------------------------------------|------------------------|
| 1  | ~~**REJECTED recovery**~~ | ✅ **Closed.** REJECTED state does not exist (Decision #1). |
| 2  | **Collector timeout:** If a Collector never responds after being assigned, is there an automatic fallback? | Out of scope for v1. |
| 3  | ~~**Duplicate detection**~~ | ✅ **Closed.** BR07 defines duplicate. BR01 defines daily limit. Both enforced (Decision #6). |
| 4  | ~~**SLA timer start**~~ | ✅ **Closed.** SLA starts at assignment (BR25). Duration configurable, deferred (Decision #10). |
| 5  | ~~**SLA violation threshold**~~ | ✅ **Closed.** Violation = not completed within SLA duration (BR26). >3 = suspend (BR27). |
| 6  | **Assignment tie-breaking:** If multiple Collectors have the same pickup count today, which one? | Default: earliest-registered. Confirm or change. |
| 7  | ~~**Point accumulation formula**~~ | ✅ **Closed.** Points granted at COMPLETED (BR28). Voucher exchange not MVP (Decision #8). |
| 8  | ~~**Badge milestones**~~ | ✅ **Closed.** 1st, 3rd, 5th, 10th (Decision #7). Auto-awarded like GitHub badges. |
| 9  | **Collector rating system:** What scale? Does it affect assignment? | "Optional" — not blocking MVP. |
| 10 | **Admin intervention scope:** What exactly can Admin do in IN_PROGRESS? | BR32 says "can intervene" — specific actions TBD. |
| 11 | ~~**Image evidence requirements**~~ | ✅ **Closed.** Required at completion (BR19). Stored in database (Decision #11). |
| 12 | ~~**Missing business rule IDs**~~ | ✅ **Closed.** All 34 rules (BR01–BR34) now provided. No gaps. |

---

## Summary

All blockers are resolved. The system has:

- **6 statuses:** PENDING, ASSIGNED, ACCEPTED, IN_PROGRESS, COMPLETED, CANCELLED
- **5 transitions:** No rejection, no post-assignment cancellation
- **2 terminal states:** COMPLETED, CANCELLED
- **4 roles:** Citizen, Assignor, Collector, Admin
- **3 waste types:** RECYCLABLE, NON_RECYCLABLE, OTHER
- **34 business rules:** BR01–BR34, fully defined, no gaps
- **Dual quantity model:** Citizen's original (immutable) + Collector's actual (separate record, auto-copy if no adjustment)
- **Quantity unit:** Kilograms, rounded to 2 decimal places
- **Completion requires:** Evidence image upload + final waste details (BR19)
- **Evidence storage:** Database BLOB (MVP) — no external service
- **Cancel cooldown:** 2 minutes before re-creation (BR34)
- **Badges:** Auto-awarded at 1st, 3rd, 5th, 10th completions (like GitHub badges)
- **Points:** Tracked at COMPLETED, voucher exchange NOT MVP
- **SLA:** Starts at assignment, duration configurable (deferred)
- **No Spring Security** in MVP — placeholder headers with service-layer role checks

> **Next step:** Implementation can begin following the MVP priority order defined in §8.
