
# HadassahHub – System Architecture

This document describes the architecture of the HadassahHub platform.

High-level goal:
Provide a collaborative academic platform for CS students to share resources and explore courses.

Technology Stack

Backend:
- Java
- Spring Boot
- Spring Security
- Spring Data JPA

Database:
- PostgreSQL

Frontend:
- React
- TypeScript
- React Router
- React Query
- TailwindCSS

Infrastructure:
- Docker / Docker Compose

Architecture Pattern

Controller -> Service -> Repository -> Database

Core Entities

Course
CourseOffering
User
Resource
CourseReview
ResourceRating

Resource Workflow

Student uploads resource
-> Resource status PENDING
-> Admin review
-> APPROVED or REJECTED

Current Feature Set

- JWT authentication
- Course catalog
- Resource sharing system
- Admin moderation
- React frontend

Current Active Development

File Upload System

Resources currently support URL links only.
The next step is adding file upload support.

Future Extensions

- AI course recommendations
- Study planner
- Analytics
- Notifications
