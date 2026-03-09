
# HadassahHub – Roadmap

This roadmap describes the development milestones of the HadassahHub platform.
Each milestone ends with a working, testable outcome.

Milestone 0 – Project Setup
- Create repository
- Setup backend (Spring Boot)
- Setup frontend (React)
- Setup Docker environment

Milestone 1 – Local Development Environment
- PostgreSQL via Docker
- Backend connects to database
- Health endpoint: GET /api/health
- React app can call backend

Milestone 2 – Authentication
- POST /api/auth/register
- POST /api/auth/login
- JWT authentication
- Roles: STUDENT / ADMIN

Milestone 3 – Course Catalog
- GET /api/courses
- GET /api/courses/{id}
- Filtering and search
- Course pages in frontend

Milestone 4 – Resource System (Links)
- Students can upload links to resources
- Resource types: SUMMARY, EXAM, HOMEWORK, LINK
- Resource moderation status

Milestone 5 – Admin Moderation
- GET /api/resources/pending
- POST /api/resources/{id}/approve
- POST /api/resources/{id}/reject

Milestone 6 – File Upload System (Current Focus)
- Extend Resource entity to support file uploads
- Store file metadata
- Validate file type and size
- Upload UI in frontend

Milestone 7 – Course Reviews
- Students can review courses
- Ratings and comments

Milestone 8 – Academic Statistics
- Grade statistics endpoint
- Charts in course page

Milestone 9 – Study Planning (Future)
- Course basket
- Credit tracking

Milestone 10 – Portfolio Polish
- README
- Screenshots
- Deployment
