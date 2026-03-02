# EcoSolution — Business Alignment Document

> **Purpose:** Establish shared understanding of the business domain between the human product owner and the AI development agent — before any implementation begins.
> **Version:** 0.1 — Draft for Review
> **Date:** 2026-03-02
> **Status:** PENDING RESOLUTION — contains contradictions that must be decided by the product owner.

---

## Table of Contents

1. [System Purpose](#1-system-purpose)
2. [Actors & Permission Boundaries](#2-actors--permission-boundaries)
3. [WasteRequest Lifecycle](#3-wasterequest-lifecycle)
4. [Business Rules Catalog](#4-business-rules-catalog)
5. [Contradictions Requiring Resolution](#5-contradictions-requiring-resolution)
6. [Scope Definition](#6-scope-definition)
7. [Open Decisions](#7-open-decisions)

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

This is **not** a generic task management tool. It is a **role-restricted state machine** built around a single core workflow.

---

## 2. Actors & Permission Boundaries

There are **four roles** in the system. Each role has a strict set of allowed actions. No role may perform actions belonging to another role.

### 2.1 Citizen (the Customer)

**What they can do:**

- Submit a new waste pickup request (with waste type, address, coordinates, preferred date).
- View their own requests and track status changes.
- Cancel their own request — **under limited conditions** (see [Contradiction #2](#contradiction-2-citizen-cancellation-after-assignment)).
- Earn points and badges for completed collections.
- Share earned badges to social media (e.g., Facebook).
- Optionally rate the Collector after completion.

**What they cannot do:**

- View other citizens' requests.
- Assign, accept, reject, start, or complete any request.
- Set or manipulate the request status directly.
- Assign a collector to their own request.
- Create more than 1 request per day (BR01).

---

### 2.2 Assignor (the Dispatcher)

**What they can do:**

- View all waste requests across all citizens.
- Assign a specific Collector to a PENDING request.
- View the request map.
- Monitor SLA compliance.

**What they cannot do:**

- Create, cancel, accept, reject, start, or complete any request.
- Perform the physical pickup.
- The Assignor's identity is **not recorded** on the WasteRequest. They are an operational facilitator, not a participant.

---

### 2.3 Collector (the Field Worker)

**What they can do:**

- Receive assigned tasks.
- Accept an assigned task — **or reject it** (see [Contradiction #1](#contradiction-1-collector-rejection)).
- Check-in and check-out with image evidence (no geotag required).
- Adjust waste type and quantity during pickup (BR09).
- Mark a request as in-progress, then as completed.

**What they cannot do:**

- Create or cancel requests.
- Assign themselves to a request.
- Act on a request that is not assigned to them.
- Skip steps in the lifecycle (e.g., cannot complete without first accepting and starting).

---

### 2.4 Admin (System Administrator)

**What they can do:**

- Full role-based access control management.
- Lock or suspend user accounts (Citizens for abuse — BR20, Collectors for SLA violations — BR15).
- Intervene in in-progress requests (BR12).

**What they are not:**

- Admin is **not** a participant in the waste-request workflow. They do not create, assign, accept, or complete requests under normal operations.
- Admin intervention is an **exceptional override**, not a standard workflow step.

---

### Role Isolation Principle

> If a Citizen tries to assign a collector — **rejected.**
> If a Collector tries to cancel a request — **rejected.**
> If an Assignor tries to complete a request — **rejected.**
>
> Unauthorized operations are **denied at the system level**, not merely hidden in the UI.

---

## 3. WasteRequest Lifecycle

The WasteRequest moves through a defined set of statuses. Each transition is triggered by a specific role and has preconditions.

### 3.1 Lifecycle Flow (Plain English)

1. A **Citizen** creates a request → it starts as **PENDING**.
2. An **Assignor** reviews pending requests and assigns a Collector → status becomes **ASSIGNED**.
3. The **Collector** accepts the assignment → status becomes **ACCEPTED**.
4. The **Collector** begins the pickup → status becomes **IN_PROGRESS**.
5. The **Collector** finishes the pickup → status becomes **COMPLETED** (terminal — done forever).

**Alternative paths:**

- At any point while PENDING or ASSIGNED, the **Citizen** may cancel → **CANCELLED** (terminal).
- After being assigned, the **Collector** may reject → **REJECTED** (terminal) — **if BR05 contradiction is resolved in favor of allowing rejection.**

### 3.2 Transition Table

| From          | To            | Who Does It | What Happens                          |
|---------------|---------------|-------------|---------------------------------------|
| PENDING       | ASSIGNED      | Assignor    | A collector is assigned to the request |
| PENDING       | CANCELLED     | Citizen     | Citizen withdraws before anyone is assigned |
| ASSIGNED      | ACCEPTED      | Collector   | Collector agrees to do the pickup     |
| ASSIGNED      | REJECTED      | Collector   | Collector declines ⚠️ **See Contradiction #1** |
| ASSIGNED      | CANCELLED     | Citizen     | Citizen withdraws after assignment ⚠️ **See Contradiction #2** |
| ACCEPTED      | IN_PROGRESS   | Collector   | Collector starts the physical work    |
| IN_PROGRESS   | COMPLETED     | Collector   | Pickup is done                        |

### 3.3 Terminal States

Once a request reaches one of these states, it is **frozen forever**. No further changes are allowed.

| Terminal State | Meaning                              |
|----------------|--------------------------------------|
| COMPLETED      | The pickup was successfully finished |
| CANCELLED      | The Citizen withdrew the request     |
| REJECTED       | The Collector declined the assignment ⚠️ |

### 3.4 The Golden Rule

> **If a transition is not in the table above, it is forbidden.**
>
> No skipping. No backdoors. No "just this once." The system rejects any attempt to perform an undefined transition.

---

## 4. Business Rules Catalog

These are the non-negotiable rules governing the system's behavior. They are grouped by domain area.

### Citizen Rules

| ID   | Rule                                                                 |
|------|----------------------------------------------------------------------|
| BR01 | A Citizen can create **at most 1 request per day**.                  |
| BR03 | A Citizen **cannot cancel after assignment**. ⚠️ **See Contradiction #2** |
| BR20 | Admin can suspend a Citizen for abuse.                               |

### Collector Rules

| ID   | Rule                                                                 |
|------|----------------------------------------------------------------------|
| BR05 | A Collector **cannot reject** a task. ⚠️ **See Contradiction #1**      |
| BR09 | A Collector can **adjust waste type and quantity** during pickup.     |
| BR15 | A Collector is **suspended** if SLA is violated **more than 3 times**. |

### Gamification Rules

| ID   | Rule                                                                 |
|------|----------------------------------------------------------------------|
| BR04 | Badge awarded based on **number of completed collections**.          |
| BR08 | Badge **never expires**.                                             |
| BR18 | Badge is **non-transferable**.                                       |

### Admin Rules

| ID   | Rule                                                                 |
|------|----------------------------------------------------------------------|
| BR12 | Admin can **intervene** in IN_PROGRESS requests.                     |
| BR20 | Admin can **suspend** Citizen for abuse.                             |

### Assignment Logic (MVP)

| Rule                                                                          |
|-------------------------------------------------------------------------------|
| Assignment criteria: Collector with the **fewest completed pickups today**.    |
| Tie-breaking logic: **Not yet defined** (see Open Decisions).                 |

### Missing Rule IDs

> The business rules provided skip IDs: BR02, BR06, BR07, BR10, BR11, BR13, BR14, BR16, BR17, BR19.
> **Are there additional rules not yet communicated, or are these IDs intentionally unused?**
> This must be clarified to ensure no rules are accidentally omitted.

---

## 5. Contradictions Requiring Resolution

These are direct conflicts between different parts of the business specification. **Each must be resolved by the product owner before implementation can proceed on the affected area.**

---

### Contradiction #1: Collector Rejection

**The conflict:**

- The **State Machine** (Section 5 of TECHNICAL_DESIGN.md) explicitly includes the transition `ASSIGNED → REJECTED` triggered by a Collector, and the TECHNICAL_DESIGN.md labels this state machine as *"authoritative, non-negotiable."*
- The **Updated Business Rules** state **BR05: "Collector cannot reject task."**

**These two statements are mutually exclusive.**

**Option A — Allow rejection:** Keep `ASSIGNED → REJECTED` in the state machine. Remove or redefine BR05. The REJECTED terminal state stays.

**Option B — Forbid rejection:** Remove `ASSIGNED → REJECTED` from the state machine. Remove the REJECTED terminal state entirely. BR05 is authoritative. If a Collector doesn't want a task, they must escalate outside the system.

**Impact:** This affects the state machine, the terminal states list, the Collector permission set, and potentially the Assignor's workflow (what happens to a request a Collector doesn't want?).

> ⏳ **BLOCKED — Awaiting decision.**

---

### Contradiction #2: Citizen Cancellation After Assignment

**The conflict:**

- The **State Machine** includes `ASSIGNED → CANCELLED` triggered by the Citizen.
- The **TECHNICAL_DESIGN.md Permission Matrix** (Section 6) explicitly allows Citizen to cancel when status ∈ {PENDING, ASSIGNED}.
- The **Updated Business Rules** state **BR03: "Citizen cannot cancel after assignment."**

**The state machine and the permission matrix agree with each other — but they both contradict BR03.**

**Option A — Allow cancellation after assignment:** Keep `ASSIGNED → CANCELLED`. Redefine or remove BR03. Citizen can cancel in both PENDING and ASSIGNED.

**Option B — Forbid cancellation after assignment:** Remove `ASSIGNED → CANCELLED` from the state machine. Update the permission matrix to allow cancel only when status = PENDING. BR03 is authoritative. If a Citizen wants to cancel after assignment, they must contact support.

**Impact:** This affects the state machine, the Citizen permission set, and the user experience (citizen loses control once a collector is assigned).

> ⏳ **BLOCKED — Awaiting decision.**

---

### Contradiction #3: Waste Type Enumeration

**The conflict:**

- The **Updated Business Scope** defines waste types as: `Recyclable, Non-Recyclable, Other` (3 values).
- The **TECHNICAL_DESIGN.md** (Section 13, Open Decision #4) states waste type values are "TBD by product owner."
- The **existing code** has: `ORGANIC, PLASTIC, PAPER, GLASS, METAL, ELECTRONIC, HAZARDOUS, OTHER` (8 values — these are placeholder/demo values).

**Option A — Use 3-value enum:** `RECYCLABLE, NON_RECYCLABLE, OTHER`. Simple. Matches the updated scope.

**Option B — Use detailed enum:** Keep granular categories. Richer data, but contradicts the updated scope document.

**Impact:** This affects validation logic, the creation form, the Collector's ability to adjust waste type (BR09), and reporting/analytics.

> ⏳ **BLOCKED — Awaiting decision.**

---

## 6. Scope Definition

### 6.1 In-Scope Modules

**Module 1: Citizen Web Application**

- Create waste collection request (max 1 per day)
- Track request status in real-time
- Earn points for completed collections
- Earn badges (non-transferable, no expiration)
- Share badge to social media (e.g., Facebook)
- Optional: Rate the Collector after completion

**Module 2: Collector Application / UI**

- Receive assigned task notifications
- Cannot reject task (pending Contradiction #1 resolution)
- Check-in / Check-out with image evidence (no geotag required)
- Adjust waste type and quantity during pickup

**Module 3: Assignor Web Dashboard**

- Assign Collectors to pending requests
- View all requests on a map
- Monitor SLA compliance
- MVP assignment criteria: Collector with fewest completed pickups today

**Module 4: Admin Portal**

- Full role-based access control management
- Lock / suspend user accounts
- Intervene in in-progress requests

### 6.2 Explicitly Out-of-Scope

| Excluded Feature         | Reason                                |
|--------------------------|---------------------------------------|
| Payment gateway          | No financial transactions in system   |
| Payroll calculation      | Outside system boundary               |
| Cash waste trading       | Not part of this platform             |
| SMS / OTP verification   | Not required for v1                   |
| Government integration   | No external government APIs           |

---

## 7. Open Decisions

These items need answers before or during implementation. They are **not contradictions** — they are genuinely undefined areas.

| #  | Question                                                                                    | Current Status         |
|----|---------------------------------------------------------------------------------------------|------------------------|
| 1  | **REJECTED recovery:** If a Collector rejects (assuming rejection is allowed), can the Assignor reassign the request back to PENDING? Or is it dead? | TECHNICAL_DESIGN.md says REJECTED is terminal. Needs confirmation. |
| 2  | **Collector timeout:** If a Collector never responds after being assigned, is there an automatic fallback (e.g., revert to PENDING after X hours)? | Out of scope for v1 per TECHNICAL_DESIGN.md. Confirm. |
| 3  | **Duplicate detection:** What exactly makes a request a "duplicate"? | Proposed: same citizen + same address + same preferred date + request not in terminal state. Needs confirmation. |
| 4  | **SLA timer start:** When does the SLA clock begin? At ASSIGNED? At ACCEPTED? | Not defined. Needs decision. |
| 5  | **SLA violation threshold:** What constitutes an SLA violation? Time exceeding X hours? Who defines X? | Not defined. Needs decision. |
| 6  | **Assignment tie-breaking:** If multiple Collectors have the same number of completed pickups today, which one gets assigned? | Not defined. Options: random, earliest registered, closest to address, etc. |
| 7  | **Point accumulation formula:** How many points per completed collection? Is it flat or variable? | Not defined. Needs decision. |
| 8  | **Badge milestones:** At what completion counts are badges awarded? (e.g., 5, 10, 25, 50, 100?) | Not defined. Needs decision. |
| 9  | **Collector rating system:** If Citizen can "optionally rate Collector," what scale? What happens with ratings? | "Optional" — needs definition if it affects anything (e.g., assignment priority). |
| 10 | **Admin intervention scope:** What exactly can Admin do when intervening in an IN_PROGRESS request? Cancel it? Reassign it? | BR12 says "can intervene" but doesn't define the allowed actions. |
| 11 | **Image evidence requirements:** What is the minimum/maximum number of images? Are both check-in AND check-out images mandatory? | Not defined. |
| 12 | **Missing business rule IDs:** BR02, BR06, BR07, BR10, BR11, BR13, BR14, BR16, BR17, BR19 are not listed. Do additional rules exist under these IDs? | Must clarify — either provide them or confirm they don't exist. |

---

## Summary: What Must Be Resolved Before Implementation

### Blockers (Cannot proceed without a decision)

1. ⚠️ **Contradiction #1** — Can Collectors reject assignments? → Affects state machine, terminal states, Collector workflow.
2. ⚠️ **Contradiction #2** — Can Citizens cancel after assignment? → Affects state machine, Citizen permissions.
3. ⚠️ **Contradiction #3** — Which waste type enum? (3-value vs. detailed) → Affects data model, validation, UI.

### Non-Blockers (Can proceed with assumptions, but should be confirmed)

4. All items in [Section 7 — Open Decisions](#7-open-decisions).

---

> **Next step:** Product owner resolves the 3 contradictions. Once resolved, the AI agent can proceed with implementation in strict alignment with the confirmed business rules.

