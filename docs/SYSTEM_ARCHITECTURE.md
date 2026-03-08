# HadassahHub - System Architecture

This document describes the current and planned architecture for HadassahHub. It is written to guide both humans and AI coding assistants and to keep implementation aligned with the intended design.

## 1. High-level goal

HadassahHub centralizes academic knowledge and course insights to help students plan their studies more effectively.

Core values:
- Course planning
- Transparency about courses
- Community knowledge

## 2. MVP scope (what to build first)

MVP focuses on:
- Course catalog and course pages
- Public browsing, search, filtering
- Auth (Guest/User/Admin)
- Resources as LINKS only (no file uploads yet)
- Reviews and ratings
- Basic admin moderation (approve/reject resources)
- Basic reputation points (optional early, can be minimal)

Not in MVP (future):
- File uploads and storage
- AI recommendations (beyond basic rules)
- Advanced analytics and notifications
- Calendar integrations

## 3. Tech stack

- Backend: Java, Spring Boot, Spring Security, Spring Data JPA, REST API
- Database: PostgreSQL
- Infra (dev): Docker + Docker Compose
- Frontend: React (separate app, later)
- Auth (planned): JWT for API-first mode (initially can use session login while prototyping)

## 4. Repository layout (suggested)

```
HadassahHub/
  backend/
    src/main/java/com/hadassahhub/backend/
      controller/
      service/
      repository/
      entity/
      dto/
      enums/
      security/
      config/
    src/main/resources/
      application.properties
  frontend/   (React app)
  infra/
    docker-compose.yml
  docs/
    PROJECT_REQUIREMENTS.md
    DATABASE_SCHEMA.md
    SYSTEM_ARCHITECTURE.md  <- this file
    DEV_PROGRESS.md
```

## 5. System context diagram

```
                 +----------------------+
                 |       Browser        |
                 |  Guest/User/Admin    |
                 +----------+-----------+
                            |
                            | HTTPS (REST)
                            v
+----------------------------------------------------------+
|                    Backend (Spring Boot)                 |
|  Controllers -> Services -> Repositories -> PostgreSQL   |
+----------------------+----------------------+------------+
                       |                      |
                       |                      |
                       v                      v
             +----------------+       +------------------+
             |   Auth/Security|       |  Business domain  |
             |  (roles, JWT)  |       | courses, etc.     |
             +----------------+       +------------------+
```

## 6. Backend layering

Guiding rule:
- Controllers handle HTTP concerns only (request/response, validation, mapping).
- Services contain business rules and transactions.
- Repositories are data access only.
- Entities are persistence models.
- DTOs are API contracts (avoid exposing entities directly long-term).

Layer diagram:

```
[Controller]
   |
   v
[Service]
   |
   v
[Repository] -> [PostgreSQL]
   ^
   |
[Entity]
```

## 7. Current domain model (now) and planned expansions

### 7.1 Current (already implemented in DB and backend prototype)
- Course
- CourseOffering (Course can appear in multiple years/semesters)

### 7.2 Planned (next milestones)
- User (Guest/User/Admin)
- Resource (LINK-based)
- Review/Rating (course reviews, resource ratings)
- Points (reputation transactions)
- Grade stats (community reported)

## 8. Key domain concepts

### 8.1 Course types and planning rule

Course types:
- CS_CORE (mandatory CS courses)
- CS_ELECTIVE (CS electives)
- GENERAL_ELECTIVE (college-wide electives)

Planning rule (product decision):
- Year-of-study (1/2/3) is critical mainly for CS_CORE courses.
- Electives do not require a strict year assignment (they are usually taken in year 2-3), but can still have offerings in the schedule for filtering.

### 8.2 Semesters

Semesters are displayed as:
- A
- B
- S (Summer)

A CourseOffering can exist for multiple semesters (same course offered in A and B).

## 9. Database model (logical)

Primary tables (current + planned):

```
courses
  id PK
  name
  category
  description
  credits (nz)
  course_type (CS_CORE/CS_ELECTIVE/GENERAL_ELECTIVE)
  created_at

course_offerings
  id PK
  course_id FK -> courses.id
  year (Y1/Y2/Y3) (optional for electives, required for core)
  semester (A/B/S)

users
  id PK
  email (unique)
  password_hash
  display_name
  role (USER/ADMIN)
  points_balance
  created_at

resources
  id PK
  course_id FK
  uploaded_by FK -> users.id
  title
  kind (SUMMARY/PAST_EXAM/SOLUTION/LINK)
  url
  academic_year_label (free text, example: "2024-2025")
  exam_term_label (free text, example: "Moed A")
  status (PENDING/APPROVED/REJECTED)
  approved_by FK -> users.id (nullable)
  approved_at
  created_at

course_reviews
  id PK
  course_id FK
  user_id FK
  overall_rating (1-5)
  difficulty_rating (1-5)
  workload_rating (1-5)
  comment
  created_at
  unique(course_id, user_id)

resource_ratings
  id PK
  resource_id FK
  user_id FK
  rating (1-5)
  created_at
  unique(resource_id, user_id)

points_transactions
  id PK
  user_id FK
  action
  delta
  reference_type
  reference_id
  created_at

grade_stats (community-reported)
  id PK
  course_id FK
  academic_year_label
  semester (A/B/S) (optional)
  avg_grade
  median_grade
  pass_rate
  sample_size
  created_at
```

## 10. API design (v1)

Base: `/api`

### 10.1 Public endpoints (Guest)
- GET `/api/health` -> "OK"
- GET `/api/courses`
  - filters: `q`, `type`, `category`
- GET `/api/courses/{id}`
- GET `/api/offerings`
  - filters: `year`, `semester`
- GET `/api/courses/{id}/reviews` (public read)
- GET `/api/courses/{id}/stats` (public read, community aggregated)

Guests can read:
- course info
- offerings (schedule)
- ratings/reviews
- general difficulty indicators (derived)

Guests cannot:
- access resources
- upload content

### 10.2 Authenticated endpoints (User)
- POST `/api/auth/register`
- POST `/api/auth/login`
- GET `/api/me`

Resources:
- GET `/api/courses/{id}/resources` (requires login, can be gated by policy later)
- POST `/api/courses/{id}/resources` (create PENDING resource)
- DELETE `/api/resources/{id}` (owner or admin)

Reviews:
- POST `/api/courses/{id}/reviews`
- PUT `/api/courses/{id}/reviews/{reviewId}` (owner)
- DELETE `/api/courses/{id}/reviews/{reviewId}` (owner or admin)

### 10.3 Admin endpoints (Admin)
- GET `/api/admin/resources/pending`
- POST `/api/admin/resources/{id}/approve`
- POST `/api/admin/resources/{id}/reject`
- CRUD courses (optional early)

## 11. API flows (sequence diagrams)

### 11.1 Browse courses (Guest)

```
Browser -> GET /api/courses?q=...
Backend Controller -> Service -> Repository -> DB
DB -> Repository -> Service -> Controller
Controller -> JSON -> Browser
```

### 11.2 View course page (Guest)

```
Browser -> GET /api/courses/{id}
Browser -> GET /api/offerings?courseId={id} (optional)
Browser -> GET /api/courses/{id}/reviews
Browser -> GET /api/courses/{id}/stats (optional)
```

### 11.3 Add resource (User)

```
User logs in -> receives session/JWT
Browser -> POST /api/courses/{id}/resources {title, kind, url, ...}
Controller validates -> Service creates Resource(status=PENDING)
Repository saves -> DB
Response -> created resource summary
Admin later approves -> resource becomes visible
```

### 11.4 Approve resource (Admin)

```
Admin -> POST /api/admin/resources/{resourceId}/approve
Service checks role -> updates status, approved_by, approved_at
Service adds points transaction (optional)
```

## 12. Security model

Roles:
- Guest: unauthenticated
- User: authenticated
- Admin: privileged moderation actions

Rules:
- Public read access for courses, offerings, reviews
- Resources require authentication (MVP)
- Admin required for moderation

Implementation approach:
- Start with Spring Security form login (fast local prototyping)
- Migrate to JWT for frontend React + API later

## 13. Implementation roadmap (near-term)

Next recommended steps:
1. Introduce DTOs for API responses (avoid exposing entities with nested graphs).
2. Add Course search endpoint:
   - query by course name
   - query by lecturer name (later)
   - query by topic keywords (later)
3. Add Resource entity and endpoints (LINK-based).
4. Add Reviews (course reviews) with public read.
5. Add Admin moderation workflow for resources.
6. Add seed data strategy:
   - Dev-only seeding (Java initializer)
   - Optional import scripts (CSV/JSON) for real data later

## 14. Notes for future online deployment

- Separate environments: dev/test/prod
- Secret management for DB passwords (not in git)
- HTTPS termination (reverse proxy)
- File upload pipeline (if added):
  - object storage (S3 compatible)
  - antivirus scanning
  - signed URLs
  - size/type limits
