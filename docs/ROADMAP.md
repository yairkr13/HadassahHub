# HadassahHub – Roadmap

This roadmap is organized into milestones. Each milestone ends with a **working**, demo-able outcome.

---

## Milestone 0 – Project Setup & Planning
**Goal:** Prepare the repository and dev workflow.

Deliverables:
- GitHub repository created (monorepo)
- Folder structure:
  - `backend/` (Spring Boot)
  - `frontend/` (React)
  - `infra/` (Docker / compose)
  - `docs/` (PRD, architecture, schema)
- Initial docs in `docs/`:
  - `PROJECT_REQUIREMENTS.md`
  - `ARCHITECTURE.md`
  - `DATABASE_SCHEMA.md`
  - `ROADMAP.md`

---

## Milestone 1 – Local Environment (DB + Hello World)
**Goal:** Everything runs locally with minimal steps.

Deliverables:
- PostgreSQL runs via `docker-compose`
- Spring Boot app runs and connects to DB
- Health endpoint: `GET /api/health`
- React app runs and can call the health endpoint (basic fetch)
- Basic CORS configured for local development

---

## Milestone 2 – Authentication (Users & Roles)
**Goal:** Implement login/register and secure endpoints.

Deliverables:
- Endpoints:
  - `POST /api/auth/register`
  - `POST /api/auth/login`
- JWT generation + validation
- Roles supported:
  - STUDENT
  - ADMIN
- Protected route example (requires JWT)

---

## Milestone 3 – Courses (Public Catalog)
**Goal:** Guests can browse courses.

Deliverables:
- DB entity + migration for `courses`
- Public endpoints:
  - `GET /api/courses` (filters: year, semester, type, search)
  - `GET /api/courses/{id}`
- Frontend pages:
  - Course list with filters
  - Course details page (name + description)

---

## Milestone 4 – Resources (Logged-in Access)
**Goal:** Students can access and submit course resources.

Deliverables:
- DB entity + migration for `resources`
- Endpoints:
  - `GET /api/courses/{id}/resources` (AUTH required)
  - `POST /api/courses/{id}/resources` (AUTH required; status=PENDING)
- Resource type support: Summary / Past Exam / Solution / Link / Other
- Frontend:
  - Resources list in course page (only when logged in)
  - Submit resource form

---

## Milestone 5 – Admin Approval Flow
**Goal:** Admin can approve/reject resources.

Deliverables:
- Admin endpoints:
  - `GET /api/admin/resources/pending`
  - `POST /api/admin/resources/{id}/approve`
  - `POST /api/admin/resources/{id}/reject`
- Admin UI page:
  - Pending list
  - Approve / reject actions

---

## Milestone 6 – Grade Statistics (Charts)
**Goal:** Display grade distributions/statistics for each course.

Deliverables:
- DB entity + migration for `grade_stats`
- Endpoint:
  - `GET /api/courses/{id}/stats` (AUTH required)
- Frontend:
  - Simple chart on course page
- Seed data for demo

---

## Milestone 7 – Course-Fit Questionnaire (Rule-Based MVP)
**Goal:** Provide course-fit recommendation based on user inputs.

Deliverables:
- Endpoint:
  - `POST /api/questionnaire/evaluate`
- Frontend:
  - Questionnaire form
  - Results page (score + explanation)
- Rule-based scoring (document the rules in code)

---

## Milestone 8 – Polish & Demo Readiness
**Goal:** Make it presentable for recruiters.

Deliverables:
- Strong README with:
  - Screenshots
  - How to run locally
  - Architecture summary
- Input validation + better error messages
- Seed demo users (admin + student)
- Optional: deploy (frontend + backend + managed DB)

---

## Notes
- Reputation/points can be introduced later (not required for MVP).
- File uploads can be added later after links-based MVP is stable.
