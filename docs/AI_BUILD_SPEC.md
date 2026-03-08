# HadassahHub – AI Build Specification

This document describes the technical structure and requirements for the HadassahHub system.

It is intended to guide AI-assisted development tools (such as Kiro, Cursor, or similar systems) to generate consistent and maintainable code.

The goal is to maintain a clean architecture while allowing easy extension in the future.

---

# 1. Project Purpose

HadassahHub is a web platform designed for students of Hadassah Academic College.

The platform allows students to:

• Explore courses
• Share academic resources
• Access past exams and study materials
• Read course reviews and recommendations

Future versions may include course planning tools and AI-based recommendations.

---

# 2. Architecture Requirements

The backend must follow a layered architecture.

Required structure:

Controller Layer
Service Layer
Repository Layer
DTO Layer
Entity Layer

Entities must never be returned directly from controllers.

Controllers must return DTO objects.

---

# 3. Technology Stack

Backend

Java
Spring Boot
Spring Security
REST API

Database

PostgreSQL

Infrastructure

Docker
Docker Compose

Authentication (future)

JWT-based authentication

Frontend (future)

React

---

# 4. Database Design Principles

The system must use normalized relational database design.

The following core entities must exist:

Course
CourseOffering
Resource
User
Review

Additional entities may be added in the future.

All entities must use numeric primary keys.

---

# 5. Course Entity

Represents a course in the academic catalog.

Fields:

id
name
description
category
credits
recommendedYear (only for required courses)

Elective courses may leave recommendedYear null.

---

# 6. CourseOffering Entity

Represents when a course is offered.

Fields:

id
course_id
year (Y1, Y2, Y3)
semester (A, B, S)

This allows the same course to be offered in multiple semesters.

---

# 7. Resource Entity

Represents study materials shared by users.

Fields:

id
course_id
user_id
title
url
type
year
approved
created_at

Resource types:

SUMMARY
EXAM
HOMEWORK
SOLUTION
LINK

The year field represents the academic year of the resource (for example: exam from 2023).

---

# 8. Review Entity

Represents course reviews written by students.

Fields:

id
course_id
user_id
rating
difficulty
workload
comment
created_at

Rating values range from 1 to 5.

---

# 9. API Design

The system must expose REST endpoints.

Example endpoints:

Course API

GET /api/courses
GET /api/courses/{id}

Course search

GET /api/courses?search=algorithm

Course offerings

GET /api/offerings
GET /api/offerings?year=Y1
GET /api/offerings?semester=A

Course resources

GET /api/courses/{id}/resources
GET /api/courses/{id}/resources?type=EXAM

Reviews

GET /api/courses/{id}/reviews
POST /api/courses/{id}/reviews

---

# 10. Data Access Rules

Controllers must never access repositories directly.

All database access must go through services.

Services may call repositories.

DTO objects must be used to return data.

---

# 11. Security Rules (Future)

Authentication will be required for resource uploads and reviews.

Only users with college email domains may register.

Allowed domains:

@edu.jmc.ac.il
@edu.hac.ac.il

Authentication will use JWT tokens.

---

# 12. Extensibility Requirements

The architecture must allow easy addition of the following features:

Study planner with credit tracking
Grade statistics
Course recommendation questionnaire
File uploads instead of links
AI-based course recommendations

Future features must not require redesigning the core data model.

---

# 13. Code Quality Requirements

Generated code must follow these principles:

• clear separation of concerns
• small focused services
• DTO usage for API responses
• repository interfaces using Spring Data JPA
• clean and readable code structure

---

# 14. Current Development State

The system already includes:

PostgreSQL database running in Docker
pgAdmin interface
Spring Boot backend
CourseOffering entity
CourseOffering repository
CourseOffering service
CourseOffering controller
DTO layer for offerings

The system successfully returns filtered offerings via API.

Next development steps include:

Course API implementation
Resource system
Review system
Authentication
