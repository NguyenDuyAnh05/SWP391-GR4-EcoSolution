BUSINESS RULES & VALIDATION — EcoSolution MVP

This document contains the project business rules, validation rules, state machine definitions, and related clarifications. Unknowns are explicitly marked.

1. Business Rules (BR)

BR-01: Report lifecycle states MUST follow PENDING → ACCEPTED → ASSIGNED → COLLECTED.
- Status values are declared in `ReportStatus` enum: PENDING, ACCEPTED, ASSIGNED, COLLECTED, CANCELLED.
- Implementation status: enum present; transition enforcement: NEEDS IMPLEMENTATION.

BR-02: Only ENTERPRISE users can claim PENDING reports (transition PENDING → ACCEPTED).
- Implementation status: NOT IMPLEMENTED. No endpoints or centralized role enforcement exist.

BR-03: Bosses can only assign collectors who work for them (collector.employerId == boss.id).
- Implementation status: NOT IMPLEMENTED. Data model lacks employer relationships and assignment fields.

BR-04: COLLECTED status requires a byte[] proof image and an actualQuantity value; without them the transition is invalid.
- Implementation status: NOT IMPLEMENTED. `WasteReport` currently lacks `proof_image` and `actualQuantity` fields.

BR-07: Prevent submission of the same address/ward within 5 minutes.
- Implementation status: PARTIAL — duplicate detection exists in `ReportServiceImpl` but the current implementation uses a 10-second window. Must be changed to 5 minutes.
- Duplicate-scope ambiguity: current code checks duplicates per createdBy (per-user). Spec ambiguity: need confirmation if duplication should be global (across all users) or per-user.

BR-CANCEL: Citizens may cancel their own report only while status == PENDING; cancelling sets status to CANCELLED and removes the report from enterprise boards.
- Implementation status: IMPLEMENTED in `ReportServiceImpl.cancelReport` (ownership and PENDING checks enforced).

BR-IMG: Uploaded images for citizen submission MUST be <= 5MB.
- Implementation status: IMPLEMENTED in `ReportMapperImpl` (throws IllegalArgumentException on violation).

BR-POINTS: Gamification: Points awarded to a citizen = actualQuantity * 10 when a report reaches COLLECTED.
- Implementation status: NOT IMPLEMENTED. Dashboard currently sums collected `quantity`, no multiplier or points storage exists.

BR-OWNERSHIP: Only the creator of a report can cancel it.
- Implementation status: IMPLEMENTED (owner equality check performed in service).

2. Validation Rules (Input & Business)

Input validation (server-side):
- `ReportMapperImpl` validates uploaded image size <= 5MB and rejects with IllegalArgumentException.
- `ReportRequest` record types ensure typed inputs (address String, wardId Long, quantity Double, image MultipartFile).
- Ward mapping: mapper calls `WardRepository.findById(wardId)` and silently leaves ward unset if not found (this behavior is ambiguous and should be changed to reject invalid wardId).

Business validation:
- Duplicate detection normalizes addresses (String.trim().toLowerCase()) and queries `ReportRepository.findRecentByCreatedByAndAddressAndWard` with a cutoff timestamp.
- Cancellation enforces ownership and PENDING-only state.

Security constraints on inputs:
- No server-side MIME type validation for uploaded images.
- No virus scanning of uploaded files.
- Images are stored as binary (MEDIUMBLOB) in the DB without encryption.

3. State Machine — ReportStatus

States:
- PENDING (initial on create)
- ACCEPTED
- ASSIGNED
- COLLECTED
- CANCELLED (terminal for cancelled reports)

Required transitions and constraints:
- PENDING → ACCEPTED: ENTERPRISE role required (claim).
- ACCEPTED → ASSIGNED: BOSS assigns collector; assigned collector must have employerId == boss.id.
- ASSIGNED → COLLECTED: Collector must provide `proof_image` and `actualQuantity` for the transition.
- PENDING → CANCELLED: Creator may cancel while PENDING.

Implementation status: Only PENDING create and PENDING → CANCELLED are implemented; other transitions are NOT IMPLEMENTED and need endpoints, service logic, and role checks.

4. Edge Cases Related to Business Rules

- Race conditions: duplicate detection via read-then-write may be bypassed by concurrent submissions. Suggest adding DB-level safeguards or transactional unique hashes.
- Missing wardId or invalid wardId: current mapper behavior allows null ward; better to reject at validation stage.
- Image upload failures: controller must present clear error messages for IllegalArgumentException from the mapper.
- Transition conflicts: no optimistic locking on `WasteReport` currently; concurrent CLAIM/ASSIGN operations can conflict.

5. Clarification Questions (Business / Validation)

- Duplicate scope: Should BR-07 (5-minute duplicate prevention) be global across all users or per-user? Current implementation is per-user.
- Points awarding: When exactly are points awarded? Immediately upon transition to COLLECTED? Should they be persisted on `User` or computed on-demand?
- Collection proof: Should the collector proof be stored on the same `WasteReport` or in a separate `CollectionProof` entity?
- Ward mapping: On invalid `wardId` should we reject the submission (preferred) or allow null ward?

6. Recommended immediate changes

- Change duplicate window to 5 minutes (300 seconds) and confirm whether duplicate scope is global or per-user.
- Reject invalid wardId during validation (do not allow null ward in saved reports).
- Add server-side image MIME/type verification and consider virus-scanning integration.
- Implement transitions for ACCEPTED / ASSIGNED / COLLECTED with role checks and transactional protections.


End of BUSINESS_RULES.md

