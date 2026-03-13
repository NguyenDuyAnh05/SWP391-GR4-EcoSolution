Task: generate a safe sequence of commits (with exact git commands) for the current work in the repository and provide clear revert instructions for each commit.

BRIEF PLAN
- I will provide a set of small, logical commits (commands you can run in PowerShell) that stage only the intended files.
- For each commit I will list the files to stage, the exact git commands to run, and precise revert steps (both safe revert and destructive reset options).
- I will also include verification steps you should run before committing and a single one-shot script you can run if you trust the plan.

IMPORTANT
- Review the `git status` output before running any commit script. Some files in your working tree are deleted or untracked; the scripts below stage only the files mentioned explicitly. If you want to include other files, inspect with `git status` and add them manually.
- These commands are PowerShell-friendly; run them in a PowerShell session from the repository root (e.g. Set-Location -Path 'D:\TERM_05\wip').

CURRENT GIT STATUS (short form)
Run this to refresh:

    git status --porcelain=1 --branch

Suggested logical commits
(Each block shows: purpose, files to stage, commands to run, and how to revert.)

COMMIT 1 — "feat: add landing + error pages and controllers"
Purpose:
- Add a small landing page and user-friendly error pages and their controllers. This is purely web/UI + controller change.
Files to stage (explicit):
- src/main/java/org/swp391_group4_backend/ecosolution/core/controller/RootController.java
- src/main/java/org/swp391_group4_backend/ecosolution/core/controller/ErrorPageController.java
- src/main/resources/templates/landing.html
- src/main/resources/templates/error.html
- src/main/resources/templates/404.html
- src/main/resources/templates/500.html
- User_Guides.md (if you want to include the doc update in this commit)

Commands (PowerShell)

    Set-Location -Path 'D:\TERM_05\wip'
    git checkout mvc-dev-report-flow

    git add src/main/java/org/swp391_group4_backend/ecosolution/core/controller/RootController.java \
      src/main/java/org/swp391_group4_backend/ecosolution/core/controller/ErrorPageController.java \
      src/main/resources/templates/landing.html \
      src/main/resources/templates/error.html \
      src/main/resources/templates/404.html \
      src/main/resources/templates/500.html \
      User_Guides.md

    git commit -m "feat(ui): add landing page and friendly error pages; add Root/Error controllers"

Verify commit

    git show --name-only --pretty=format:%B HEAD

Safe revert (recommended if already pushed):

    git revert <commit-sha>

Destructive revert (local undo, before push):

    git reset --hard HEAD~1

Note: after a destructive reset, if you already pushed the commit to a remote, you will need to force-push to rewrite remote history:

    git push --force-with-lease origin mvc-dev-report-flow

Use force pushes only when you understand remote consequences.

-------------------------------------------------------------------------------
COMMIT 2 — "chore: extend data model (description) and request DTO (wasteType/description)"
Purpose:
- Add `description` to `WasteReport` and extend `ReportRequest` to include `wasteType` and `description`. Mapper updated accordingly.
Files to stage (explicit):
- src/main/java/org/swp391_group4_backend/ecosolution/reporting/domain/entity/WasteReport.java
- src/main/java/org/swp391_group4_backend/ecosolution/reporting/domain/request/ReportRequest.java
- src/main/java/org/swp391_group4_backend/ecosolution/reporting/mapper/impl/ReportMapperImpl.java

Commands

    git add src/main/java/org/swp391_group4_backend/ecosolution/reporting/domain/entity/WasteReport.java \
      src/main/java/org/swp391_group4_backend/ecosolution/reporting/domain/request/ReportRequest.java \
      src/main/java/org/swp391_group4_backend/ecosolution/reporting/mapper/impl/ReportMapperImpl.java

    git commit -m "chore(model): add description to WasteReport; extend ReportRequest with description and wasteType; map fields in ReportMapper"

Revert options

    git revert <commit-sha>

or locally

    git reset --hard HEAD~1

Notes:
- Adding the `description` column may require a DB migration in production. With Hibernate `ddl-auto=update` you will get a column added locally, but for production use create an explicit SQL migration.

-------------------------------------------------------------------------------
COMMIT 3 — "feat: extend citizen report form with wasteType + description"
Purpose:
- Update the UI form and controller defaults to allow users to submit wasteType and description.
Files to stage:
- src/main/resources/templates/reporting/citizen/report-form.html
- src/main/java/org/swp391_group4_backend/ecosolution/reporting/controller/CitizenReportController.java

Commands

    git add src/main/resources/templates/reporting/citizen/report-form.html \
      src/main/java/org/swp391_group4_backend/ecosolution/reporting/controller/CitizenReportController.java

    git commit -m "feat(ui): add wasteType and description fields to citizen report form and controller"

Revert

    git revert <commit-sha>

or locally

    git reset --hard HEAD~1

-------------------------------------------------------------------------------
COMMIT 4 — "refactor(service): enforce N-layer separation — controllers use ReportService only"
Purpose:
- Move repository reads behind the `ReportService` API and update controllers to call service methods and use RedirectAttributes for flash messages.
Files to stage (explicit list — these were changed during the refactor):
- src/main/java/org/swp391_group4_backend/ecosolution/reporting/service/ReportService.java
- src/main/java/org/swp391_group4_backend/ecosolution/reporting/service/impl/ReportServiceImpl.java
- src/main/java/org/swp391_group4_backend/ecosolution/reporting/controller/EnterpriseController.java
- src/main/java/org/swp391_group4_backend/ecosolution/reporting/controller/BossController.java
- src/main/java/org/swp391_group4_backend/ecosolution/reporting/controller/CollectorController.java

Commands

    git add src/main/java/org/swp391_group4_backend/ecosolution/reporting/service/ReportService.java \
      src/main/java/org/swp391_group4_backend/ecosolution/reporting/service/impl/ReportServiceImpl.java \
      src/main/java/org/swp391_group4_backend/ecosolution/reporting/controller/EnterpriseController.java \
      src/main/java/org/swp391_group4_backend/ecosolution/reporting/controller/BossController.java \
      src/main/java/org/swp391_group4_backend/ecosolution/reporting/controller/CollectorController.java

    git commit -m "refactor(service): move repository reads into ReportService and update controllers to use service layer; use RedirectAttributes for flash messages"

Revert

    git revert <commit-sha>

or locally

    git reset --hard HEAD~1

Notes:
- This commit touches behavior — run an application compile and basic smoke tests after committing:

    .\mvnw.cmd -DskipTests clean compile

-------------------------------------------------------------------------------
COMMIT 5 — "docs: add BUSINESS_RULES, DATA_MODEL and Implementation_Specification"
Purpose:
- Add or update project documentation files that were created/updated during design work.
Files to stage (explicit):
- BUSINESS_RULES.md
- DATA_MODEL.md
- Implementation_Specification.md

Commands

    git add BUSINESS_RULES.md DATA_MODEL.md Implementation_Specification.md
    git commit -m "docs(spec): add BUSINESS_RULES, DATA_MODEL and Implementation_Specification"

Revert

    git revert <commit-sha>

or locally

    git reset --hard HEAD~1

-------------------------------------------------------------------------------
OPTIONAL: COMMIT 6 — "chore: remaining changes / minor fixes"
Purpose:
- Add any other changed files you intentionally want to commit (templates, small controllers, README changes). Be conservative: inspect with `git status` and `git diff` first.
Files to stage: pick only files you intend to commit (do not use `git add .` to avoid accidentally staging deletions).

Recommended workflow to add remaining files interactively

    # review everything changed
    git status --porcelain=1 --branch
    git diff --name-only

    # interactively add changes (PowerShell Git doesn't support 'git add -p' easily, but you can use 'git add -p' from Git Bash or use a GUI). Example:
    git add -p src/main/java/...

    # commit staged
    git commit -m "chore: misc fixes"

Revert

    git revert <commit-sha>

or locally

    git reset --hard HEAD~1

-------------------------------------------------------------------------------
How to inspect commits and their SHAs (useful for revert)

    git log --oneline --graph --decorate --all -n 20
    git show --name-only <sha>

Safe revert pattern (preferred when commits have been pushed)
- To undo a commit that has been pushed to origin, use `git revert <sha>` which creates a new commit that undoes the change while preserving history.

Destructive reset pattern (local-only, careful)
- To drop the latest commit locally before pushing:
    git reset --hard HEAD~1
- To roll back to a specific commit (dangerous, rewrites history):
    git reset --hard <sha>
  then push with force if you must update the remote:
    git push --force-with-lease origin mvc-dev-report-flow

Create a one-shot PowerShell script (if you trust the planned grouping)

    Set-Location -Path 'D:\TERM_05\wip'
    git checkout mvc-dev-report-flow

    # COMMIT 1
    git add src/main/java/org/swp391_group4_backend/ecosolution/core/controller/RootController.java `
      src/main/java/org/swp391_group4_backend/ecosolution/core/controller/ErrorPageController.java `
      src/main/resources/templates/landing.html `
      src/main/resources/templates/error.html `
      src/main/resources/templates/404.html `
      src/main/resources/templates/500.html `
      User_Guides.md
    git commit -m "feat(ui): add landing page and friendly error pages; add Root/Error controllers"

    # COMMIT 2
    git add src/main/java/org/swp391_group4_backend/ecosolution/reporting/domain/entity/WasteReport.java `
      src/main/java/org/swp391_group4_backend/ecosolution/reporting/domain/request/ReportRequest.java `
      src/main/java/org/swp391_group4_backend/ecosolution/reporting/mapper/impl/ReportMapperImpl.java
    git commit -m "chore(model): add description to WasteReport; extend ReportRequest with description and wasteType; map fields in ReportMapper"

    # COMMIT 3
    git add src/main/resources/templates/reporting/citizen/report-form.html `
      src/main/java/org/swp391_group4_backend/ecosolution/reporting/controller/CitizenReportController.java
    git commit -m "feat(ui): add wasteType and description fields to citizen report form and controller"

    # COMMIT 4
    git add src/main/java/org/swp391_group4_backend/ecosolution/reporting/service/ReportService.java `
      src/main/java/org/swp391_group4_backend/ecosolution/reporting/service/impl/ReportServiceImpl.java `
      src/main/java/org/swp391_group4_backend/ecosolution/reporting/controller/EnterpriseController.java `
      src/main/java/org/swp391_group4_backend/ecosolution/reporting/controller/BossController.java `
      src/main/java/org/swp391_group4_backend/ecosolution/reporting/controller/CollectorController.java
    git commit -m "refactor(service): move repository reads into ReportService and update controllers to use service layer; use RedirectAttributes for flash messages"

    # COMMIT 5
    git add BUSINESS_RULES.md DATA_MODEL.md Implementation_Specification.md
    git commit -m "docs(spec): add BUSINESS_RULES, DATA_MODEL and Implementation_Specification"

    # Review
    git log --oneline --graph -n 10

Final verification and build (recommended)

    .\mvnw.cmd -DskipTests clean compile

Notes about deletions seen in `git status`
- Your `git status` shows several deleted files (marked with D). Be intentional: if those deletions are unintended, DO NOT run `git add -A` or `git commit` that includes them. Instead restore them with:

    git restore --source=HEAD -- <path-to-deleted-file>

or to restore all deleted files listed by git status:

    git restore --source=HEAD -- $(git status --porcelain | awk '/^ D/ {print $2}')

(If you are on PowerShell and awk isn't available, inspect the list with `git status` and restore files manually.)

Last notes / safety checklist (run these first)
1. Inspect the working tree: `git status --porcelain=1 --branch`
2. Review diffs for files you plan to stage: `git diff <path>` or `git diff --staged` after staging.
3. Run `mvn -DskipTests clean compile` between commits that change code to ensure compilability.
4. Push commits in small batches: `git push origin mvc-dev-report-flow` after each stable commit.

If you want, I can now:
- (A) run the one-shot script and create the commits automatically, or
- (B) stage and commit the first commit only so you can review, or
- (C) update the commit groupings (file lists) to better match your intent.

Tell me which option you want me to run, or run the scripts yourself. If you want me to execute the commits I will perform them and then show the resulting `git log` and how to revert each commit (I will not force-push remote changes without confirmation).
