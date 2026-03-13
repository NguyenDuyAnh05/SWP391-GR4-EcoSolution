SYSTEM ARCHITECTURE — EcoSolution MVP

This document provides a concise architecture overview for the EcoSolution application: component diagrams in text, runtime flow, technologies, deployment assumptions, and security overview.

1. High-level Architecture

- Application Type: Monolithic web application using server-side rendering (Thymeleaf) and Spring Boot backend.
- Components:
  - Presentation Layer: Thymeleaf templates with Bootstrap 5 for styling.
  - Controller Layer: Spring MVC controllers handling HTTP GET/POST routes.
  - Service Layer: Business logic in `ReportService` and other service classes.
  - Data Layer: Spring Data JPA repositories using MySQL as persistent store.
  - Mapper layer: `ReportMapperImpl` for DTO->Entity mapping and upload validation.

2. Technology Stack

- Language: Java 21
- Framework: Spring Boot 3.4.3
- Templating: Thymeleaf
- Styling: Bootstrap 5 (CDN)
- Database: MySQL 8.x (local)
- Build: Maven (mvnw)
- ORM: Spring Data JPA / Hibernate

3. Runtime Flow

- Client -> HTTP request -> Spring DispatcherServlet -> Controller
- Controller -> Service -> Mapper / Repository -> Database
- Service -> Repository persists entities -> Transaction completes
- Controller sends model to Thymeleaf template -> Browser renders

Typical request paths
- Citizen report form
  - GET `/citizen/report/new` -> `CitizenReportController.form()` -> load `wards` -> render `report-form.html`.
  - POST `/citizen/report` (multipart) -> `CitizenReportController.submit()` -> `ReportService.createReport()` -> `ReportMapperImpl` -> `ReportRepository.save()` -> redirect to history.

- Dashboard
  - GET `/citizen/dashboard` -> `CitizenReportController.dashboard()` -> `ReportService.getReportsForCurrentUser()` -> compute points -> render `dashboard.html`.

- History
  - GET `/citizen/reports` -> `CitizenReportController.history()` -> returns user reports -> render `history.html`.

4. Data Layer & Persistence

- JPA entities map to MySQL tables; `wards` and `waste_reports` are core tables.
- `spring.jpa.hibernate.ddl-auto=update` allows the app to evolve schema automatically in development.
- Images are stored in `waste_reports.image` as MEDIUMBLOB (in-DB storage).

5. Security Architecture (current)

- Authentication: None (simulated stub via `UserServiceImpl`).
- Authorization: Not centrally enforced; services perform ad-hoc permission checks like ownership for cancellation.
- Recommendation: Add Spring Security with role-based access control (method-level security via annotations and principal injection for current user).

6. Operational Considerations

- Running locally requires a MySQL instance accessible at `jdbc:mysql://localhost:3306/ecosolution`.
- Maven build & run steps:

```powershell
# Build the project (skip tests during rapid iteration)
.\mvnw.cmd "-Dmaven.test.skip=true" clean package

# Run the jar
java -jar .\target\ecosolution-0.0.1-SNAPSHOT.jar
```

- For UI testing:
  - Visit http://localhost:8080/login/citizen to view the dashboard as the stubbed citizen user.

7. Scalability & Storage

- Storing images as MEDIUMBLOB in MySQL may not scale; recommend using object storage (S3-compatible) for production and store references in DB.
- Consider pagination for history views and indexing frequently queried columns (created_by, ward_id, created_at).

8. Recommended Architectural Changes to meet new spec

- Add Spring Security and implement proper authentication & role-based authorization.
- Introduce persistence for users (`UserRepository`) and seed enterprise and collector accounts.
- Migrate BLOB storage to object storage for production deployments, or at least adopt streaming and clean-up strategies.
- Add transactional and optimistic locking for critical state transitions (claim, assign, complete) to prevent race conditions.
- Consider moving to a feature-package layout under `com.ecosolution.*` if the new spec mandates it.

9. Unknowns / NEEDS CONFIRMATION

- Should package namespace be changed from `org.swp391_group4_backend.ecosolution` to `com.ecosolution`? (recommended but not applied)
- Should images remain in DB or move to an external store? (production decision)
- Exact expected roles and permissions mapping for enterprise flows.


End of SYSTEM_ARCHITECTUR.md

