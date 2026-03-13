Task receipt

I will add a developer-focused user guide with step-by-step instructions to run the application locally and test each actor flow. The guide explains how to impersonate seeded users (no passwords are required — we use DEV_USER_ID for quick role switching), how to start the app, the pages to visit, expected behavior, and helpful SQL/PowerShell commands to seed an ENTERPRISE user if you want to test the "claim" flow.

Checklist
- [x] Prerequisites & environment
- [x] How to start the app (PowerShell + Maven wrapper)
- [x] How to impersonate seeded users (DEV_USER_ID) and list of seeded users
- [x] Endpoints & UI pages for each actor (Citizen, Boss, Collector, Enterprise)
- [x] Step-by-step test flows (submit → claim → assign → complete → points)
- [x] MySQL SQL snippets to inspect data and seed an ENTERPRISE user
- [x] Notes about constraints (image size, duplicate window)

User Guide — Running & Testing EcoSolution (Developer)

1) Prerequisites
- Java 21 installed and JAVA_HOME set.
- MySQL 8.x running locally and reachable at jdbc:mysql://localhost:3306/ecosolution.
- Maven wrapper is included in the repo (use .\mvnw.cmd on Windows PowerShell).
- Workspace root: D:\TERM_05\wip

2) Build & Run (PowerShell)
- Open PowerShell and run the following commands from the project root:

```powershell
Set-Location -Path 'D:\TERM_05\wip'
# (Optional) build the project
.\mvnw.cmd -DskipTests clean package
# Run the app (development)
.\mvnw.cmd spring-boot:run
```

- The application listens on http://localhost:8080 by default.
- On first run, `DataInitializer` will seed the wards list and sample users + sample reports when the corresponding tables are empty.

Root endpoint behavior
- Hitting the application root (`/` or `/index`) will show a small developer landing page with quick links to the main actor pages (citizen dashboard, report form, enterprise board, boss assignments, collector tasks). The landing page lives at `/` and is useful when you want to quickly open the app in a browser at http://localhost:8080 while developing.

Error pages
- The application includes simple user-friendly error pages under `src/main/resources/templates`:
  - `error.html` — generic error page used by Spring Boot for general errors.
  - `404.html` — page-not-found (HTTP 404).
  - `500.html` — internal-server-error (HTTP 500).

To test the 404 page, open a non-existent path in your browser, e.g. http://localhost:8080/this-does-not-exist

To see the generic error page or 500 page you can trigger an internal server error from a controller (for development testing) or inspect logs when an exception occurs — the templates will be shown automatically for those error responses.

3) How the app chooses the "current user" (Dev impersonation)
- For development/testing, the `UserServiceImpl` checks an environment variable `DEV_USER_ID` and will return the persisted user with that UUID if present.
- If `DEV_USER_ID` is not set (or lookup fails) the service returns the default stub user (citizen-stub seeded at UUID 00000000-0000-0000-0000-000000000001).
- There are intentionally no passwords required for this developer mode — this makes it fast to exercise flows without full authentication.

4) Seeded test users (created by DataInitializer)
- Citizen
  - username: citizen-stub
  - role: CITIZEN
  - id: 00000000-0000-0000-0000-000000000001

- Boss
  - username: boss-sample
  - role: BOSS
  - id: 00000000-0000-0000-0000-000000000010

- Collector 1
  - username: collector-1
  - role: COLLECTOR
  - id: 00000000-0000-0000-0000-000000000011
  - employerId: boss-sample (000...0010)

- Collector 2
  - username: collector-2
  - role: COLLECTOR
  - id: 00000000-0000-0000-0000-000000000012
  - employerId: boss-sample (000...0010)

Note: these seeded users do not have passwords — you impersonate them by setting DEV_USER_ID (see below).

5) Impersonation (set DEV_USER_ID in PowerShell)
- Open PowerShell and set the environment variable in the same session before running the app:

```powershell
# Impersonate the boss
$env:DEV_USER_ID = '00000000-0000-0000-0000-000000000010'
# Impersonate collector1
$env:DEV_USER_ID = '00000000-0000-0000-0000-000000000011'
# Impersonate citizen (or leave DEV_USER_ID unset to use default stub)
$env:DEV_USER_ID = '00000000-0000-0000-0000-000000000001'
```

- After setting `$env:DEV_USER_ID`, start the app with `.\mvnw.cmd spring-boot:run` in the same PowerShell session. The running app will use that dev user as the current user.

6) Pages & endpoints (UI)
- Simulated login (quick start):
  - GET /login/citizen -> redirect to /citizen/dashboard
- Citizen pages:
  - GET /citizen/dashboard — shows points and links
  - GET /citizen/report/new — report submission form (multipart)
  - POST /citizen/report — submit report (multipart)
  - GET /citizen/reports — history and cancel action
  - POST /citizen/report/{id}/cancel — cancel a PENDING report
- Enterprise / Boss / Collector pages:
  - Enterprise board: GET /enterprise/board — lists PENDING reports and allows claim (POST /enterprise/claim/{id})
  - Boss assignments: GET /boss/assignments — lists ACCEPTED reports; POST /boss/assign (params: reportId, collectorId)
  - Collector tasks: GET /collector/tasks — lists ASSIGNED reports; POST /collector/complete (params: reportId, actualQuantity, proofImage multipart)

7) Step-by-step test scenario (end-to-end)
This scenario shows how to exercise the full flow: submit report (citizen) → claim (enterprise) → assign (boss) → complete (collector) and verify points.

A. Start the app and seed DB
```powershell
Set-Location -Path 'D:\TERM_05\wip'
# (optional) set DEV user to boss before starting?
# $env:DEV_USER_ID = '00000000-0000-0000-0000-000000000010'
.\mvnw.cmd spring-boot:run
```
- On startup `DataInitializer` will seed wards and sample users & reports if DB tables are empty.

B. As Citizen: submit a new report
- In PowerShell (same session) set DEV user to citizen (or leave unset):
```powershell
$env:DEV_USER_ID = '00000000-0000-0000-0000-000000000001'
```
- Visit: http://localhost:8080/citizen/report/new
- Fill: Ward (choose), Address: "123 Test St", Quantity: 2.5, Photo: upload a small image <= 5MB
- Submit. Expected: redirect to /citizen/reports and new PENDING report visible.
- Note: duplicate submissions for same address+ward within 5 minutes are rejected (server error message flashed).

C. As Enterprise: claim the pending report
- The claim operation requires role "ENTERPRISE" in code. The DataInitializer by default created a user with role BOSS; there is no preseeded ENTERPRISE user. You have two options:
  1) Create an ENTERPRISE user in the DB (SQL snippet below), then set DEV_USER_ID to that user's UUID, restart the app and use /enterprise/board to claim; OR
  2) Temporarily change the claim-role check in `ReportServiceImpl.claimReport` to accept BOSS as well (quick dev change). Option 1 is recommended to keep code unchanged.

- Example SQL to insert an ENTERPRISE user (run in MySQL):
```sql
INSERT INTO users (id, username, role, points) VALUES
('00000000-0000-0000-0000-000000000020', 'enterprise-sample', 'ENTERPRISE', 0);
```
- Then in PowerShell set:
```powershell
$env:DEV_USER_ID = '00000000-0000-0000-0000-000000000020'
```
- Visit: http://localhost:8080/enterprise/board
- Click "Claim" on the pending report. Expected: report.status becomes ACCEPTED (no longer on enterprise board).

D. As Boss: assign a collector
- Set DEV_USER_ID to the boss id:
```powershell
$env:DEV_USER_ID = '00000000-0000-0000-0000-000000000010'
```
- Visit: http://localhost:8080/boss/assignments
- For an ACCEPTED report, enter one of the seeded collector IDs (see section 4) in the collectorId input and click Assign.
- Expected: report.status becomes ASSIGNED; assignedTo = collectorId; assignedBy = boss id.

E. As Collector: complete the collection and award points
- Set DEV_USER_ID to the collector that was assigned (e.g., collector-1):
```powershell
$env:DEV_USER_ID = '00000000-0000-0000-0000-000000000011'
```
- Visit: http://localhost:8080/collector/tasks
- For a report assigned to that collector, use the Complete form: enter actualQuantity (e.g., 3.2), upload a proof image (<=5MB), and submit.
- Expected: report.status changes to COLLECTED; proofImage and actualQuantity persisted; citizen's points increase by round(actualQuantity * 10) — you can verify by switching to the citizen user and viewing their dashboard.

F. Verify points (as citizen)
- Set DEV_USER_ID to citizen and visit /citizen/dashboard
- Points show the accumulated points (persisted on user.points). Seeded collected reports added by the initializer did not automatically award points; only completions processed via `completeCollection` add points. If you want seeded collected reports to already award points, I can update the initializer accordingly.

8) SQL snippets (MySQL) useful for verification and ad-hoc seeds
- Show users:
```sql
SELECT id, username, role, points, employerId FROM users;
```
- Show recent waste reports:
```sql
SELECT id, address, quantity, status, ward_id, created_by, assigned_to, assigned_by, actual_quantity
FROM waste_reports ORDER BY created_at DESC LIMIT 50;
```
- Insert an ENTERPRISE user (example):
```sql
INSERT INTO users (id, username, role, points) VALUES
('00000000-0000-0000-0000-000000000020', 'enterprise-sample', 'ENTERPRISE', 0);
```
- Set a collector's employer via SQL (example):
```sql
UPDATE users SET employerId = '00000000-0000-0000-0000-000000000010' WHERE id = '00000000-0000-0000-0000-000000000011';
```

9) Important constraints & notes
- Image size limit: 5MB maximum for citizen submission and collector proof images. Files larger than 5MB are rejected server-side and the form will show an error.
- Duplicate prevention: identical address + ward by the same user is prevented within a 5-minute window (BR-07). Duplicate scope is per-user by current implementation.
- Claim logic: claim currently requires role 'ENTERPRISE' (see note in section 7C). If you want bosses to claim as well, I can relax that check.
- Points awarding: points are persisted in `users.points` as integer: points = round(actualQuantity * 10). If you want a different rounding rule, tell me.

10) Troubleshooting
- If you see "Communications link failure" on startup, ensure MySQL is running and the URL in `src/main/resources/application.properties` is correct.
- If you don't see seeded users/reports, the initializer runs only when the corresponding tables are empty. You can delete data from tables to force re-seeding (or drop and recreate the `ecosolution` database) and restart the app.

11) Next improvements you may want
- Add an `ENTERPRISE` seeded user in the initializer (I provided SQL to create one). I can also modify the initializer to create this user automatically.
- Add a simple login UI and Spring Security for proper authentication.
- Award points for already-collected seeded reports during initialization (optional).

If you want, I will now:
- (A) modify the initializer to also award points for seeded COLLECTED reports and seed an ENTERPRISE user, or
- (B) modify claim logic to allow BOSS to claim (quick code change), or
- (C) create a small README/test script that automates the test scenario with cURL requests.

Choose A / B / C (or ask for a different next step) and I will apply it and run another build.

