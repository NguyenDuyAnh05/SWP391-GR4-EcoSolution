# Report Lifecycle Flow — Source of Truth

**File:** agent-documentation/Report_Lifecyle_References.md
**Created:** 2026-03-12

---

## 1. Overview
Implements the core state machine for WasteReport lifecycle:
- Statuses: PENDING → ACCEPTED → ASSIGNED → COLLECTED
- Only valid transitions allowed (BR-01)
- Proof image required for COLLECTED (BR-04)

## 2. Business Rules
- **BR-01:** Reports must follow the defined status flow. Invalid transitions are rejected.
- **BR-04:** Proof image is mandatory to complete (COLLECTED) a report.

## 3. Acceptance Criteria
- All 9 tests in ReportServiceTest.java pass (see TODO 1.1)
- Entity, repository, and service code matches specification
- Only valid transitions allowed; invalid transitions throw
- Proof image required for COLLECTED

## 4. Required Files & Symbols
- reporting/domain/ReportStatus.java (enum)
- reporting/domain/entity/WasteReport.java (entity)
- reporting/repository/WasteReportRepository.java
- reporting/service/ReportService.java (interface)
- reporting/service/impl/ReportServiceImpl.java
- src/test/java/.../reporting/service/impl/ReportServiceTest.java

## 5. Test Cases (from TODO 1.1)
- shouldTransition_FromPending_ToAccepted
- shouldTransition_FromAccepted_ToAssigned
- shouldTransition_FromAssigned_ToCollected_WhenProofProvided
- shouldThrow_WhenSkipping_PendingToAssigned
- shouldThrow_WhenSkipping_PendingToCollected
- shouldThrow_WhenSkipping_AcceptedToCollected
- shouldThrow_WhenTransitioning_FromCollected
- shouldThrow_WhenTransitioning_Backwards
- shouldThrow_WhenCompleting_WithoutProofImage

## 6. Implementation Sequence
1. Write failing test skeletons (ReportServiceTest.java)
2. Implement ReportStatus.java (enum)
3. Implement WasteReport.java (entity)
4. Implement WasteReportRepository.java
5. Implement ReportService.java (interface)
6. Implement ReportServiceImpl.java (state machine logic)
7. Refactor, add summary comments
8. Run tests, ensure all pass
9. Commit: "Phase 1: Report lifecycle state machine — 9 tests green"

## 7. Traceability
- TODO_IMPLEMENTATION.md: PHASE 1, items 1.1–1.10
- PROJECT_SPECIFICATION.md: BR-01, BR-04

---

**This file is the single source of truth for the Report Lifecycle implementation. All changes, design decisions, and references should be tracked here.**


