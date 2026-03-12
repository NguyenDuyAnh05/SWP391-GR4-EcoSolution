# Assignment Feature — Commit Plan

This file outlines the recommended commit sequence for implementing the assignment feature (assignedCollector, optimistic locking, audit, service, UI, tests, docs) in EcoSolution.

---

## Commit 1: WasteReport Entity Enhancements
- Add `assignedCollector` field as nullable @ManyToOne to WasteReport
- Add `@Version private Long version;` for optimistic locking
- Update DB schema via migration if needed

## Commit 2: ReportAssignmentAudit Entity & Repository
- Create `ReportAssignmentAudit` entity with fields: id, reportId, assignorId, assignedCollectorId, timestamp, notes
- Create `ReportAssignmentAuditRepository`

## Commit 3: Service Layer — assignCollector Logic
- Implement `assignCollector(UUID reportId, UUID assignorId, UUID collectorId)` in ReportServiceImpl
- Add all validations (assignor role, report status, collector employer, not already assigned, etc.)
- Use optimistic locking and insert audit entry

## Commit 4: Assignment Tests
- Add/expand tests for assignment logic and concurrency
- Test cases: eligible assignment, employer mismatch, concurrent assignment

## Commit 5: Controller & UI
- Add assign endpoint to EnterpriseController
- Add/modify assign modal in enterprise UI (assign-modal.html)
- Show assigned collector on report view

## Commit 6: Documentation Updates
- Update developer notes (NOTES.md), implementation log (DEVELOPMENT_LOG.md), current state (CURRENT_STATE.md), and specification (PROJECT_SPECIFICATION.md)
- Check off completed TODOs in TODO_IMPLEMENTATION.md

---

Each commit should be atomic, descriptive, and pass all tests.

