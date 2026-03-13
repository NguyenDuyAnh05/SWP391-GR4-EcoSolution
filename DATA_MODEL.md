DATA MODEL — EcoSolution MVP

This document summarizes the domain entities, their fields, relationships, and required schema changes to satisfy the new project specification. Unknowns are explicitly marked.

1. Core Entities (as implemented)

User
- Description: System actor identity.
- Fields (entity):
  - id: UUID (PK)
  - username: String
  - role: String
- Notes: `User` entity exists in code but there is no `UserRepository` currently and `UserServiceImpl` provides a seeded stub user. No password field or authentication data present.

Ward
- Description: Flat geographic unit.
- Fields:
  - id: Long (PK, IDENTITY)
  - name: String (unique, not null)
- Notes: `WardRepository` exists and `DataInitializer` seeds a large list of wards.

WasteReport
- Description: Represents citizen-submitted waste report (current).
- Fields:
  - id: UUID (PK)
  - wasteType: Enum (RECYCLABLE | NON_RECYCLABLE | OTHER)
  - address: String
  - quantity: Double (citizen-provided estimate)
  - image: byte[] (Lob; citizen photo)
  - status: Enum (ReportStatus: PENDING, ACCEPTED, ASSIGNED, COLLECTED, CANCELLED)
  - ward: Ward (ManyToOne -> wards.id)
  - createdBy: User (ManyToOne -> users.id)
  - createdAt: OffsetDateTime
- Notes: Missing fields required by spec: `actualQuantity`, `proofImage`, `assignedTo`, `assignedBy`.

2. Proposed / Required Schema Additions (to meet spec)

WasteReport additions
- actualQuantity: Double
  - Purpose: store measured quantity after collector finishes work.
  - Constraint: required for COLLECTED status.

- proofImage: byte[] (MEDIUMBLOB)
  - Purpose: collector's proof photo saved at collection time.
  - Constraint: required for COLLECTED status.

- assignedTo: UUID (FK -> users.id)
  - Purpose: reference the collector user assigned to the report.

- assignedBy: UUID (FK -> users.id)
  - Purpose: reference the boss who assigned the collector.

- assignedAt: OffsetDateTime
  - Purpose: timestamp when assignment occurred.

User additions (optional)
- points: Double / Integer
  - Purpose: store gamification points for user (optional; otherwise compute on demand).

3. Database Tables (current and proposed)

Table: users (current)
- id UUID (PK)
- username VARCHAR
- role VARCHAR

Table: wards (current)
- id BIGINT (PK) AUTO_INCREMENT
- name VARCHAR UNIQUE NOT NULL

Table: waste_reports (current)
- id UUID (PK)
- waste_type VARCHAR
- address VARCHAR
- quantity DOUBLE
- image MEDIUMBLOB
- status VARCHAR
- ward_id BIGINT (FK -> wards.id)
- created_by UUID (FK -> users.id)
- created_at TIMESTAMP

Table: waste_reports (proposed additions)
- actual_quantity DOUBLE
- proof_image MEDIUMBLOB
- assigned_to UUID (FK -> users.id)
- assigned_by UUID (FK -> users.id)
- assigned_at TIMESTAMP

4. Data Access (repositories)

Existing repositories
- `WardRepository` extends JpaRepository<Ward, Long>
- `ReportRepository` extends JpaRepository<WasteReport, UUID> and includes custom JPQL queries:
  - `findByCreatedByIdAndAddressAndWardId`
  - `findRecentByCreatedByAndAddressAndWard`
  - `findAllByCreatedByIdOrderByCreatedAtDesc`

Proposed repository changes
- Add queries to find by assignedTo, find PENDING reports, find reports for enterprises, and to support duplicate deduplication queries using normalized address indexes.

5. DTOs & Records

ReportRequest (existing record)
- address: String
- wardId: Long
- quantity: Double
- image: MultipartFile

Proposed additional DTOs
- CollectionCompleteRequest
  - reportId: UUID
  - actualQuantity: Double
  - proofImage: MultipartFile

- AssignCollectorRequest
  - reportId: UUID
  - collectorId: UUID

6. Migration & Backward Compatibility

- Adding new columns to `waste_reports` is compatible with `spring.jpa.hibernate.ddl-auto=update`, but consider manual migrations for production.
- Existing reports without `actualQuantity` or `proofImage` must be tolerated; queries for COLLECTED must validate presence.

7. Unknowns / NEEDS CONFIRMATION

- Should `User` include `employerId` or should `Enterprise` be a separate entity? (affects BR-03 implementation)
- Should `points` be persisted on `User` or computed from `WasteReport` at runtime?
- Should proof images be stored in DB or in an external object storage with only references in DB?

8. Next steps (implementation tasks)

- Update `WasteReport` entity to include new fields: `actualQuantity`, `proofImage`, `assignedTo`, `assignedBy`, `assignedAt`.
- Add a `UserRepository` and persist sample users (enterprise boss, collectors, citizen sample) if using persisted users.
- Update `ReportMapperImpl` to map `CollectionCompleteRequest` to `WasteReport` fields and add server-side validation for proof image size/type.
- Update `ReportRepository` with queries supporting assignment/collection workflows.

End of DATA_MODEL.md

